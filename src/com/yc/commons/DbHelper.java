package com.yc.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;

import java.lang.reflect.Method;
import java.sql.*;

/**增删改   executeUptate()
 * 查询 	executeQuery
 * @author 欧阳志
 * @date 2019年10月29日 下午7:11:37
 */
public class DbHelper {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
//	static {
//		try {
//			//加载驱动
//			Class.forName(MyProperties.getInstance().getProperty("driverName"));//全路径名称
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
	//获取连接对象
	public  Connection getConn() throws SQLException{
		//Connection	conn = DriverManager.getConnection(MyProperties.getInstance().getProperty("url"),MyProperties.getInstance());
		//（1）	
		/*<!-- 配置数据源  -->
	    <Resource
	    name="jdbc/fresh"   
	    auth="Container"   
	    type="javax.sql.DataSource" 
	    driverClassName="com.mysql.jdbc.Driver"
	    username="root"
	    password="a"
		url="jdbc:mysql://localhost:3306/db_fresh"
	    autoReconnect="true"
		autoReconnectForPools="true"
	    maxActive="120"
	    maxIdle="30"
	    maxWait="8000"
	    />*/
		/*配置context.xml
				try {
					Context ic=new InitialContext();
					DataSource source=(DataSource)ic.lookup("java:comp/env/jdbc/fresh");
					conn=source.getConnection();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
		
		
		
		//（2）
		try {
			DataSource source =(DataSource)BasicDataSourceFactory.createDataSource(Env.getInstance());
			conn=source.getConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	public void closeAll(Connection conn,PreparedStatement pstmt,ResultSet rs) {
		if(null!=rs) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(null!=pstmt) {
			try {
				pstmt.close();
			} catch (SQLException e) {
			
				e.printStackTrace();
			}
		}
		if(null!=conn) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**查询语句返回多条数据  select *from table_name ?
	 * @param sql  查询语句
	 * @param params  所需参数List<>
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String,Object>> findMutipl(String sql,List<Object> params) throws Exception{
		
		List<Map<String,Object>> list =new ArrayList<Map<String,Object>>();
		Map<String,Object> map=null;
		try{
			conn=this.getConn();
			pstmt=conn.prepareStatement(sql);
			setParamsList(pstmt,params);
			rs=pstmt.executeQuery();
			//获取所有的列名
			List<String> columnNames=getColumnNames(rs);
			while(rs.next()) {
				map=new HashMap<String,Object>();
				for(String name:columnNames) {
					Object obj=rs.getObject(name);
					if(null==obj) {
						continue;
					}
					String typeName =obj.getClass().getName();
//					if("oracle.sql.BLOB".equals(typeName)) {
//						BLOB blob =(BLOB) rs.getBlob(name);
//						InputStream in=blob.getBinaryStream();
//						byte[] bt=new byte[(int) blob.length()];
//						in.read(bt);
//						map.put(name, bt);
//					}else {
//						map.put(name,obj);
//					}
				}
				list.add(map);
			}
		}
				finally {
			this.closeAll(conn, pstmt, rs);
		}
		return list;
	}
	/**查询语句最多返回一条数据  select *from table_name where id=?
	 * @param sql  查询语句
	 * @param params  所需参数List<>
	 * @return
	 * @throws Exception 
	 */
public Map<String,Object> findSingle(String sql,List<Object> params) throws Exception{
		Map<String,Object> map=null;
		try{
			//获取连接对象
			conn=this.getConn();
			//获取预编译对象
			pstmt=conn.prepareStatement(sql);
			//设置参数
			setParamsList(pstmt,params);
			rs=pstmt.executeQuery();
			//获取所有的列名
			List<String> columnNames=getColumnNames(rs);
			if(rs.next()) {
				map=new HashMap<String,Object>();
				for(String name:columnNames) {
					//根据字段名获取值
					Object obj=rs.getObject(name);
					if(null==obj) {
						continue;
					}
					String typeName =obj.getClass().getName();
//					if("oracle.sql.BLOB".equals(typeName)) {
//						//说明是图片，存储到map集合中字节数组中
//						BLOB blob =(BLOB) rs.getBlob(name);//BLOB操作只能在连接未断时进行
//						InputStream in=blob.getBinaryStream();
//						byte[] bt=new byte[(int) blob.length()];
//						in.read(bt);
//						map.put(name, bt);
//					}else {
//						//字段名做键
//						map.put(name,obj);
//					}
				}
			}
		}finally {
			this.closeAll(conn, pstmt, rs);
		}
		return map;
	}
	
public 	<T> T find(String sql,List<Object> params,Class<T> cls) throws Exception {
	T t=null;
		try {
			conn = getConn();
			pstmt = conn.prepareStatement(sql); // 预处理sql语句
			setParamsList(pstmt, params);
			rs=pstmt.executeQuery();//执行查询
			Method[] methods = cls.getDeclaredMethods();
			List<String> columnNames=getColumnNames(rs);
			if(rs.next()) {
				t = cls.newInstance();
				// 根据类信息，实例化一个对�?new Dept()
				if (methods != null && methods.length > 0) {
				for (Method m :methods) { // 循环取列
					for (String name : columnNames) { // 循环
							if (("set"+name).equalsIgnoreCase(m.getName())){
								//修改:  1. 取  m 这个方法 ( setXXX() )的参数的类型 2. 因为 setXXX是java的方法，java的数据类型 是固定
								String type=m.getParameterTypes()[0].getName();
								if(  "int".equals(type) || "java.lang.Integer".equals(   type) ){
									m.invoke(t, rs.getInt(name));
								}else if(  "float".equals(type) || "java.lang.Float".equals(   type) ){
									m.invoke(t, rs.getFloat(name));
								}else if(  "double".equals(type) || "java.lang.Double".equals(   type) ){
									m.invoke(t, rs.getDouble(name));
								}else if(  "boolean".equals(type) || "java.lang.Boolean".equals(   type) ){
									m.invoke(t, rs.getBoolean(name));
								}else if(  "String".equals(type) || "java.lang.String".equals(   type) ){
									m.invoke(t, rs.getString(name));
								}else{
									m.invoke(t, rs.getObject(name));
								}
								break;
							}
						}
					}
				}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}  finally {
				closeAll(conn,pstmt,rs);
			}
			
			return t;
		}

public <T> List<T> findMutil(String sql,List<Object> params,Class<T> cls) throws Exception {
	T t=null;
	List<T> list=new ArrayList<T>();
	try {
		conn = getConn();
		pstmt = conn.prepareStatement(sql); // 预处理sql语句
		setParamsList(pstmt, params);
		rs=pstmt.executeQuery();//执行查询
		Method[] methods = cls.getDeclaredMethods();
		List<String> columnNames=getColumnNames(rs);
	
		while (rs.next()) {
			t = cls.newInstance();
			// 根据类信息，实例化一个对�?new Dept()
			for (Method m :methods) { // 循环取列
				for (String name : columnNames) { // 循环
						
						if (("set"+name).equalsIgnoreCase(m.getName())){
							//修改:  1. 取  m 这个方法 ( setXXX() )的参数的类型 2. 因为 setXXX是java的方法，java的数据类型 是固定
							String type=m.getParameterTypes()[0].getName();
							if(  "int".equals(type) || "java.lang.Integer".equals(   type) ){
								m.invoke(t, rs.getInt(name));
							}else if(  "float".equals(type) || "java.lang.Float".equals(   type) ){
								m.invoke(t, rs.getFloat(name));
							}else if(  "double".equals(type) || "java.lang.Double".equals(   type) ){
								m.invoke(t, rs.getDouble(name));
							}else if(  "boolean".equals(type) || "java.lang.Boolean".equals(   type) ){
								m.invoke(t, rs.getBoolean(name));
							}else if("java.lang.String".equals(type) ){
								m.invoke(t, rs.getString(name));
							}else{
								m.invoke(t, rs.getObject(name));
							}
							break;
						}
					}
				}
			list.add(t);
			
		}

	} catch (Exception e) {
		e.printStackTrace();
	}  finally {
		closeAll(conn,pstmt,rs);
	}
	
	return list;
}
	/**
	 * @param rs2
	 * @return
	 * @throws SQLException 
	 */
	private List<String> getColumnNames(ResultSet rs2) throws SQLException {
		List<String> list =new ArrayList<String>();
		ResultSetMetaData data=rs.getMetaData();
		//获取总列数
		int count=data.getColumnCount();
		for(int i=1;i<=count;i++) {
			list.add(data.getColumnName(i));
		}
		return list;
	}

	/**设置参数   参数是LIST
	 * @param pstmt2
	 * @param params
	 * @throws SQLException 
	 */
	private void setParamsList(PreparedStatement pstmt2, List<Object> params) throws SQLException {
		if(null==params|| params.isEmpty()) {
			return;
		}
		for(int i=0;i<params.size();i++) {
			pstmt.setObject(i+1, params.get(i));
		}
	}
	public int update(List<String> sqls,List<List<Object>> params) throws Exception {
		int result=0;
		try {
			conn=getConn();//事务默认自动提交
			//事务设置为手动提交
			conn.setAutoCommit(false);
			//循环sql语句
			if(null==sqls||sqls.isEmpty()) {
				return result;
			}
			for(int i=0;i<sqls.size();i++) {
				String sql=sqls.get(i);
				pstmt=conn.prepareStatement(sql);
				//设置参数
				setParamsList(pstmt, params.get(i));//获取第i个小List设置参数
				result=pstmt.executeUpdate();
				if(result<=0) {
					conn.rollback();
					return result;
				}
			}
			//事务提交
			conn.commit();
		}catch(SQLException e){
			conn.rollback();
		}
		finally {
			//还原事务状态
			conn.setAutoCommit(true);
			this.closeAll(conn, pstmt, null);
		}
		return result;
	}
	public int updatenoback(List<String> sqls,List<List<Object>> params) throws Exception {
		int result=0;
		try {
			conn=getConn();//事务默认自动提交
			//事务设置为手动提交
			conn.setAutoCommit(false);
			//循环sql语句
			if(null==sqls||sqls.isEmpty()) {
				return result;
			}
			for(int i=0;i<sqls.size();i++) {
				String sql=sqls.get(i);
				pstmt=conn.prepareStatement(sql);
				//设置参数
				setParamsList(pstmt, params.get(i));//获取第i个小List设置参数
				result=pstmt.executeUpdate();
			
			}
			//事务提交
			conn.commit();
		}catch(SQLException e){
			System.out.println("error");
		}
		finally {
			//还原事务状态
			conn.setAutoCommit(true);
			this.closeAll(conn, pstmt, null);
		}
		return result;
	}
	/*
	 * params 不定长数组
	 * 单条更新语句
	 **/
	public int update(String sql,Object...params) throws SQLException {
		int result =0;
		
		try{
			conn=this.getConn();
			pstmt=conn.prepareStatement(sql);
			setParamsObject(pstmt,params);//设置参数
			result=pstmt.executeUpdate();
		}finally {
			this.closeAll(conn, pstmt, rs);
		}
		
		return result;
	}

	/**
	 * @param pstmt2
	 * @param params
	 * @throws SQLException 
	 */
	private void setParamsObject(PreparedStatement pstmt2, Object[] params) throws SQLException {
		if(null==params|| params.length<=0) {
			return ;
		}
		for(int i=0;i<params.length;i++) {
			pstmt.setObject(i+1, params[i]);
		}
		
	} 
	/**
	 * 聚合函数    sum   avg   min   max
	 * select sum(*) from tb_name where ....
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public double getPolymer(String sql,List<Object> params) throws SQLException {
		double result=0;
		try {
			conn=getConn();
			pstmt=conn.prepareStatement(sql);
			setParamsList(pstmt, params);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				result=rs.getDouble(1);
			}
		}finally {
			closeAll(conn,pstmt,rs);
		}
		
		return result;
	}
//	public static void main(String[] args) {
//		DbHelper db=new DbHelper();
//		try {
//			System.out.println(db.getConn());
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
