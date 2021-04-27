package com.lirunlong.main;

import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.lirunlong.db.C3P0Util;
import com.lirunlong.net.http.HttpHandle;
import com.lirunlong.net.http.HttpServer;
import com.lirunlong.net.http.HttpHandlers.HttpApiHandle;
import com.lirunlong.util.Log;
import com.lirunlong.util.Util;



public class MainClass {
    private static MainClass main;

    public static MainClass getInstance() {
        return main;
    }

    public static HttpServer apiServer;

    public static void main(String[] args) throws Exception {

        if(args.length < 2) {
            throw new Exception("please input database address and password.");
        }

        C3P0Util.init(args[0], args[1]);

        var conn = C3P0Util.getConnection();

        var sql = " select * from t_user";

        ArrayList<List<String>> l = new ArrayList<>();
        try {
            // 获取PreparedStatement对象
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            int col = rs.getMetaData().getColumnCount();
            for(int i =0;i<col;i++){
                String colName = rs.getMetaData().getColumnName(i+1);
            }
            while(rs.next()){
                ArrayList<String> list = new ArrayList<String>();
                for(int i=0;i<col;i++){
                    list.add(rs.getString(i+1));
                    Log.info(rs.getString(i+1));
                }
                l.add(list);
            }
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            C3P0Util.close(conn);
        }

    }



    public static void StartApiServer(){
        apiServer = new HttpServer(6090);
        addRouts(apiServer);
        apiServer.start(() -> new HttpApiHandle());
    }

    private static void addRouts(HttpServer server) {
        server.addRout("/ip", (ctx, request, content) -> {
            server.threadPool.addTask(() -> {
                InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
                final String clientIP = insocket.getAddress().getHostAddress();
                Util.saveIp(clientIP, 1);
                HttpHandle.responsString(ctx, "1", request);
            });
        });
    }
}
