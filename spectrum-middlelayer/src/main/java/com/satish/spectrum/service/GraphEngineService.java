package com.satish.spectrum.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.satish.spectrum.graphEngine.TransactionGraph;
import com.satish.spectrum.graphEngine.TxNode;
import com.satish.spectrum.graphEngine.TxNodeList;
import com.satish.spectrum.vo.ApiTransaction;
import com.satish.spectrum.vo.ApiTxInput;
import com.satish.spectrum.vo.MlUtils;

	@Service
	public class GraphEngineService {

		private final Logger log = LoggerFactory.getLogger(this.getClass());

		 
		@Autowired
		private CacheManager cacheManager;

		@Autowired
		BlockstemService blockstemService;
		
		public TransactionGraph buildGraph(String transactionHash, int depth) throws Exception {
			
			int maxDepth = depth;
			ApiTransaction apiTx = blockstemService.getApiTransaction(transactionHash);
			long minVolume = apiTx.volumeOut / 100; // 1%
			TransactionGraph tg = new TransactionGraph(apiTx);
			boolean aborted = false, deeper = false;
			String logTxt = "buildGraph DONE";

			//int lastAllNodesSize = 0;

			cacheManager = CacheManager.getInstance();
			Cache graphCache = cacheManager.getCache("graphCache");
			Element element = graphCache.get(transactionHash);

			if (element != null && element.getObjectValue() != null) {
				tg = (TransactionGraph) element.getObjectValue();
				logTxt += "(cached up to depth: " + tg.getDepth()+")";
			}

			try{
				if (tg.getDepth() < maxDepth) {
					for (int d = tg.getDepth(); d < maxDepth; d++) {
						TxNodeList nodesToParse = tg.nodesAtDepth(d);
						for (int i = 0; i < nodesToParse.size(); i++) {
							TxNode nTx = nodesToParse.get(i);
							tg.addNode(nTx);
							if (!nTx.cTx.coinbase){
								List<ApiTxInput> nTxInputs = nTx.getRelevantInputs(minVolume * (depth - 1));
								if (nTxInputs.size() == 0 && tg.message == "") tg.message = "no relevant( > " + MlUtils.toBtcString(minVolume, true) + ") transactions found";
								for (ApiTxInput ai : nTxInputs) {
									tg.addLink(ai.previousTransactionHash, nTx.hash, ai);
									ApiTransaction iTx = blockstemService.getApiTransaction(ai.previousTransactionHash);
									if (iTx != null){
										iTx.reducer = (nTx.reducer() * ai.value) / (double) nTx.value;
										tg.addNode(new TxNode(iTx, d + 1));
										iTx.getOutput(ai.previousIndex).nextTransactionHash = iTx.hash;
										deeper = true;
										if (tg.message != "") tg.message = ""; // reset msg;
									}
								}
							}
						}
					}
					tg.updateAll(deeper);
					graphCache.put(new Element(transactionHash, tg));
				} else {
					tg.message = "depth (" + maxDepth + ") reached";
				}
			} catch (Throwable e){
				e.printStackTrace();
				logTxt += " (FAILED)";
			}
			
			if (aborted) {
				logTxt += " (ABORTED)";
			}
			logTxt += " d: " + tg.getDepth() + " n: " + tg.numNodes() + " l: " + tg.numLinks();
			logTxt += " <" + tg.rootHash +  ">";
			log.info(logTxt);
			
			return tg;
		}
	}
