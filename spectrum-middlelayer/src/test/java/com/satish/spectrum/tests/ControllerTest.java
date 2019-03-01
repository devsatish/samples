package com.satish.spectrum.tests;

import static org.junit.Assert.assertEquals;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.satish.spectrum.SpectrumServiceApplication;
import com.satish.spectrum.controller.BigQueryController;
import com.satish.spectrum.controller.GraphController;
import com.satish.spectrum.controller.TransactionController;
import com.satish.spectrum.service.AddressService;
import com.satish.spectrum.service.BlockstemService;
import com.satish.spectrum.service.DbService;
import com.satish.spectrum.service.GraphEngineService;
import com.satish.spectrum.vo.TxVolumeInfo;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.QueryRequest;
import com.google.api.services.bigquery.model.QueryResponse;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(SpectrumServiceApplication.class)
public class ControllerTest {
	static final String TX_WITH_LABELS = "b779fbe04079bbefd6ff661bde76497f76ea7911be07f787de828aae745e99d3", TX_WITH_MANY_INPUTS = "c7a7111ba174cd59c28bab8f3cdb6bfab094ee4eb3e34711d1c68d9b11033615", TX_WITH_MANY_OUTPUTS = "1fb8ed47e47bd6583f038ed709f2765a35a5717b66114fcccc914e360a339466",
			TX_SPECTRUM = "ae01eeaa69714b1c178eeba6d4d90793666b8937d460685be19bfbc83e743091", TX_EMPTY_GRAPH = "682cf6c8d4314a620199ab52e61bc1a0261ec7acf887e0b97720ab18d72922ff", ADDRESS_TIM_DRAPER = "1a8LDh3qtCdMFAgRXzMrdvB8w1EG4h1Xi", ADDRESS_TOFIX = "1a8LDh3qtCdMFAgRXzMrdvB8w1EG4h1Xi",
			ADDRESS_EMPTY = "3Hs6BYdun3t2A7eteDkBwnFN1rQX53rSYQ", REPOKEY_LABELTYPES = "labelTypes", REPOKEY_LABELSOFTYPES = "labelsOfType";

	static final String[] TXS = { TX_SPECTRUM, TX_WITH_MANY_INPUTS, TX_WITH_MANY_OUTPUTS, TX_WITH_LABELS };

	static final String[] L_TXS = { TX_WITH_LABELS };

	@SuppressWarnings("rawtypes")
	static Map REPO = new HashMap();

	@Autowired
	TransactionController apiController;

	@Autowired
	BigQueryController bigQueryController;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	DbService dbService;

	@Autowired
	GraphController ge;

	@Autowired
	GraphEngineService graphEngineService;

	@Autowired
	BlockstemService blockstemService;

	@Autowired
	AddressService addressService;

	public static Bigquery createAuthorizedClient() throws IOException {
		// Create the credential
		HttpTransport transport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		GoogleCredential credential = GoogleCredential.getApplicationDefault(transport, jsonFactory);
		if (credential.createScopedRequired()) {
			credential = credential.createScoped(BigqueryScopes.all());
		}

		return new Bigquery.Builder(transport, jsonFactory, credential).setApplicationName("Bigquery Samples").build();
	}

	private static List<TableRow> executeQuery(String querySql, Bigquery bigquery, String projectId) throws IOException {
		QueryResponse query = bigquery.jobs().query(projectId, new QueryRequest().setQuery(querySql)).execute();
		GetQueryResultsResponse queryResult = bigquery.jobs().getQueryResults(query.getJobReference().getProjectId(), query.getJobReference().getJobId()).execute();
		return queryResult.getRows();
	}

	private static void printResults(List<TableRow> rows) {
		System.out.print("\nQuery Results:\n------------\n");
		for (TableRow row : rows) {
			for (TableCell field : row.getF()) {
				System.out.printf("%-50s", field.getV());
			}
			System.out.println();
		}
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	

	@Ignore
	@Test
	public void testTxByInOut() {
		String i = "14Ee3Ln6dzrEu75ojoweMmHLqwBzLiN4s3", o = "154FiiWA5NGioVouv9HTx9CSEAAawXFpbZ";
		List<TxVolumeInfo> txs = blockstemService.getTxsByInOut(i, o);
		assertEquals("got txs", txs.size(), 7);
	}
	
	@Ignore
	@Test
	public void _bigQueryControllerTest() throws IOException {
		long start = System.currentTimeMillis();
		String address = bigQueryController.addressForHash("033647d819e9ea4f4faef84bf1132e1f08947f29");
		long end = System.currentTimeMillis();
		System.out.println("Address from BigQuery "+address +"  in time "+TimeUnit.MILLISECONDS.toSeconds(end-start));

	}
	
	
	
	
	@Test
	public void _BQQueryTest() throws IOException {
		String projectId = "spectrum-satish";
		Bigquery bigquery = createAuthorizedClient();
		
		long start = System.currentTimeMillis();
		
		List<TableRow> rows = executeQuery("SELECT * FROM [skry-ml.skry_bitcoin.transactionsNew] LIMIT 1000", bigquery, projectId);
		long end = System.currentTimeMillis();
		System.out.println("In time "+TimeUnit.MILLISECONDS.toSeconds(end-start));
		
		//printResults(rows);

	}

	@Test
	public void fbTokenValidate() {
		String token = "";
		String secret = "";

		try {
			Claims claims = Jwts.parser().setSigningKey(secret.getBytes("UTF-8")).parseClaimsJws(token).getBody();

			Date issuedDate = claims.getIssuedAt();
			long diff = new Date().getTime() - issuedDate.getTime();
			long hrs = TimeUnit.MILLISECONDS.toHours(diff);

			if (issuedDate.before(new Date())) {
				System.out.println("before");
			} else {
				System.out.println("after");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
