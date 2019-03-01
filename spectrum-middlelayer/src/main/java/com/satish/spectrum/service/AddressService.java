package com.satish.spectrum.service;

import java.util.ArrayList;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.satish.spectrum.db.Label;
import com.satish.spectrum.vo.ApxAddressInfo;
import com.satish.spectrum.vo.ApxTxInfo;
import com.satish.spectrum.vo.ClientAddress;
import com.satish.spectrum.vo.TxScoresInfo;
import com.satish.spectrum.vo.TxVolumeInfo;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

	@Service
	public class AddressService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	ExtraService extraService;
	
	@Autowired
	BlockstemService blockstemService;
	
	@Autowired
	DbService dbService;
	
//		public ClientAddress getAddress(String hash) {
//			
//			ClientAddress a = null;
//			String ls = "getAddress DONE";
//			
//			try{
//				cacheManager = CacheManager.getInstance();
//				Cache aCache = cacheManager.getCache("addressCache");
//				Element element = aCache.get(hash);
//			
//				if (element != null && element.getObjectValue() != null) {
//					ls += " (cache)";
//					a = (ClientAddress) element.getObjectValue();
//				} else {
//					a = new ClientAddress(hash);
//					a.updateLabel(dbService.getLabelOfAddress(hash));
//					a.updateTop100(blockstemService.getTopTxByVolume(hash, false), false); 
//					a.updateTop100(blockstemService.getTopTxByVolume(hash, true), true);
//					aCache.put(new Element(hash, a));
//				}
//				
//				ls += " " + a.toString();
//				log.info(ls);
//				
//			} catch (Exception e){
//				ls += " FAILED: " + e.getMessage(); 
//				log.error(ls);
//				e.printStackTrace();
//			}
//			
//			return a;
//		}
	
	public ClientAddress getAddress(String hash) {
		
		ClientAddress a = null;
		String ls = "getAddress(w/Info) DONE";
		
		try{
			cacheManager = CacheManager.getInstance();
			Cache aCache = cacheManager.getCache("addressCache");
			Element element = aCache.get(hash);
		
			if (element != null && element.getObjectValue() != null) {
				ls += " (cache)";
				a = (ClientAddress) element.getObjectValue();
			} else {
				a = new ClientAddress(hash);
				
				Future<ApxAddressInfo> info =  extraService.getAddressInfo(hash);
				Future<Label> lbl = dbService.getLabelOfAddressAsync(hash);
				Future<ArrayList<TxVolumeInfo>> recvd =  blockstemService.getTopTxByVolume(hash, false);
				Future<ArrayList<TxVolumeInfo>> sent =  blockstemService.getTopTxByVolume(hash, true);
				Future<ArrayList<TxScoresInfo>> scores = blockstemService.getLatestTxScores(hash);
				// Future<String> cls = blockstemService.getClusterOfAddress(hash);
				
				while(!lbl.isDone() && !info.isDone() && !recvd.isDone() && !sent.isDone()  && !scores.isDone()) {
					Thread.sleep(10);
				}
				
				a.updateLabel(lbl.get());
				a.updateInfo(info.get());
				a.updateTop100(recvd.get(), false);
				a.updateTop100(sent.get(), true);
				a.updateRisk(scores.get());
				// a.clusterId = cls.get();

				aCache.put(new Element(hash, a));
			}
			
			ls += " " + a.toString();
			log.info(ls);
			
		} catch (Exception e){
			ls += " FAILED: " + e.getMessage(); 
			log.error(ls);
			e.printStackTrace();
		}
		
		return a;
	}
}
