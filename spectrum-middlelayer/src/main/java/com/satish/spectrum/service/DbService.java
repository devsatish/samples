package com.satish.spectrum.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.satish.spectrum.db.AddressLabel;
import com.satish.spectrum.db.Label;

@Service
public class DbService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public Future<Label> getLabelOfAddressAsync(String hash){
	    Label lbl = new Label();
	    List<String> l = new ArrayList<String>();
		l.add(hash);
		List<Label> labels = getLabelsForAddressHashes(l);
		if (labels != null && labels.size() > 0) {
			lbl = labels.get(0);
		}
		// log.info("hash "+hash+" label "+lbl);
		return new AsyncResult<Label>(lbl);
	}
	
	public Label getLabelOfAddress(String hash){
	    Label lbl = new Label();
	    List<String> l = new ArrayList<String>();
		l.add(hash);
		List<Label> labels = getLabelsForAddressHashes(l);
		if (labels != null && labels.size() > 0) {
			lbl = labels.get(0);
		}
		// log.info("hash "+hash+" label "+lbl);
		return lbl;
	}
	
	public List<Label> getLabelsForAddressHashes(List<String> hashList) {

		if (hashList == null || hashList.size() == 0) { return null; }
		
		List<byte[]> addrList = new ArrayList<byte[]>();
		List<Label> labels = null;
		int cnt = 0;
		try{
			for(String hash : hashList){
				addrList.add(AddressLabel.get20bytesFromAddress(hash));
			}
			String queryLabelsTable = "SELECT * FROM labels WHERE address IN ( :addrList )";
			
			NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
			Map namedParameters = Collections.singletonMap("addrList", addrList);
			labels = namedParameterJdbcTemplate.query(queryLabelsTable, namedParameters,new RowMapper<Label>(){
				@Override
				public Label mapRow(ResultSet rs, int rowNum) throws SQLException {
					Label label = new Label();
					label.address = rs.getBytes("address");
					label.cluster_id = rs.getBytes("cluster_id");
					label.label = rs.getString("label");
					label.type = rs.getString("type");
					label.url = rs.getString("url");
					return label;
				}
				
			});
			
		} catch(Throwable e){
			e.printStackTrace();
		}
		
		return labels;
	}

}
