import {DataManager} from "../module/data";

export enum ErrorCode {
  ERR = 'ERR',
  SUCCESS = 'SUCCESS',
  NEED_LOGIN = 'NEED_LOGIN',
}

export enum ActionId {
  LOGIN = 'LOGIN',
  GET_NOTE_LIST = 'GET_NOTE_LIST',
  ADD_NOTE = 'ADD_NOTE',
  UPDATE_NOTE = 'UPDATE_NOTE',
}


export interface LoginRequest {
  userName: string;
  password: string;
}

export interface LoginResponse {
  sessionId?: string;
}


export interface RequestMessage<T>{
  actionId: ActionId;
  sessionId?: string;
  body: T;
};

export interface ResponseMessage<T>{
  isSuccess: boolean;
  errorCode: ErrorCode;
  body: T;
};


// 
export interface NoteData {
  id: number;
  content: string;
}

export interface NoteListResponse {
  noteList?: NoteData[];
}



export interface AddNoteRequest {
  content: string;
}


export interface UpdateNoteRequest {
  noteId: number;
  content: string;
}

const url = 'http://127.0.0.1:8080/easynote';

const request = async <REQUEST, RESPONSE>(data: RequestMessage<REQUEST>) => {
  console.log('asking:', data);
  if(data.actionId !== ActionId.LOGIN) {
    const sId = DataManager.getInstance().data.userInfo?.sessionId;
    if(sId === undefined) {
      return null;
    }

    data.sessionId = sId;
  }

	const response = await fetch(url, {
		method: 'POST', 
		cache: 'no-cache', 
		headers: {
			'Content-Type': 'application/text'
		},
		body: JSON.stringify(data)
	});
	const responseData = ( await response.json()) as ResponseMessage<RESPONSE>;

  console.log('echo:', responseData);
  if(responseData.errorCode === ErrorCode.NEED_LOGIN) {
    DataManager.getInstance().changeData(d => {
      d.userInfo = undefined;
    });
  }
  return responseData;
} 

export default  request;
