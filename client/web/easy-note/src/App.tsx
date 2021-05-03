import React, {useState} from 'react';
import CreateNote from './components/CreateNote';
import NoteList from './components/NoteList';
import { Button, Fab } from '@material-ui/core';
import { Add } from '@material-ui/icons';


import style from './App.module.css';


const App: React.FC = () => {

  const [ showCreate , setShowCreate] = useState<boolean>(false);

  const handleAddClickled = () => {
    setShowCreate(true);
  };

  return (
    <div className={style.app}>
      <NoteList/>

      <Fab color="primary" onClick={ handleAddClickled }>
        <Add/>
      </Fab>

      { 
        showCreate ?  <CreateNote /> : null
      }
    </div>
  );
}

export default App;
