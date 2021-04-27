package com.lirunlong.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.lirunlong.db.C3P0Util;

public class DB2JavaBean {

    public static class FieldMateData {
        public String fieldName;
        public String fieldTypeName;
        public String dbTypeName;
    }

    public static class ClassMateData {
        public String className;
        public ArrayList<FieldMateData> fields = new ArrayList<>();
    }

    public static void main(String[] args) {
        var className = "DBBeans";
        var packageName = "com.lirunlong.db.beans";
        File f = new File("./server/src/main/java/com/lirunlong/db/beans/" + className + ".java").getAbsoluteFile();
        if (f.isFile()) {
            System.out.println(f.getPath());
            try {
                var code = codeGen(className, packageName);
                var os = new FileWriter(f);
                os.write(code);
                os.flush();
                os.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            System.err.println("dir not exit");
        }
    }

    static String codeGen(String className,String packageName) throws Exception {
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

            var classes = new ArrayList<ClassMateData>();

            for (String tName : tables) {

                var cla = new ClassMateData();
                cla.className = tName;
                classes.add(cla);

                var sql = "select * from " + tName + " limit 1";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                int col = rs.getMetaData().getColumnCount();

                System.out.println(tName);
                System.out.println("---");
                for (int i = 0; i < col; i++) {
                    String colName = rs.getMetaData().getColumnName(i + 1);
                    var colType = rs.getMetaData().getColumnTypeName(i + 1);

                    var f = new FieldMateData();
                    cla.fields.add(f);
                    f.fieldName = colName;
                    f.dbTypeName = colType;

                    System.out.print(colName + "["+colType+"] ,");

                    String t = colType.toLowerCase();
                    if(
                        t=="varchar"
                        || t=="text"
                    ){
                        f.fieldTypeName= "String";
                    }
                    else if(
                        t == "int4"
                        || t == "serial"
                    ){
                        f.fieldTypeName = "Integer";
                    }
                    else if(
                        t == "int8"
                    ){
                        f.fieldTypeName = "Long";
                    }
                    else if(t=="date"){
                        f.fieldTypeName = "java.sql.Date";
                    }else{
                        throw new Exception("no this type :" + t);
                    }
                }
                System.out.println("");
                System.out.println("");

                // while(rs.next()){
                // ArrayList<String> list = new ArrayList<String>();
                // for(int i=0;i<col;i++){
                // list.add(rs.getString(i+1));
                // }
                // }
                ps.close();
            }

            conn.close();

            System.out.println("====================================================================================================");
            var sb = new StringBuilder();
            typeNum = 0;



            apendLineToSb(
                "package " + packageName  + ";"
            , sb);
            sb.append("\n");

            apendLineToSb( "import com.lirunlong.db.DOutils.DBBean;" , sb);

            sb.append("\n");

            apendLineToSb(
                "public class "+className+"{"
            , sb);

            typeNum ++;

            apendLineToSb( "" , sb);

            for (ClassMateData classMateData : classes) {
                apendLineToSb( "" , sb);
                apendLineToSb(
                    "public static class "+classMateData.className +  " extends DBBean {"
                , sb);
                typeNum ++;

                for (var fName  : classMateData.fields) {

                    apendLineToSb(
                        "/**  " + fName.dbTypeName+" */"
                    , sb);

                    apendLineToSb(
                        "public "+fName.fieldTypeName +" "+ fName.fieldName +";"
                    , sb);
                }

                typeNum --;
                apendLineToSb(
                    "}"
                , sb);
            }

            apendLineToSb( "" , sb);
            typeNum --;
            apendLineToSb(
                "}"
            , sb);

            System.out.println(sb.toString());

            System.out.println("====================================================================================================");
            return sb.toString();

    }
    static int typeNum;
    static void apendLineToSb(String line,StringBuilder sb){
        sb.append("\n");
        for(var i = 0;i< typeNum;i++){
            sb.append("    ");
        }
        sb.append(line);
    }
}