package com.lbsserver.db;
//MySql网络数据库访问设施
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class DBImp{
	Connection conn = null;
	protected String Url = "";
	protected String DataBase = "";
	protected String UserName = "";
	protected String Password = "";


	// 构造函数
	public DBImp(String url, String db, String un, String pass) {

		setUrl(url);
		setUserName(un);
		setDataBase(db);
		setPassword(pass);
		conn=this.InitConnection(un, url, db, pass);
	}

	// 设置网络数据库路径
	public void setUrl(String value) {
	
		Url = value;
	}

	// 设置需要访问的数据库
	public void setDataBase(String value) {
	
		DataBase = value;
	}

	// 设置用户登录用户名和密码
	public void setUserName(String value) {
	
		UserName = value;
	}

	public void setPassword(String value) {
	
		Password = value;
	}

	// 获得请求数据集合
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

	// 关闭数据库
	public void Close() {
	
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 插入二进制文件到数据库
	/*
	 * sql: 数据库执行语句，如insert into userinfo ( username , password , image) values
	 * (?,?,?) type: 字段类型 enfiles: 依次插入的字段值，根据类型，如果是二进制文件，则该二进制被加密为BASE64编码
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
				// 普通二进制文件
				if (type[i].equals("file")) {
					Base64 bs = new Base64();
					byte[] file = bs.decodestr(enfiles[i] );
					is = new ByteArrayInputStream(file);
					psmt.setBinaryStream(i, is);
					continue;
				}
				// 图片
				if (type[i].equals("blob")) {
					Base64 bs = new Base64();
					byte[] file = bs.decode(enfiles[i], 0, enfiles[i].length());
					is = new ByteArrayInputStream(file);
					psmt.setBlob(i, is);
					continue;
				}
				// 保存文本
				if (type[i].equals("string")) {
					psmt.setString(i, enfiles[i]);

					continue;
				}
				// 保存日期
				if (type[i].equals("date")) {
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					java.util.Date date = sdf.parse(enfiles[i]);
					psmt.setDate(i, new java.sql.Date(date.getTime()));
					;
					continue;
				}
				// 保存数字
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
				// 保存字节
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
	 * sql 执行语句
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
					//图片
					if(type[i].equals("blob")){
						Base64 bs=new Base64();
						InputStream is=rs.getBinaryStream(col[i]);
						byte[] tmp=input2byte(is);
						r[rs.getRow() - 1][i] = bs.encodestr(tmp, 0, tmp.length);
						continue;
					}	
					//保存文本
					if(type[i].equals("string")){
						String s=rs.getString(col[i]);
						r[rs.getRow() - 1][i] = s;
						continue;
					}
					//保存日期
					if(type[i].equals("date")){
						SimpleDateFormat sdf  =   new  SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ); 
						Date date=rs.getDate(col[i]);
						r[rs.getRow() - 1][i] = sdf.format(date);			 
						continue;
					}
					//保存数字
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
					//保存字节
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

	// 执行插入，删除等数据库操作，成功返回0，不成功返回-1；
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
	//初始化数据库连接
	
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
	// 执行更新数据库操作，成功返回0，不成功返回-1；
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
	
	

	// 获取数据集合中想要的列
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

	// 获取数据集中所有记录的总数
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

	// 获取一个查询请求的所有表数据
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