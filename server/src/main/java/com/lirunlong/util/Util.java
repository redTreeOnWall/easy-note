package com.lirunlong.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.lirunlong.db.C3P0Util;

public class Util {
    public static boolean saveIp(String ip, int type) {
        Connection conn = C3P0Util.getConnection();
        // 插入信息的sql语句
        String sql = "insert into t_ips(ip,time,type) values(?,?,?)";
        try {
            // 获取PreparedStatement对象
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ip);
            ps.setLong(2, System.currentTimeMillis());
            ps.setInt(3, type);
            ;
            // 执行更新操作
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // 关闭数据库连接
            C3P0Util.close(conn);
        }
    }

    public static ArrayList<List<Object>> SqlQuerryAll(String sql, Object[] params) throws SQLException {
        Connection conn = C3P0Util.getConnection();
        // 获取PreparedStatement对象
        PreparedStatement ps;
        ArrayList<List<Object>> l = new ArrayList<>();
        ps = conn.prepareStatement(sql);
        if (params != null) {
            for (var i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }
        ResultSet rs = ps.executeQuery();
        int col = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            ArrayList<Object> list = new ArrayList<Object>();
            for (int i = 0; i < col; i++) {
                list.add(rs.getObject(i + 1));
            }
            l.add(list);
        }
        ps.close();
        conn.close();
        return l;
    }

    public static int SqlUpdate(String sql, Object[] params) throws SQLException {
        Connection conn = C3P0Util.getConnection();
        // 获取PreparedStatement对象
        PreparedStatement ps;
        ps = conn.prepareStatement(sql);
        for (var i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        // 执行更新操作
        var rs = ps.executeUpdate();
        ps.close();
        conn.close();
        return rs;
    }

    public static Map<String, String> parsUrlParam(String urlParam) {
        var map = new HashMap<String, String>();
        var arr = urlParam.split("=");
        return map;
    }

    public static byte[] checkImg(String s) {
        var w = 60;
        var h = 20;
        var img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        var g = img.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);
        for (var i = 0; i <= 3; i++) {
            g.setColor(getRandowmColor());
            g.drawLine(0, (int) (Math.random() * h), w, (int) (Math.random() * h));
        }
        g.setColor(getRandowmColor());
        g.setFont(new Font("", Font.ITALIC, 20));
        g.drawString(s, 0, h - 3);
        System.out.println(img);
        // var file = new File("D:/tt/test.gif");
        try {
            var bts = new ByteArrayOutputStream();
            // var isok = ImageIO.write(img, "gif", file);
            ImageIO.write(img, "gif", bts);
            bts.close();
            return bts.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static Color getRandowmColor() {
        var c = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
        return c;
    }

    static void autoDBbean() {
        try {
            Connection conn = C3P0Util.getConnection();
            var m_DBMetaData = conn.getMetaData();

            var m_TableName = "%";
            ResultSet tableRet = m_DBMetaData.getTables(null, "%", m_TableName, new String[] { "TABLE" });
            /*
             * 其中"%"就是表示*的意思，也就是任意所有的意思。其中m_TableName就是要获取的数据表的名字，如果想获取所有的表的名字，
             * 就可以使用"%"来作为参数了。
             */
            var tables = new ArrayList<String>();

            while (tableRet.next()) {
                tables.add(tableRet.getString("TABLE_NAME"));
            }
            ;

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 核心思路 hashMap和 bean之间互相转化
        /*
            1. 从数据库读取表结构和类型,
            2. 生成bean类 ,可以顺便生成field和 field name的映射函数//并用用hashMap或者二叉树 或者hashcode数组之类的做缓存
            3. 查询 将数据库查询结果转化为hashMap(或者list)再生成bean 实例, (若是用的反射,可以跳过hashMap阶段)
            4. 生成适用于任何bean对象的
        */
    }

    public static class TestMap {
        public String name;
        public int age;
    }

    static void TestBean2Map() {
        // bean to hashMap
        var b = new TestMap();
        var bMap = new HashMap<String,Object>();
        b.name = "lee";
        b.age = 25;

        try {
            Class clz = Class.forName("com.lirunlong.util.Util$TestMap");
            var fs = clz.getFields();
            for (Field field : fs) {
                var val = field.get(b);
                var key = field.getName();
                bMap.put(key, val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        for ( var kv : bMap.entrySet()) {
            System.out.println(String.format("key:%s,val:%s",kv.getKey(),kv.getValue()));
        }

        //hashMap to bean

        try {
            Class clz = Class.forName("com.lirunlong.util.Util$TestMap");
            Constructor constructor = clz.getConstructor();
            var object = (com.lirunlong.util.Util.TestMap)constructor.newInstance();

            for ( var kv : bMap.entrySet()) {
                var field = clz.getField(kv.getKey());
                field.set(object, kv.getValue());
            }

            System.out.println(MessageFormat.format("name:{0} , age:{1}",object.name,object.age));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}