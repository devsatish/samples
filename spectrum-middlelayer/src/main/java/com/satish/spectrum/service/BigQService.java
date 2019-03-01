package com.satish.spectrum.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

@Service
public class BigQService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

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

	

	public String getAddress(String hash160) throws IOException {
		String projectId = "spectrum-satish";
		Bigquery bigquery = createAuthorizedClient();
		List<TableRow> rows = executeQuery("SELECT hash160, address FROM [bigquerydemo.hashnaddress] where hash160='" + hash160 + "';", bigquery, projectId);
		if (rows == null || rows.size() == 0)
			return null;
		TableRow row = rows.get(0);
		TableCell a = row.getF().get(1);
		return (String) a.getV();
	}

}
