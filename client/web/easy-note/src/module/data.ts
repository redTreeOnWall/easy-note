import {Action} from "../utils/Action";


export interface Data{
  userInfo?: {
    userName: string;
    password: string;
    sessionId: string;
  };
}

export class Module{

  static instance: Module| null = null;
  static getInstance(){
    if(Module.instance === null){
      Module.instance = new Module();
    }
    return Module.instance;
  }

  // datas
  
  data: Data = {
  };

  changeData(change: (data: Data) => void){
    change(this.data);
    this.onDataChanged.invoke(this.data);
  }


  onDataChanged = new Action<Data>();

}

