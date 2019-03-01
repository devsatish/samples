package com.coinalytics.spectrum.database;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DBStressTest {
	static Map<String, String[]> labelMap = new HashMap<String, String[]>();
	static Map<Blob, BigLabelRow> bigLabelInsertMap = new HashMap<Blob, BigLabelRow>();

	static Blob emptyBlob;
	static int DBGlimit = 0; //0  - default
	static long MAX_BATCH_LIMIT = 10000; //10000
	static long CHUNK_SIZE = 1000; //10000
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	final static String dbString = "jdbc:mysql://104.196.8.3:3306/spectrum";
	static Connection connection;
	
	static String info;
	static {
		try {
			connection = DriverManager.getConnection(dbString,"","")
			connection.setAutoCommit(false);
			emptyBlob = connection.createBlob();
			emptyBlob.setBytes(1, "".getBytes());
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	private ResultSet getRangeOfAddressesOfCluster(Blob toFind, long from) throws Exception {
		if (connection == null) {
			throw new Exception("getClusterForAddress: no connection");
		} // stop here

		PreparedStatement stmt = connection.prepareStatement("SELECT DISTINCT address, cluster_id FROM address_cluster_ids WHERE cluster_id = ? LIMIT ?, ?");
		stmt.setBlob(1, toFind);
		stmt.setLong(2,  from);
		stmt.setLong(3,  CHUNK_SIZE);
		
		return stmt.executeQuery();
	}
	public Blob findClusterOfAddress(Blob toFind) throws Exception {
		if (connection == null) {
			throw new Exception("getClusterForAddress: no connection");
		} // stop here

		Blob cluster = null;
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM address_cluster_ids WHERE address = ?");
		stmt.setBlob(1, toFind);
		// System.out.println("    (f) finding the cluster of address: " +
		// blob2string(toFind));
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			if (rs.getBlob("address") == toFind) {
				cluster = rs.getBlob("cluster_id");
				break;
			}
		}
		// System.out.println("    (f) finding the cluster of address - FOUND: "
		// + blob2string(cluster));
		return cluster;
	}
	public ResultSet addressLabelsTableRows() throws Exception {
		Statement stmt = connection.createStatement();
		String sql;
		sql = "select distinct address, label, type, url from address_labels " + getSqlLimit();
		System.out.println(" Retrieveing addresses from labels table");
		return stmt.executeQuery(sql);
	}
	
	private static String getSqlLimit() {
		return DBGlimit > 0 ? (" LIMIT " + DBGlimit) : "";
	}
	
	public void startProcessing() throws Exception {
		ResultSet crs, irs = null, xlrs =null, lrs = addressLabelsTableRows();

		Blob adr = null, cls = null, iaddr = null;
		byte[] adrbytes;
		String lbl, typ, url;
		
		while (lrs.next()) {
			adrbytes = lrs.getBytes("address");
			lbl = lrs.getString("label");
			typ = lrs.getString("type");
			url = lrs.getString("label");
			cls = findClusterOfAddress(adr);

			labelMap.put(bytesToHex(adrbytes), new String[] { lbl, typ, url });
			System.out.println(""+ labelMap.size());	
		}
		
		System.out.println("Total labelMap Size " + labelMap.size());
		
		
		
		
		
	}

}
