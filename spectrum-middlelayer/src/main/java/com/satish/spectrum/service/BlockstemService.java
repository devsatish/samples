package com.satish.spectrum.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.satish.spectrum.vo.ApiResponse;
import com.satish.spectrum.vo.ApiTransaction;
import com.satish.spectrum.vo.FilterGroup;
import com.satish.spectrum.vo.Sort;
import com.satish.spectrum.vo.TxScoresInfo;
import com.satish.spectrum.vo.TxVolumeInfo;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BlockstemService {

	static final String API_PREFIX = "http://satish.io/api/blockstem/";
	static final String API_POSTFIX = "?publicKey=1PHqohMVhH8GHY66TZqKjpmhzXFsKwLcXo&nonce=0";
	static final String API_TX = "transaction";
	static final String API_LABEL = "label/addresses";
	static final String API_LABELTYPE = "label/types";
	static final int RESULTS_MAX = 100;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CacheManager cacheManager;

	public BlockstemService() {
	}

	public static String getJsonRequest_filterArray(int page, int resultPerPage, List<FilterGroup> filters, Sort sort) {
		resultPerPage = Math.min(RESULTS_MAX, resultPerPage);
		String json = "{\"page\": " + page + ",";
		json += "\"resultsPerPage\":" + resultPerPage;

		if (filters != null) {
			json += ", \"filterGroup\": {";
			json += " \"logicalOperator\": \"AND\"";
			json += ", \"filters\":[";
			int i = 1;
			for (FilterGroup filter : filters) {
				json += filter.asJson();
				if (i < filters.size()) {
					json += ",";
				}
				i++;
			}
			json += "]";
			json += "}";
		}

		if (sort != null)
			json += ", \"sorts\":[" + sort.asJson() + "]";
		json += "}";
		return json;
	}

	public static String getJsonRequest(int page, int resultPerPage, FilterGroup filter, Sort sort) {
		resultPerPage = Math.min(RESULTS_MAX, resultPerPage);
		String json = "{\"page\": " + page + ",";
		json += "\"resultsPerPage\":" + resultPerPage;
		if (filter != null)
			json += ", \"filterGroup\":" + filter.asJson();
		if (sort != null)
			json += ", \"sorts\":[" + sort.asJson() + "]";
		json += "}";
		return json;
	}

	public static String getJsonRequestWithProjections(int page, int resultPerPage, FilterGroup filter, Sort sort, List<String> prjs) {
		resultPerPage = Math.min(RESULTS_MAX, resultPerPage);
		String json = "{\"page\": " + page;
		json += ", \"resultsPerPage\":" + resultPerPage;
		if (prjs != null) {
			json += ", \"projections\":[\"";
			json += String.join("\",\"", prjs);
			json += "\"]";
		}
		if (filter != null)
			json += ", \"filterGroup\":" + filter.asJson();
		if (sort != null)
			json += ", \"sorts\":[" + sort.asJson() + "]";
		json += "}";
		return json;
	}

	public static String getJsonOpRequestWithProjections(int page, int resultPerPage, String operator, List<FilterGroup> filters, Sort sort, List<String> prjs) {
		resultPerPage = Math.min(RESULTS_MAX, resultPerPage);
		String json = "{\"page\": " + page;
		json += ", \"resultsPerPage\":" + resultPerPage;
		if (prjs != null) {
			json += ", \"projections\":[\"";
			json += String.join("\",\"", prjs);
			json += "\"]";
		}
		if (filters != null) {
			json += ", \"filterGroup\":{";
			json += "\"logicalOperator\":\"" + operator + "\",";
			json += "\"filters\":[";
			List<String> fs = new ArrayList<String>();
			for (FilterGroup filter : filters) {
				fs.add(filter.asJson());
			}
			json += String.join(",", fs) + "]}";
		}
		if (sort != null)
			json += ", \"sorts\":[" + sort.asJson() + "]";
		json += "}";
		return json;
	}

	public static String getTxJsonRequest(int page, int resultPerPage, FilterGroup filter, Sort sort, boolean withClusters, boolean withScores) {
		resultPerPage = Math.min(RESULTS_MAX, resultPerPage);
		String json = "{\"page\": " + page + ",";
		json += "\"resultsPerPage\":" + resultPerPage + ",";
		json += "\"fetchScores\":" + (withScores ? 1 : 0) + ",";
		json += "\"fetchClusterIds\":" + withClusters;

		if (filter != null)
			json += ", \"filterGroup\":" + filter.asJson();
		if (sort != null)
			json += ", \"sorts\":[" + sort.asJson() + "]";

		json += "}";
		return json;
	}

	public static String getTxJsonRequest(int page, int resultPerPage, FilterGroup filter, Sort sort, boolean withScores, List<String> prjs) {
		resultPerPage = Math.min(RESULTS_MAX, resultPerPage);
		String json = "{\"page\": " + page + ",";
		json += "\"resultsPerPage\":" + resultPerPage + ",";
		json += "\"fetchScores\":" + (withScores ? 1 : 0) + ",";
		if (prjs != null) {
			json += "\"projections\":[\"";
			json += String.join("\",\"", prjs);
			json += "\"]";
		}
		if (filter != null)
			json += ", \"filterGroup\":" + filter.asJson();
		if (sort != null)
			json += ", \"sorts\":[" + sort.asJson() + "]";

		json += "}";
		return json;
	}

	private String processGzipResponse(byte[] responseBytes) {
		GZIPInputStream gzipInputStream = null;

		try {
			gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(responseBytes));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		java.io.ByteArrayOutputStream byteout = new java.io.ByteArrayOutputStream();

		int res = 0;
		byte buf[] = new byte[1024];
		while (res >= 0) {
			try {
				res = gzipInputStream.read(buf, 0, buf.length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (res > 0) {
				byteout.write(buf, 0, res);
			}
		}
		byte uncompressed[] = byteout.toByteArray();

		String response = new String(uncompressed);

		return response;

	}

	private ApiResponse<?> getApiResults(String api, String body, TypeReference<?> responseType) {
		SimpleClientHttpRequestFactory schrf = new SimpleClientHttpRequestFactory();
		schrf.setConnectTimeout(180000); // 3 mts timeout

		RestTemplate restTemplate = new RestTemplate(schrf);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("ACCEPT-ENCODING", "GZIP");
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		HttpEntity<String> entity = new HttpEntity<String>(body, headers);
		String url = API_PREFIX + api + API_POSTFIX;

		ResponseEntity<byte[]> responseBytesEntity = null;

		try {
			responseBytesEntity = restTemplate.postForEntity(url, entity, byte[].class);
		} catch (RestClientException ex) {
			logger.info("Error connecting to Blockstem " + ex.getMessage());
		}

		HttpHeaders responseHeaders = null;

		String response = null;
		String ce = null;

		if (responseBytesEntity != null) {
			responseHeaders = responseBytesEntity.getHeaders();
			if (responseHeaders.get("Content-Encoding") != null && responseHeaders.get("Content-Encoding").size() > 0) {
				ce = responseHeaders.get("Content-Encoding").get(0);
			}
			if (ce != null && ce.equalsIgnoreCase("gzip")) {
				response = processGzipResponse(responseBytesEntity.getBody());

			} else {
				response = new String(responseBytesEntity.getBody());
			}
		}

		ApiResponse<?> apiResponse = null;
		try {

			apiResponse = mapper.readValue(response.toString(), responseType);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return apiResponse;
	}

	public ApiTransaction getApiTransaction(String transactionHash) {
		ApiTransaction tx = null;
		String ls = "getApiTransaction<" + transactionHash + ">";

		cacheManager = CacheManager.getInstance();
		Cache transactionCache = cacheManager.getCache("transactionCache");
		Element element = transactionCache.get(transactionHash);

		if (element != null) {
			tx = (ApiTransaction) element.getObjectValue();
			ls += "(cache)";
		} else {
			FilterGroup filter = new FilterGroup("hash", "EQUAL", transactionHash);
			TypeReference<?> respType = new TypeReference<ApiResponse<ApiTransaction>>() {
			};
			String body = getTxJsonRequest(1, 1, filter, null, true, true);
			ArrayList<?> r = getApiResults(API_TX, body, respType).results;
			if (r.size() == 0) {
				ls += " NOT FOUND in blockstem DB";
			} else {
				tx = (ApiTransaction) r.get(0);
				transactionCache.put(new Element(transactionHash, tx));
			}
		}
		logger.info(ls);
		return tx;
	}

	public ApiTransaction getNextTxFromPrevTxandInputAddress(String inputPreviousTransactionHash, String inputAddress) {
		FilterGroup filter1 = new FilterGroup("inputPreviousTransactionHash", "EQUAL", inputPreviousTransactionHash);
		FilterGroup filter2 = new FilterGroup("inputAddresses", "EQUAL", inputAddress);

		TypeReference<?> respType = new TypeReference<ApiResponse<ApiTransaction>>() {
		};

		ArrayList<FilterGroup> filters = new ArrayList<FilterGroup>();
		filters.add(filter1);
		filters.add(filter2);

		String body = getJsonRequest_filterArray(1, 50, filters, null);
		@SuppressWarnings("unchecked")
		List<ApiTransaction> results = (List<ApiTransaction>) getApiResults(API_TX, body, respType).results;
		ApiTransaction tx = null;
		if (results != null && results.size() > 0) {
			tx = (ApiTransaction) getApiResults(API_TX, body, respType).results.get(0);
			logger.info("getNextTxFromPrevTxandIndex[" + tx.hash + "]");
		} else {
			logger.info("getNextTxFromPrevTxandIndex - No results");
		}

		return tx;

	}

	public Future<ArrayList<TxScoresInfo>> getLatestTxScores(String addressHash) {
		/*
		 * "page":1, "resultsPerPage":100, "projections": ["hash"],
		 * "filterGroup":{ "field":"outputAddresses",
		 * "comparisonOperator":"EQUAL", "fieldValue":addressHash }, "sorts":[{
		 * "sortBy":"blockHeight", "sortDirection":"DESC"}]
		 */

		FilterGroup filter = new FilterGroup("outputAddresses", "EQUAL", addressHash);
		Sort sort = new Sort("volumeOut", "DESC");
		TypeReference<?> respType = new TypeReference<ApiResponse<TxScoresInfo>>() {
		};

		List<String> prjs = new ArrayList<String>();
		prjs.add("hash");
		String body = getTxJsonRequest(1, 100, filter, sort, true, prjs);
		@SuppressWarnings("unchecked")
		ArrayList<TxScoresInfo> txs = (ArrayList<TxScoresInfo>) getApiResults(API_TX, body, respType).results;
		logger.info("getTop50txsOfAddress[" + txs.size() + "]");
		return new AsyncResult<ArrayList<TxScoresInfo>>(txs);
	}

	public Future<ArrayList<TxVolumeInfo>> getTopTxByVolume(String addressHash, boolean sent) {
		/*
		 * "page":1, "resultsPerPage":100, "projections": ["volumeOut"],
		 * "filterGroup":{ "field":"outputAddresses",
		 * "comparisonOperator":"EQUAL", "fieldValue":addressHash }, "sorts":[{
		 * "sortBy":"volumeOut", "sortDirection":"DESC"}]
		 */

		FilterGroup filter = new FilterGroup(sent ? "inputAddresses" : "outputAddresses", "EQUAL", addressHash);
		Sort sort = new Sort("volumeOut", "DESC");
		TypeReference<?> respType = new TypeReference<ApiResponse<TxVolumeInfo>>() {
		};
		List<String> prjs = new ArrayList<String>();
		prjs.add("volumeOut");
		String body = getJsonRequestWithProjections(1, 100, filter, sort, prjs);
		// System.out.println(body);
		@SuppressWarnings("unchecked")
		ArrayList<TxVolumeInfo> txs = (ArrayList<TxVolumeInfo>) getApiResults(API_TX, body, respType).results;
		logger.info("getTopTxByVolume[" + txs.size() + "]");
		return new AsyncResult<ArrayList<TxVolumeInfo>>(txs);
	}

	public Future<String> getClusterOfAddress(String addressHash) {
		// "page":1,
		// "resultsPerPage":1,
		// "fetchClusterIds": true,
		// "filterGroup":{ "field":"outputAddresses",
		// "comparisonOperator":"EQUAL",
		// "fieldValue":"1a8LDh3qtCdMFAgRXzMrdvB8w1EG4h1Xi"},
		// "sorts":[{"sortBy":"numOutputs","sortDirection":"ASC"}]}
		String cls = "";
		TypeReference<?> respType = new TypeReference<ApiResponse<ApiTransaction>>() {
		};
		FilterGroup filter = new FilterGroup("outputAddresses", "EQUAL", addressHash);
		Sort sort = new Sort("numOutputs", "ASC");
		String body = getTxJsonRequest(1, 1, filter, sort, true, false);
		@SuppressWarnings("unchecked")
		ArrayList<ApiTransaction> txs = (ArrayList<ApiTransaction>) getApiResults(API_TX, body, respType).results;
		if (txs.size() > 0) {
			cls = txs.get(0).getOutputsClusterIdOf(addressHash);
		}
		return new AsyncResult<String>(cls);
	}

	public ArrayList<TxVolumeInfo> getTxsByInOut(String inAddress, String outAddress) {
		/*
		 * "page":1, "resultsPerPage":200, "projections": ["volumeOut"],
		 * "filterGroup":{ "filterGroup":{ "logicalOperator":"AND", "filters":[{
		 * "field":"inputAddresses", "comparisonOperator":"EQUAL", "fieldValue":
		 * <inAddress> },{ "field":"outputAddresses",
		 * "comparisonOperator":"EQUAL", "fieldValue":<outAddress> }]},
		 * "sorts":[{ "sortBy":"volumeOut", "sortDirection":"DESC"}]
		 */

		List<FilterGroup> filters = new ArrayList<FilterGroup>();
		if (inAddress != null)
			filters.add(new FilterGroup("inputAddresses", "EQUAL", inAddress));
		if (outAddress != null)
			filters.add(new FilterGroup("outputAddresses", "EQUAL", outAddress));
		Sort sort = new Sort("volumeOut", "DESC");
		TypeReference<?> respType = new TypeReference<ApiResponse<TxVolumeInfo>>() {
		};
		List<String> prjs = new ArrayList<String>();
		prjs.add("volumeOut");
		String body = getJsonOpRequestWithProjections(1, 200, "AND", filters, sort, prjs);
		// System.out.println(body);
		@SuppressWarnings("unchecked")
		ArrayList<TxVolumeInfo> txs = (ArrayList<TxVolumeInfo>) getApiResults(API_TX, body, respType).results;
		logger.info("getTxsByInOut[" + txs.size() + "]");
		return txs;
	}

}