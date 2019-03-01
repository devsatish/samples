package com.coinalytics.spectrum.database;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.bind.DatatypeConverter;

public class DbProcessor {
	
	final static String SQLlocal = "";
	final static String SQLremote = "";
    
	static int          DBGlimit = 50000;
	static Connection 	connection = null;
	static boolean 		local = true; 		// false: remote, connects MySQL on the could
	
	// COMMAND-LINE OPTIONs 
	// -remote :run locally with MySQL in the cloud
	// no options: run locally, expect MySQL server on localhost
	
	public static void main(String[] argv) {

		// parse arguments
		for (String option : argv) {
			if (option.contains("-remote")) {
				local = false;
			}
		}

		System.out.println("--- SPECTRUM LABEL IMPORTER (MySQL server is " + (local ? "this machine)" : "REMOTE)") + " ---");
		if (DBGlimit > 0){
			System.out.println("----- (DEBUG) LIMIT: " + DBGlimit + " -----");
		}
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("missing jdbc connection driver");
			e.printStackTrace();
			return;
		}

		System.out.println("MySQL JDBC Driver registered");
		
		try {
			connection = DriverManager.getConnection(local ? SQLlocal : SQLremote, "spectrum-dba", "$pecTRUM$dba");
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}

		if (connection == null) {
			System.out.println("Failed to make connection");
			return;
		}
		
		System.out.println("Connection OK");
		try{
			ResultSet crs, irs, lrs = getAllAddressesOfLabelsTable();
			
			System.out.println("A) iterating over all addresses with labels...");
			
			int cnt = 0, icnt = 0; // only for testing purpose
			String s;    // only for testing purpose
			
			Blob adr, cls;
			String lbl, typ, url;
			
			while(lrs.next()){
				adr = lrs.getBlob("address");
				lbl = lrs.getString("label");
				typ = lrs.getString("type");
				url = lrs.getString("label");
				cls = findClusterOfAddress(adr);
				
				s = getLog(++cnt, adr, cls, lbl, typ, url);
				if (cls != null) {
					icnt = 0;
					irs = getAllAddressessOfCluster(cls);
					while(irs.next()){
						icnt++;
						// TOOD
					}
					s += " inner loop done for [" + icnt + "] addresses of same cluster...";
					System.out.println(s);
				} else {
					s += "\tno cluster: add directly to <big_labels>";
					// TODO
					if (DBGlimit > 1000){
						if (cnt % 250 == 0 || cnt == DBGlimit || cnt == 1) {
							System.out.println(s); 
						} else if (cnt % 30 == 0) {
							System.out.print("."); 
						}
					} else if (DBGlimit > 10){
						if (cnt % 10 == 0){
							System.out.println(s); 
						} else {
							System.out.print("."); 
						}
					} else {
						System.out.println(s); 
					}
				}
			}
			
			// DBGlimit = 100; // for test
			crs = getAllAddressesOfClustersTable();
			System.out.println("B) iterating over all addresses with clusters...");
			cnt = 0;
			while(crs.next()){
				s = "\t [" + (++cnt) + "] " + blob2string(crs.getBlob("address"));
				s += " - " + blob2string(crs.getBlob("cluster_id"));
				if (DBGlimit > 1000){
					if (cnt % 1000 == 0 || cnt == DBGlimit || cnt == 1) {
						System.out.println(s);
					} else if (cnt % 500 == 0) {
						System.out.print("|"); 
					} else if (cnt % 250 == 0) {
						System.out.print("-"); 
					} else if (cnt % 25 == 0) {
						System.out.print("."); 
					}
				} else if (DBGlimit > 10){
					if (cnt % 10 == 0){
						System.out.println(s); 
					} else {
						System.out.print("."); 
					}
				} else {
					System.out.println(s); 
				}
			}
			
			System.out.println("C) done.");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
		if (connection != null) { 
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static ResultSet getAllAddressessOfCluster(Blob toFind) throws Exception {
		if (connection == null) { throw new Exception("getClusterForAddress: no connection"); } // stop here
		
		PreparedStatement stmt = connection.prepareStatement("SELECT DISTINCT address, cluster_id FROM address_cluster_ids WHERE cluster = ?");
		stmt.setBlob(1, toFind);
		// System.out.println("    (f) finding all addresses of cluster: " + blob2string(toFind));
		return stmt.executeQuery();
	}
	
	static Blob findClusterOfAddress(Blob toFind) throws Exception{
		if (connection == null) { throw new Exception("getClusterForAddress: no connection"); } // stop here
		
		Blob cluster = null;
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM address_cluster_ids WHERE address = ?");
		stmt.setBlob(1, toFind);
		// System.out.println("    (f) finding the cluster of address: " + blob2string(toFind));
		ResultSet rs = stmt.executeQuery();
		while (rs.next()){
			if (rs.getBlob("address") == toFind){
				cluster = rs.getBlob("cluster_id");
				break;
			}
		}
		// System.out.println("    (f) finding the cluster of address - FOUND: " + blob2string(cluster));
		return cluster;
	}
	
	static ResultSet getAllAddressesOfLabelsTable(long from, long to) throws SQLException{
		Statement stmt = connection.createStatement();
		String sql;
		sql = "SELECT DISTINCT address, label, type, url FROM address_labels" + getSqlLimit();
		System.out.println("    (f) getting " + (to - from) + " addresses of Labels table...");
		return stmt.executeQuery(sql);
	}
	
	private static String getSqlLimit() {
		return DBGlimit > 0 ? (" LIMIT " + DBGlimit) : "";
	}

	static ResultSet getAllAddressesOfLabelsTable() throws SQLException{
		Statement stmt = connection.createStatement();
		String sql;
		sql = "SELECT DISTINCT address, label, type, url FROM address_labels" + getSqlLimit();
		System.out.println("    (f) getting ALL addresses of LABELS table...");
		return stmt.executeQuery(sql);
	}
	
	static ResultSet getAllAddressesOfClustersTable() throws SQLException{
		Statement stmt = connection.createStatement();
		String sql;
		sql = "SELECT address, cluster_id FROM address_cluster_ids" + getSqlLimit();
		System.out.println("    (f) getting ALL addresses of CLUSTERS table...");
		return stmt.executeQuery(sql);
	}
	
	static String blob2string(Blob b){
		// TODO: use Base58 
		byte[] bytes = {0x00};
		try {
			if (b != null){
				bytes = b.getBytes(1, (int) b.length());
			} else {
				return " -(empty)- ";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return DatatypeConverter.printHexBinary(bytes);
	}
	
	static String blob2mini(Blob b){
		try {
			if (b != null){
				return ".." + DatatypeConverter.printHexBinary(b.getBytes((int) b.length()-3,3));
			} else {
				return "- - - - -";
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static String getLog(int i, Blob a, Blob c, String l, String t, String u){
		String s = "[" + i + "] " + blob2mini(a) + " ";
		if (c != null){
			s += "| " + blob2mini(c) + " ";
		}
		s += " - " + (l != null ? l : " ");
		s += " (" + (t != null ? t : "") + ") ";
		if (u != null && u.length() > 0){
			s += "WWW";
		} else {
			s += "---";
		}
		return s;
	}
}
