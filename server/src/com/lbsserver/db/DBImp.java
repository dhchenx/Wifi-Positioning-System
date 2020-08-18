package com.lbsserver.db;
//MySql�������ݿ������ʩ
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class DBImp{
	Connection conn = null;
	protected String Url = "";
	protected String DataBase = "";
	protected String UserName = "";
	protected String Password = "";


	// ���캯��
	public DBImp(String url, String db, String un, String pass) {

		setUrl(url);
		setUserName(un);
		setDataBase(db);
		setPassword(pass);
		conn=this.InitConnection(un, url, db, pass);
	}

	// �����������ݿ�·��
	public void setUrl(String value) {
	
		Url = value;
	}

	// ������Ҫ���ʵ����ݿ�
	public void setDataBase(String value) {
	
		DataBase = value;
	}

	// �����û���¼�û���������
	public void setUserName(String value) {
	
		UserName = value;
	}

	public void setPassword(String value) {
	
		Password = value;
	}

	// ����������ݼ���
	private ResultSet getResultSet(String s) {
	
		try {
			conn=this.InitConnection();
			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery(s);
			return rs;
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		return null;
	}

	// �ر����ݿ�
	public void Close() {
	
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ����������ļ������ݿ�
	/*
	 * sql: ���ݿ�ִ����䣬��insert into userinfo ( username , password , image) values
	 * (?,?,?) type: �ֶ����� enfiles: ���β�����ֶ�ֵ���������ͣ�����Ƕ������ļ�����ö����Ʊ�����ΪBASE64����
	 */
	public String BinaryExecute(String sql, String[] type, String[] enfiles) {

		// 1.create sql ;
		// sql =
		// "insert into userinfo ( username , password , image) values (?,?,?)";

		// 2.get connection
		PreparedStatement psmt = null;

		InputStream is = null;
		conn=this.InitConnection();
		if(conn==null)
			return "-1";
		try {

			// 3.prepare sql
			psmt = conn.prepareStatement(sql);

			// 4.set params
			for (int i = 0; i < type.length; i++) {
				// ��ͨ�������ļ�
				if (type[i].equals("file")) {
					Base64 bs = new Base64();
					byte[] file = bs.decodestr(enfiles[i] );
					is = new ByteArrayInputStream(file);
					psmt.setBinaryStream(i, is);
					continue;
				}
				// ͼƬ
				if (type[i].equals("blob")) {
					Base64 bs = new Base64();
					byte[] file = bs.decode(enfiles[i], 0, enfiles[i].length());
					is = new ByteArrayInputStream(file);
					psmt.setBlob(i, is);
					continue;
				}
				// �����ı�
				if (type[i].equals("string")) {
					psmt.setString(i, enfiles[i]);

					continue;
				}
				// ��������
				if (type[i].equals("date")) {
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					java.util.Date date = sdf.parse(enfiles[i]);
					psmt.setDate(i, new java.sql.Date(date.getTime()));
					;
					continue;
				}
				// ��������
				if (type[i].equals("int")) {
					psmt.setInt(i, Integer.valueOf(enfiles[i]));
					continue;
				}
				if (type[i].equals("double")) {
					psmt.setDouble(i, Double.valueOf(enfiles[i]));
					continue;
				}
				if (type[i].equals("float")) {
					psmt.setDouble(i, Float.valueOf(enfiles[i]));
					continue;
				}
				if (type[i].equals("short")) {
					psmt.setShort(i, Short.valueOf(enfiles[i]));
					continue;
				}
				// �����ֽ�
				if (type[i].equals("byte")) {
					psmt.setByte(i, Byte.valueOf(enfiles[i]));
					continue;
				}
				psmt.setString(i, enfiles[i]);
			}
			// 5.update db
			psmt.executeUpdate();
			
			return "0";
		} catch (Exception e) {
			e.printStackTrace();
			return "1";
		} finally {
			// 6.close db
			try {
				if (psmt != null)
					psmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	/*
	 * sql ִ�����
	 */
	private final byte[] input2byte(InputStream inStream)
			throws IOException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();
		return in2b;
	}
	
	public String[][] BinarySelect(String sql,String[] type,String[] col){
		conn=this.InitConnection();
		if(conn==null)
			return null;
		ResultSet rs = getResultSet(sql);
		int d = GetCountByResult(rs);
		String r[][] = new String[d][col.length];
		try {
			rs.beforeFirst();
			while (rs.next()) {
				for (int i = 0; i < col.length; i++) {

					if(type[i].equals("file")){
						Base64 bs=new Base64();
						InputStream is=rs.getBinaryStream(col[i]);
						byte[] tmp=input2byte(is);
						r[rs.getRow() - 1][i] = bs.encodestr(tmp, 0, tmp.length);
						continue;
					}	
					//ͼƬ
					if(type[i].equals("blob")){
						Base64 bs=new Base64();
						InputStream is=rs.getBinaryStream(col[i]);
						byte[] tmp=input2byte(is);
						r[rs.getRow() - 1][i] = bs.encodestr(tmp, 0, tmp.length);
						continue;
					}	
					//�����ı�
					if(type[i].equals("string")){
						String s=rs.getString(col[i]);
						r[rs.getRow() - 1][i] = s;
						continue;
					}
					//��������
					if(type[i].equals("date")){
						SimpleDateFormat sdf  =   new  SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ); 
						Date date=rs.getDate(col[i]);
						r[rs.getRow() - 1][i] = sdf.format(date);			 
						continue;
					}
					//��������
					if(type[i].equals("int")){
						r[rs.getRow() - 1][i] = String.valueOf(rs.getInt(col[i]));	 
						continue;
					}
					if(type[i].equals("double")){
						r[rs.getRow() - 1][i] =String.valueOf(rs.getDouble(col[i])) ;
						continue;
					}
					if(type[i].equals("float")){
						r[rs.getRow() - 1][i] =String.valueOf(rs.getFloat(col[i]));	 
						continue;
					}
					if(type[i].equals("short")){
						r[rs.getRow() - 1][i] =String.valueOf(rs.getShort(col[i]));	 
						continue;
					}
					//�����ֽ�
					if(type[i].equals("byte")){
						r[rs.getRow() - 1][i] =String.valueOf(rs.getByte(col[i]));	 
						continue;
					}
					
				}
				//System.out.print("\n");
			}
			conn.close();
			return r;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// ִ�в��룬ɾ�������ݿ�������ɹ�����0�����ɹ�����-1��
	public int execute(String s) {
		try {
			conn=this.InitConnection();
			Statement st = conn.createStatement();
			st.execute(s);
			conn.close();
			return 0;
		} catch (Exception e) {
			System.out.println("database execute error!");
			e.printStackTrace();
			return -1;
		}

	}
	//��ʼ�����ݿ�����
	
	public Connection InitConnection(String dbuser,String dburl,String dbname,String dbpass) {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String url = "jdbc:mysql://" + dburl + "/" + dbname + "?user="
					+ dbuser + "&password=" + dbpass + "";
			conn = DriverManager.getConnection(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
    public Connection InitConnection(){
    	return conn;
    }
	// ִ�и������ݿ�������ɹ�����0�����ɹ�����-1��
	public int executeUpdate(String s) {
		try {
			conn=this.InitConnection();
			Statement st = conn.createStatement();
			st.executeUpdate(s);
			conn.close();
			return 0;
		} catch (Exception e) {
			System.out.println("database init error!");
			e.printStackTrace();
			return -1;
		}

	}
	
	

	// ��ȡ���ݼ�������Ҫ����
	public String[] GetRecordByCol(ResultSet rs, int index, String[] col) {
		try {
			rs.first();
			String[] rr = new String[col.length];
			while (rs.next()) {
				int i = rs.getRow();
				if (i == index) {
					for (int j = 0; j < rr.length; j++) {
						rr[j] = rs.getString(col[j]);
					}
					break;
				}
			}
			return rr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// ��ȡ���ݼ������м�¼������
	public int GetCountByResult(ResultSet rs) {
		int i = 0;
		try {
			while (rs.next()) {
				i++;
			}
			return i;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// ��ȡһ����ѯ��������б�����
	public String[][] getTable(String query, String[] col) {
		ResultSet rs = getResultSet(query);
		int d = GetCountByResult(rs);
		String r[][] = new String[d][col.length];
		try {
			rs.beforeFirst();
			while (rs.next()) {
				for (int i = 0; i < col.length; i++) {
					r[rs.getRow() - 1][i] = rs.getString(col[i]);
					// System.out.print(r[rs.getRow()-1][i]+"\t");
				}
			}
			conn.close();
			return r;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {


	}

}