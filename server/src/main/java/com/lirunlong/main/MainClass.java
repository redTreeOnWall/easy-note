package com.lirunlong.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.lirunlong.db.C3P0Util;
import com.lirunlong.main.Message.ActionId;
import com.lirunlong.main.Message.NoteData;
import com.lirunlong.net.http.HttpHandle;
import com.lirunlong.net.http.HttpServer;
import com.lirunlong.net.http.HttpHandlers.HttpApiHandle;
import com.lirunlong.net.tool.Session;
import com.lirunlong.util.Action;
import com.lirunlong.util.JsonUtil;
import com.lirunlong.util.Log;
import com.lirunlong.util.Util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;


public class MainClass {
  private static MainClass main;

  public static MainClass getInstance() {
    return main;
  }

  public static HttpServer apiServer;

  public static void test() {
    try {
      var json = "{\"actionId\":\"LOGIN\",\"sessionId\":\"\",\"body\":{\"userName\":\"lee\",\"password\":\"123\"}}";
      JsonMapper mapper = new JsonMapper();

      var req = mapper.readValue(json, Message.RequestMassage.class);
    }catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws Exception {
    // test();
    start(args);
  }

  public static void start(String[] args) throws Exception{
  
    System.out.println("please input the address of database");
    // var DBAddress = System.console().readLine();
    var DBAddress = "redtree.com";

    System.out.println("please input the password of database");
    var DBPassWord = new String(System.console().readPassword());

    C3P0Util.init(DBAddress, DBPassWord);

    // var conn = C3P0Util.getConnection();

    // var sql = " select * from t_user";

    // ArrayList<List<String>> l = new ArrayList<>();
    // try {
    //   // 获取PreparedStatement对象
    //   PreparedStatement ps = conn.prepareStatement(sql);
    //   ResultSet rs = ps.executeQuery();
    //   int col = rs.getMetaData().getColumnCount();
    //   for(int i =0;i<col;i++){
    //     String colName = rs.getMetaData().getColumnName(i+1);
    //   }
    //   while(rs.next()){
    //     ArrayList<String> list = new ArrayList<String>();
    //     for(int i=0;i<col;i++){
    //       list.add(rs.getString(i+1));
    //       Log.info(rs.getString(i+1));
    //     }
    //     l.add(list);
    //   }
    //   ps.close();
    // } catch (Exception e) {
    //   e.printStackTrace();
    // } finally {
    //   C3P0Util.close(conn);
    // }

    StartApiServer();
  }


  public static void StartApiServer(){
    apiServer = new HttpServer(8080);
    addRouts(apiServer);
    apiServer.start(() -> new HttpApiHandle());
  }


  private static void addRouts(HttpServer server) {

    final Action.A1<Object>  filter = o -> {

    };


    server.addRout("/ip", (ctx, request, content) -> {
      server.threadPool.addTask(() -> {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        final String clientIP = insocket.getAddress().getHostAddress();
        Util.saveIp(clientIP, 1);
        HttpHandle.responsString(ctx, "1", request);
      });
    });

    // 暂时不采用 SSO 方案. 
    server.addRout("/login", (ctx, request, content) -> {
      var s = content.content().toString(CharsetUtil.UTF_8);

      try{
        jsonMapper.readTree(s);
      }catch(IOException e){
        HttpHandle.responsString(ctx, "err", request);
        return;
      }

      HttpHandle.responsString(ctx, "login echo", request);
    });

    server.addRout("/easynote", (ctx, request, content) -> {

      var response = new Message.ResponseMessage<Object>();

      var messageText = content.content().toString(CharsetUtil.UTF_8);

      ObjectMapper mapper = new ObjectMapper();

      try{

        // var requestMassage = mapper.readValue(messageText, new TypeReference<Message.RequestMassage<Object>>(){});
        var messageTree = mapper.readTree(messageText);

        var actionId = messageTree.get("actionId").asText("");

        Log.info("actionId:" + actionId);

        // TODO change to hashmap
        
        if(Message.ActionId.LOGIN.toString().equals(actionId)) {
          Log.info("login");
          var loginBody = mapper.convertValue(
            messageTree,
            new TypeReference<Message.RequestMassage<Message.LoginRequest>>(){}
          );

          var userName = loginBody.body.userName;
          var password = loginBody.body.password;

          if(userName == null || password == null) {
            var loginResponse = new Message.LoginResponse();
            loginResponse.sessionId = "";
            response.isSuccess = false;
            reply(response, ctx, request);
            return;
          } else {
            //TODO encode password
            server.threadPool.addTask(() -> {
              final var sql = "select * from t_user where name = ? and password_sha = ? limit 10 ";
              try {
                var list = Util.SqlQuerryAll(sql, new Object[]{userName, password});
                if(list.size() > 0) {

                  var sessionId = UUID.randomUUID().toString();
                  var session = new Session(sessionId);
                  session.sessionMap.put("userName", userName);
                  server.sessionMap.put(sessionId, session);

                  response.isSuccess = true;
                  var body = new Message.LoginResponse();
                  body.sessionId = sessionId;
                  response.body = body;
                  reply(response, ctx, request);
                  return;
                }
              } catch (SQLException e) {}

              reply(response, ctx, request);
            });
            return;
          }
        }
      
        // filter
        var sId = messageTree.get("sessionId").asText(null);
        if(sId == null || !server.sessionMap.containsKey(sId)) {
          response.errorCode = Message.ErrorCode.NEED_LOGIN;
          reply(response, ctx, request);
          return;
        }


        if(Message.ActionId.GET_NOTE_LIST.toString().equals(actionId)) {
          Log.info("get note list");
          var uName =(String)server.sessionMap.get(sId).sessionMap.get("userName");
          server.threadPool.addTask(() -> {
            var noteListSql = "select id,content from t_note where user_id = ? order by id desc limit 1000";
            try {
              var noteList = Util.SqlQuerryAll(noteListSql, new Object[]{uName});
              var nList = new ArrayList<NoteData>();
              noteList.forEach((d) -> {
                var noteData = new NoteData();
                noteData.id =(Integer) d.get(0);
                noteData.content = (String) d.get(1);
                nList.add(noteData);
              });

              var getNoteResponse = new Message.NoteListResponse();
              getNoteResponse.noteList = nList;

              response.isSuccess = true;
              response.body = getNoteResponse;
              reply(response, ctx, request);
              return;
            } catch (SQLException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            reply(response, ctx, request);
          });
          return;
        }

        if(Message.ActionId.UPDATE_NOTE.toString().equals(actionId)) {
          Log.info("update note");
          var uName =(String)server.sessionMap.get(sId).sessionMap.get("userName");

          var updateNoteBody = mapper.convertValue(
            messageTree,
            new TypeReference<Message.RequestMassage<Message.UpdateNoteRequest>>(){}
          );

          server.threadPool.addTask(() -> {
            
            var updateSql = "update t_note set content = ? where id = ? and user_id = ?";
            try {
              var addId = Util.SqlUpdate(updateSql, new Object[]{updateNoteBody.body.content, updateNoteBody.body.noteId,uName});

              response.isSuccess = addId > 0;
              reply(response, ctx, request);
              return;
            } catch (SQLException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            reply(response, ctx, request);
          });
          return;
        }

      }catch(IOException e){
        e.printStackTrace();
        return;
      }
    });
  }

  private static final JsonMapper jsonMapper = new JsonMapper();

  private static <T> void reply(Message.ResponseMessage<T> response, ChannelHandlerContext ctx, HttpRequest request) {
    try {
      String s = jsonMapper.writeValueAsString(response);
      HttpHandle.responsString(ctx, s , request);
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    }
}
