import React, {useState} from 'react';
import { Button, TextField , Fab } from '@material-ui/core';
import { Save } from '@material-ui/icons';
import style from './CreateNote.module.css';

const CreateNote: React.FC = () => {

  const [value, setValue] = useState<string>("");

  const handleValueChange : React.ChangeEventHandler<HTMLTextAreaElement| HTMLInputElement> = event => {
    setValue(event.target.value);
  };

  const handleSave = () => {
    console.log('saved');
  };

  return (
    <div className={style.createNote}>
      <div className={style.createNoteInner}>
        <TextField
          label=""
          multiline
          fullWidth
          value={value}
          onChange={handleValueChange}
        />

      </div>
      <Fab
        style={{
          position: 'fixed',
          bottom: '16px',
          right: '16px',
        }}
        color="primary"
        onClick={ handleSave }
        className={style.fab}
      >
        <Save/>
      </Fab>
    </div>
  );
}

export default CreateNote;
