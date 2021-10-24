import React, {useState, useEffect} from 'react';
import CreateNote from './components/CreateNote';
import NoteList from './components/NoteList';
import { Button, Fab, Dialog, TextField } from '@mui/material';
import { Add } from '@mui/icons-material';
import { Login } from './components/Login';


import style from './App.module.css';
import {DataManager, Data} from './module/data';
import {login} from './service/NoteService';
import request, {ActionId} from './utils/request';



    
const sId = window.localStorage.getItem('sessionId');
if(sId !== null) {
  DataManager.getInstance().data.userInfo = {
    userName: '',
    password: '',
    sessionId: sId,
  }
}
    

const App: React.FC = () => {

  const [ showCreate , setShowCreate] = useState<boolean>(false);
  const [ showLogin , setShowLogin] = useState<boolean>(false);

  useEffect( () => {

    const onDataChanged = async (data:Data) => {
      const moduleIns = DataManager.getInstance();
      const needLogin = moduleIns.data.userInfo?.sessionId === undefined;
      if (needLogin ) {
        // try to auto login
        if(
          moduleIns.data.userInfo?.password !== undefined && 
          moduleIns.data.userInfo?.userName !== undefined
        ) {
          const res = await login(moduleIns.data.userInfo.userName as string, moduleIns.data.userInfo.password as string);
          if(res !== null && res.isSuccess) {
            moduleIns.changeData((d) => {
              if(d.userInfo !== undefined && res.body.sessionId !== undefined) {
                d.userInfo.sessionId = res.body.sessionId;
                window.localStorage.setItem('sessionId', res.body.sessionId)
              }
            });
          } else {
            alert('login faild');
            moduleIns.changeData((d) => {
              d.userInfo=undefined;
            });
          }
        } else {
          setShowLogin(true);
        }
      } else {
        setShowLogin(needLogin);
      }

    };

    DataManager.getInstance().onDataChanged.add(onDataChanged);

    onDataChanged(DataManager.getInstance().data);

    return () => {
      DataManager.getInstance().onDataChanged.remove(onDataChanged);
    }
  }, []);

  const handleAddClickled = () => {
    setShowCreate(true);
  };

  return (
    <div className={style.app}>
      { showLogin ? null : <NoteList/>}

      <Fab
        style={{
          position: 'fixed',
          right: '16px',
          bottom: '16px',
        }}
        color="primary"
        onClick={ handleAddClickled }
      >
      <Add/>
      </Fab>

      { showCreate ?  
        <CreateNote
          onConfirm={ async (text) => {
            if (text === '') {
              return;
            }

            const res = await request({
              actionId: ActionId.ADD_NOTE,
              body: {
                content: text 
              }
            });
            
            if(res?.isSuccess) {
              setShowCreate(false);
              DataManager.getInstance().updateNoteList();
            }
          }}
          textFieldValue=""
        />
      : null }

      { <Login showLogin={showLogin} />}

    </div>
  );
}

export default App;
