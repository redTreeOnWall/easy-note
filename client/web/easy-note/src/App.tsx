import React, {useState, useEffect} from 'react';
import CreateNote from './components/CreateNote';
import NoteList from './components/NoteList';
import { Button, Fab, Dialog } from '@material-ui/core';
import { Add } from '@material-ui/icons';


import style from './App.module.css';
import {Data, Module} from './module/data';


const App: React.FC = () => {

  const [ showCreate , setShowCreate] = useState<boolean>(false);
  const [ showLogin , setShowLogin] = useState<boolean>(false);

  useEffect(
    () => {
      const onDataChanged = (data:Data) => {
        const needLogin = Module.getInstance().data.userInfo === undefined;
        setShowLogin(needLogin);
      };
      Module.getInstance().onDataChanged.add(onDataChanged);
      return () => {
        Module.getInstance().onDataChanged.remove(onDataChanged);
      }
    },
    []
  );

  const handleAddClickled = () => {
    setShowCreate(true);
  };

  return (
    <div className={style.app}>
      <NoteList/>

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

      { 
        showCreate ?  <CreateNote /> : null
      }

      <Dialog
        open={showLogin}
        fullScreen
      >
        a tet
        <Button
          onClick={() =>{
            Module.getInstance().changeData( d => {
              d.userInfo={
                userName: 'name',
                password: 'pp',
                sessionId: 's',
              };
            });
          }}
        >close</Button>
      </Dialog>


    </div>
  );
}

export default App;
