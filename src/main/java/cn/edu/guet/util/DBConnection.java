package cn.edu.guet.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBConnection {

	private static DruidDataSource druidDataSource;
	private static ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

	static{
		try {
			Properties properties = new Properties();
			// ��ȡ jdbc.properties���������ļ�
			InputStream resourceAsStream = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
			// �����м�������
			properties.load(resourceAsStream);
			// �������ݿ����ӳ�
			druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection(){
		Connection conn = connectionThreadLocal.get();
		try {
			if(null == conn){
				conn = druidDataSource.getConnection();
				connectionThreadLocal.set(conn);
				conn.setAutoCommit(false);//����Ϊ�ֶ��ύ
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return conn;
	}


	/**
	 * �ֶ��ύ����
	 */
	public static void commitAndClose(){
		Connection conn = connectionThreadLocal.get();
		if(conn != null){  //˵��֮ǰ���ӹ����ݿ�
			try {
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
				close(conn);
			}
		}
		// һ��Ҫִ�� remove ��������ֹ�ڴ�й©������ͻ��������Ϊ Tomcat �������ײ�ʹ�����̳߳ؼ�����
		connectionThreadLocal.remove();
	}

	/**
	 * �ع�
	 */
	public static void rollbackAndClose(){
		Connection conn = connectionThreadLocal.get();
		if(conn != null){
			try {
				conn.rollback(); //�ع�����
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}finally {
				close(conn);
			}
		}
		// һ��Ҫִ�� remove ��������ֹ�ڴ�й©������ͻ��������Ϊ Tomcat �������ײ�ʹ�����̳߳ؼ�����
		connectionThreadLocal.remove();
	}

	public static void close(Connection conn){
		try {
			if(null != conn){
				conn.close();
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	public static void close(PreparedStatement pstt,Connection conn){
		try {
			if(null != pstt){
				pstt.close();
			}
			if(null != conn){
				conn.close();
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	public static void close(ResultSet rs,PreparedStatement pstt,Connection conn){
		try {
			if(null != rs){
				rs.close();
			}
			if(null != pstt){
				pstt.close();
			}
			if(null != conn){
				conn.close();
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}
}
