package com.lirunlong.main;

import java.util.List;

public class Message {
  
  public static enum ErrorCode {
    ERR,
    SUCCESS,
    NEED_LOGIN,
  }

  public static enum ActionId {
    LOGIN,
    GET_NOTE_LIST,
    ADD_NOTE,
    UPDATE_NOTE,
  }

  public static class RequestMassage<T>{
    public ActionId actionId;
    public String sessionId;
    public T body;
  }

  public static class ResponseMessage<T> {
    public boolean isSuccess = false;
    public ErrorCode errorCode = ErrorCode.ERR; 

    public T body = null;
  }


  public static class LoginRequest {
    public String userName;
    public String password;
  }

  public static class LoginResponse {
    public String sessionId;
  }

  
  public static class NoteData {
    public int id;
    public String content;
  }

  public static class NoteListResponse {
    public List<NoteData> noteList;
  }


  public static class AddNoteRequest {
    public String content;
  }


  public static class UpdateNoteRequest {
    public int noteId;
    public String content;
  }
}
