import request, {ActionId, LoginRequest, LoginResponse, NoteListResponse} from "../utils/request";

export const login = async (userName: string, password: string) => {
  const res = await request<LoginRequest,LoginResponse>({
    actionId: ActionId.LOGIN,
    body: {
      userName,
      password,
    },
  });

  return res;

};

export const getNoteList = async () => {
  const res = await request<undefined, NoteListResponse>({
    actionId: ActionId.GET_NOTE_LIST,
    body: undefined,
  });

  return res;
};


