package com.lirunlong.db;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DOutils {
    public static class DBBean {

    }

    static class FieldNameValuePair {
        protected String name;
        protected Object value;
    }

    public static void setBeanFromData(ResultSet rs, DBBean beanToSet) {
        try {
            var cla = beanToSet.getClass();
            var mat = rs.getMetaData();
            var colNum = mat.getColumnCount();
            for (var i = 0; i < colNum; i++) {
                var value = rs.getObject(i + 1);
                var name = mat.getColumnLabel(i + 1);
                var f = cla.getField(name);
                f.set(beanToSet, value);
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
                | SQLException e) {
            e.printStackTrace();
        }
    }

    public static int  InsertOneBean(DBBean bean ,String keyNameOrNull ) {
        var cla = bean.getClass();

        var tableName = cla.getSimpleName();

        var filds = cla.getFields();
        var notEmptyPairs = new ArrayList<FieldNameValuePair>(filds.length);
        for (var i = 0; i < filds.length; i++) {
            try {
                var val = filds[i].get(bean);
                if (val != null) {
                    var pair = new FieldNameValuePair();
                    pair.name = filds[i].getName();
                    pair.value = val;
                    notEmptyPairs.add(pair);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (notEmptyPairs.size() == 0) {
            System.err.println("the bean is empty");
            return -1;
        }

        var sqlSb = new StringBuilder();

        sqlSb.append("insert into ")
            .append(tableName)
            .append("( ");

        sqlSb.append(notEmptyPairs.get(0).name);

        for(var i = 1 ;i < notEmptyPairs.size() ; i++){
            sqlSb.append(" ,");
            sqlSb.append(notEmptyPairs.get(i).name);
        }

        sqlSb.append(" ) values( ");

        sqlSb.append(" ? ");

        for(var i = 1 ;i < notEmptyPairs.size() ; i++){
            sqlSb.append(" ,?");
        }

        sqlSb.append(" );");
    
        var sql = sqlSb.toString();
        System.out.println(sql);

        var con = C3P0Util.getConnection();
        try {

            PreparedStatement statement;

            if(keyNameOrNull != null){
                String[] keys = {keyNameOrNull};
                statement = con.prepareStatement(sql,keys );
            }else{
                statement = con.prepareStatement(sql );
            }

            for (var i = 0; i < notEmptyPairs.size(); i++) {
                statement.setObject(i+1, notEmptyPairs.get(i).value);
            }
            var i = statement.executeUpdate();
            var rs = statement.getGeneratedKeys();
            var key = -1;
            if (rs.next()) {
                key = rs.getInt(1);
            }
            return key;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;

    }

    public static void UpdateBean(String condition){
        //TODO: implement when need
    }
}