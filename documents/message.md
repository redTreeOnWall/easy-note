

massage
``` typescript

// ask

interface AskMassage{
  actionId: string;
  sessiongId: string;
  body: any;
}

// response
interface ResponseMessage{
  isSuccess: boolean;
  erroCode: 'needLogin' | 'err'
  body: any;
}

// login ask

{
  userName: string;
  password: string;
}

// login response
{
  sessionId: string;
}



```
