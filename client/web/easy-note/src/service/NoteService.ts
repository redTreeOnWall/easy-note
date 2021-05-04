import request from "../utils/request";


export const login = async (userName: string, passWord: string) => {
  const echo = await  request<string>({
    actionId: 'login',
    sessionId: "",
    body: {
      userName,
      passWord,
    },
  });

};

export const getNoteList = async () => {

};


