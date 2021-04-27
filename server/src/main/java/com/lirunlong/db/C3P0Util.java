package com.lirunlong.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;

import com.lirunlong.util.Log;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 数据库工具类
 * 
 */
public class C3P0Util {
    static ComboPooledDataSource cpds = null;

    public static void init(String address, String password){
        cpds = new ComboPooledDataSource();
        cpds.setUser("postgres");
        cpds.setPassword(password);
        final String url = MessageFormat.format("jdbc:postgresql://{0}:5432/easynote", address);
        Log.info(url);
        cpds.setJdbcUrl(url);
    }
    /**
     * 获得数据库连接
     * 
     * @return Connection
     */
    public static Connection getConnection() {
        try {
            return cpds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("连接失败");
            return null;
        }
    }

    /**
     * 放回连接对象，close方法并不是关闭，而是更改该连接对象的状态为可用。
     * 
     * @param conn
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}