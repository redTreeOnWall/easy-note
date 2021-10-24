import {getNoteList} from "../service/NoteService";
import {Action} from "../utils/Action";
import {NoteData} from "../utils/request";


export interface Data{
  userInfo?: {
    userName: string;
    password: string;
    sessionId?: string;
  };
  noteList?: NoteData[]
}

export class DataManager{

  static instance: DataManager| null = null;
  static getInstance(){
    if(DataManager.instance === null){
      DataManager.instance = new DataManager();
    }
    return DataManager.instance;
  }

  // datas
  
  data: Data = {
  };

  changeData(change: (data: Data) => void){
    change(this.data);
    this.onDataChanged.invoke(this.data);
  }


  onDataChanged = new Action<Data>();

  async updateNoteList() {
    const res = await getNoteList();
    if(res?.isSuccess){
      this.changeData((d) => {
        d.noteList = res.body.noteList;
      });
    }
  }

}

