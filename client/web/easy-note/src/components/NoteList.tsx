import React, {useEffect, useState} from 'react';
import {Data, DataManager} from '../module/data';
import request, {ActionId, NoteData, UpdateNoteRequest} from '../utils/request';
import style from './NoteList.module.css';
import Card from '@mui/material/Card';
import CreateNote from './CreateNote';

const NoteList: React.FC = () => {

  const [noteList, setNoteList] = useState<NoteData[] | undefined>([]);

  const [sessionId, setSessionId] = useState<string | undefined>(DataManager.getInstance().data.userInfo?.sessionId);

  const [clickedNote, setClickedNote] = useState<NoteData|null>(null);

  const [showEditor, setShowEditor] = useState(false);

  const handleClick = (n: NoteData) => {
    setClickedNote(n);
    setShowEditor(true);
  };
  
  useEffect(() => {
    const onDataChanged = (d: Data) => {
      setNoteList(d.noteList);

      setSessionId(d.userInfo?.sessionId);

      console.log(d.noteList);
    };

    DataManager.getInstance().onDataChanged.add(onDataChanged);

    return () => {
      DataManager.getInstance().onDataChanged.remove(onDataChanged);
    }
  }, []);

  useEffect(() => {
    DataManager.getInstance().updateNoteList();
  }, [sessionId]);

  return (
    <div className={style.noteList}>
      note list
      <br/>
      <br/>

      {
        noteList?.map(n => 
        (<Card
          key={n.id} 
          style={{minHeight: '80px', width: '90%', margin: '20px auto'}}
          onClick={() => {handleClick(n)}}
        >
          {n.content}
        </Card>))
      }
      {showEditor ? 
      <CreateNote
        onConfirm={ async (text) => {
          const id = clickedNote?.id;
          if(id === undefined ) {
            return;
          }

          const res = await request<UpdateNoteRequest, undefined>({
            actionId: ActionId.UPDATE_NOTE,
            body: {
              noteId: id,
              content: text, 
            }
          });

          if(res?.isSuccess) {
            setShowEditor(false);
            DataManager.getInstance().updateNoteList();
          }
        }}
        textFieldValue={clickedNote ? clickedNote.content : ''}
      /> 
      : null}
    </div>
  );
}

export default NoteList;
