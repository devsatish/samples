package com.coinalytics.spectrum.database;

import java.lang.instrument.Instrumentation;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.bind.DatatypeConverter;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;




public class LabelImporter {
	static Map<Blob, String[]> labelMap = new HashMap<Blob, String[]>();
    Map<Blob, BigLabelRow> bigLabelInsertMap = new WeakHashMap<Blob, BigLabelRow>();

	static Blob emptyBlob;
	static int DBGlimit = 0; //0  - default
	static long MAX_BATCH_LIMIT = 1000; //10000
	static long CHUNK_SIZE = 1000; //10000 -

	
	final static String dbString = "";
	static Connection connection;
	
	static String info;
	static {
		try {
			connection = DriverManager.getConnection(dbString, "","");
			connection.setAutoCommit(false);
			emptyBlob = connection.createBlob();
			emptyBlob.setBytes(1, "".getBytes());
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static String getSqlLimit() {
		return DBGlimit > 0 ? (" LIMIT " + DBGlimit) : "";
	}

	public ResultSet distinctAddresses(Integer limit) throws Exception {
		Statement stmt = connection.createStatement();
		String sql = "SELECT * FROM distinct_address " + (limit > 0 ? " LIMIT " + limit : "");

		ResultSet rs = stmt.executeQuery(sql);

		return rs;
	}

	public ResultSet getLabelofAddr(Blob adr) throws Exception {
		PreparedStatement stmt = connection.prepareStatement("select * from address_labels WHERE address = ?");
		stmt.setBlob(1, adr);
		return stmt.executeQuery();

	}

	public ResultSet addressLabelsTableRows() throws Exception {
		System.out.println("Starting Fetch of Address Label Table Rows "+new Date().toString());
		Statement stmt = connection.createStatement();
		String sql;
		sql = "select distinct address, label, type, url from address_labels " + getSqlLimit();
		System.out.println(" Retrieveing addresses from labels table");
		return stmt.executeQuery(sql);
	}

	private long getNumOfAddressesesOfCluster(Blob toFind) throws Exception {
		if (connection == null) {
			throw new Exception("getClusterForAddress: no connection");
		} // stop here

		PreparedStatement stmt = connection.prepareStatement("SELECT count(address) FROM address_cluster_ids WHERE cluster_id = ?");
		stmt.setBlob(1, toFind);

		ResultSet r = stmt.executeQuery();
		long cnt = 0;
		if (r.next()) {
			cnt = r.getLong(0);
		}
		r.close();
		stmt.close();
		return cnt;

	}

	private ResultSet getAllAddressessOfCluster(Blob toFind) throws Exception {
		if (connection == null) {
			throw new Exception("getClusterForAddress: no connection");
		} // stop here

		PreparedStatement stmt = connection.prepareStatement("SELECT DISTINCT address, cluster_id FROM address_cluster_ids WHERE cluster_id = ? ");
		stmt.setBlob(1, toFind);
		// System.out.println("    (f) finding all addresses of cluster: " +
		// blob2string(toFind));
		return stmt.executeQuery();
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

	public ResultSet clusterIdTableRows() throws Exception {
		Statement stmt = connection.createStatement();
		String sql;
		sql = "select  address, cluster_id from address_cluster_ids " + getSqlLimit();
		System.out.println(" Retrieveing address, cluster_id from address_cluster_ids table");
		return stmt.executeQuery(sql);
	}

	public ResultSet findAddressesInSameCluster(byte[] cluster_id) throws Exception {
		Statement stmt = connection.createStatement();
		String sql;
		sql = "select  address, cluster_id from address_cluster_ids where cluster_id = " + cluster_id;
		System.out.println(" Retrieveing addresses with same cluster_id from address_cluster_ids table");
		return stmt.executeQuery(sql);
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
		rs.close();
		stmt.close();
		// System.out.println("    (f) finding the cluster of address - FOUND: "
		// + blob2string(cluster));
		return cluster;
	}

	public void insertIntoBigLabel(Blob cluster_id, Blob address, String clusterString, String addressString, String label, String type, String url, long cnt, long icnt) {
		if (cnt % 50 == 0) {
			// System.out.println();
		}
		// System.out.print(".");

		BigLabelRow labelRow = new BigLabelRow(cluster_id, address, clusterString, addressString, label, type, url);
		bigLabelInsertMap.put(address, labelRow);
		

		if (bigLabelInsertMap.size() > MAX_BATCH_LIMIT) {
			if (processBatchInsert()) {
				bigLabelInsertMap.clear();
				System.out.println("inserted " + MAX_BATCH_LIMIT);
			}

		}

	}

	public boolean processBatchInsert() {
		
		

		PreparedStatement stmt = null;
		try {

			String insertSql = "Insert IGNORE into big_labels (cluster_id, address, clusterString, addressString, label, type, url) values (?,?,?,?,?,?,?)";
			stmt = connection.prepareStatement(insertSql);
			for (Blob adr : bigLabelInsertMap.keySet()) {
				BigLabelRow labelRow = bigLabelInsertMap.get(adr);
				stmt.setBlob(1, labelRow.cluster_id);
				stmt.setBlob(2, labelRow.address);
				stmt.setString(3, labelRow.clusterString);
				stmt.setString(4, labelRow.addressString);
				stmt.setString(5, labelRow.label);
				stmt.setString(6, labelRow.type);
				stmt.setString(7, labelRow.url);
				stmt.addBatch();
			}

			stmt.executeBatch();
			stmt.clearBatch();
			stmt.close();
			connection.commit();
			

			return true;

		} catch (Exception ex) {
			System.out.println(ex.toString());

		}

		return false;

	}

	public void insertIntoBigLabelDB(Blob cluster_id, Blob address, String clusterString, String addressString, String label, String type, String url, long cnt, long icnt) {
		PreparedStatement stmt = null;
		try {
			String insertSql = "Insert into big_labels (cluster_id, address, clusterString, addressString, label, type, url) values (?,?,?,?,?,?,?)";
			stmt = connection.prepareStatement(insertSql);

			stmt.setBlob(1, cluster_id);
			stmt.setBlob(2, address);
			stmt.setString(3, clusterString);
			stmt.setString(4, addressString);
			stmt.setString(5, label);
			stmt.setString(6, type);
			stmt.setString(7, url);
			stmt.executeUpdate();
			stmt.close();
			System.out.println(" Inserting into big_labels " + cnt + "." + (icnt > 0 ? icnt : "") + "\r");
		} catch (Exception ex) {
			System.out.print("Skipping " + " a) " + addressString + "  c)" + clusterString);
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} finally {

		}

	}

	public static String getStringFromBlob(Blob blob) throws Exception {
		return blob == null ? "" : getAddressFrom20bytes(blob.getBytes(1, (int) blob.length()));
	}

	public static String getAddressFrom20bytes(byte[] input) {
		byte[] b21 = new byte[21];
		b21[0] = 0x00;
		byte[] b20 = input;
		System.arraycopy(b20, 0, b21, 1, b20.length);
		byte[] checksum = Sha256Hash.hashTwice(b21);
		byte[] b25 = new byte[25];
		System.arraycopy(b21, 0, b25, 0, b21.length);
		System.arraycopy(checksum, 0, b25, b21.length, 4);
		String bAddress = Base58.encode(b25);
		return bAddress;
	}

	public void emptyBigLabelsTable() throws Exception {
		System.out.println("Emptying big labels Table");
		Statement stmt = connection.createStatement();
		String truncSql = "truncate table big_labels";
		stmt.execute(truncSql);
	}

	public void startProcessing() throws Exception {

		try {
			emptyBigLabelsTable();
			ResultSet crs, irs = null, xlrs =null, lrs = addressLabelsTableRows();
			System.out.println(new Date().toString());
			System.out.println("A) iterating over 20M all addresses with labels...");
			int cnt = 0, icnt = 0;
			long rcnt = 0;// only for testing purpose

			String s; // only for testing purpose

			Blob adr = null, cls = null, iaddr = null;
			String lbl, typ, url;

			while (lrs.next()) {
				adr = lrs.getBlob("address");
				lbl = lrs.getString("label");
				typ = lrs.getString("type");
				url = lrs.getString("label");
				cls = findClusterOfAddress(adr);

				// labelMap.put(adr, new String[] { lbl, typ, url });
				s = getLog(++cnt, adr, cls, lbl, typ, url);
				if (cls != null) {
					icnt = 0;

					// find All addresses of cluster - select count(*)
					// if > 10000
					// spawn n threads (0 - 10000), (10000...)
					// irs_T1 = getAllAddressessOfCluster(cls,l,hi);

					rcnt = getNumOfAddressesesOfCluster(cls);

					if (rcnt > CHUNK_SIZE) {
						
						System.out.println("Row Count "+rcnt);
						
						long from = 0;
						long to = 0;
						int i ;
						if (rcnt > CHUNK_SIZE) {
							for(i=0; i<rcnt/CHUNK_SIZE;i++) {
									from = i*CHUNK_SIZE;
									to  = (i+1)*CHUNK_SIZE-1;
									
									if(irs !=null) { irs.close();}
									irs = getRangeOfAddressesOfCluster(cls, from);
									while (irs.next()) {
										icnt++;
										iaddr = irs.getBlob("address");
										if (!iaddr.equals(adr)) {
											insertIntoBigLabel(cls, iaddr, getStringFromBlob(cls), getStringFromBlob(iaddr), lbl, typ, url, cnt, icnt);
										}
									}
							}
							from = (to+1);
							to = rcnt;
							if(irs !=null) { irs.close();}
							irs = getRangeOfAddressesOfCluster(cls, from);
							while (irs.next()) {
								icnt++;
								iaddr = irs.getBlob("address");
								if (!iaddr.equals(adr)) {
									insertIntoBigLabel(cls, iaddr, getStringFromBlob(cls), getStringFromBlob(iaddr), lbl, typ, url, cnt, icnt);
								}
							}
						}
						
						
						
						
						
						

					} else {

						if(irs !=null) { irs.close();}
						irs = getAllAddressessOfCluster(cls);
						while (irs.next()) {
							icnt++;
							iaddr = irs.getBlob("address");
							if (!iaddr.equals(adr)) {
								insertIntoBigLabel(cls, iaddr, getStringFromBlob(cls), getStringFromBlob(iaddr), lbl, typ, url, cnt, icnt);
							}
						}
					}

					s += " inner loop done for [" + icnt + "] addresses of same cluster...";
					System.out.println(s);
				}
				insertIntoBigLabel(cls, adr, getStringFromBlob(cls), getStringFromBlob(adr), lbl, typ, url, cnt, -1);

			}

			
			if (1 == 1) {
				return;
			}
			
			// DBGlimit = 100; // for test
			crs = clusterIdTableRows();
			System.out.println("B) iterating over 80M all addresses with clusters...");
			cnt = 0;
			while (crs.next()) {
				s = "\t [" + (++cnt) + "] " + blob2string(crs.getBlob("address"));
				s += " - " + blob2string(crs.getBlob("cluster_id"));
				System.out.print(cnt + "\r");
				adr = crs.getBlob("address");
				cls = crs.getBlob("cluster_id");
				// s = getLog(++cnt, adr, cls, lbl, typ, url);
				icnt = 0;
				
				if(irs !=null) { irs.close();}
				irs = getAllAddressessOfCluster(cls);
				boolean pop = false;
				while (irs.next()) {
					icnt++;
					System.out.print(cnt + "." + icnt + "\r");
					iaddr = irs.getBlob("address");
					pop = labelMap.containsKey(iaddr); // check if address has
														// label = set pop
														// boolean to true and
														// break;
					if (pop) {
						break;
					}
				}

				icnt = 0;
				if (pop) {
					if(xlrs !=null) { xlrs.close();}
					xlrs = getLabelofAddr(iaddr);
					lbl = xlrs.getString("label");
					typ = xlrs.getString("type");
					url = xlrs.getString("label");
					irs.first();
					do {
						icnt++;
						System.out.print(cnt + "-" + icnt + "\r");
						iaddr = irs.getBlob("address");
						insertIntoBigLabel(cls, iaddr, getStringFromBlob(cls), getStringFromBlob(iaddr), lbl, typ, url, cnt, icnt);

					} while (irs.next());
				}
				irs.close();

				cnt++;
				System.out.print(cnt + "\r");
			}

			crs.close();
			adr.free();
			cls.free();
			iaddr.free();
			System.out.println("C) done.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			System.out.println(new Date().toString());
			connection.close();

		}

	}

	static String blob2string(Blob b) {
		// TODO: use Base58
		byte[] bytes = { 0x00 };
		try {
			if (b != null) {
				bytes = b.getBytes(1, (int) b.length());
			} else {
				return " -(empty)- ";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return DatatypeConverter.printHexBinary(bytes);
	}

	static String blob2mini(Blob b) {
		try {
			if (b != null) {
				return ".." + DatatypeConverter.printHexBinary(b.getBytes((int) b.length() - 3, 3));
			} else {
				return "- - - - -";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	static String getLog(int i, Blob a, Blob c, String l, String t, String u) {
		String s = "[" + i + "] " + blob2mini(a) + " ";
		if (c != null) {
			s += "| " + blob2mini(c) + " ";
		}
		s += " - " + (l != null ? l : " ");
		s += " (" + (t != null ? t : "") + ") ";
		if (u != null && u.length() > 0) {
			s += "WWW";
		} else {
			s += "---";
		}
		return s;
	}

}
