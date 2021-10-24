import React, {useEffect, useState} from 'react';
import {Button, TextField, Fab} from '@mui/material';
import {Save} from '@mui/icons-material';
import style from './CreateNote.module.css';
import request, {ActionId} from '../utils/request';

export interface CreateProps{
  onConfirm: (text: string) =>void;
  textFieldValue: string;
}

const CreateNote: React.FC<CreateProps> = (props) => {

  const [value, setValue] = useState<string>('');

  useEffect(() => {
    setValue(props.textFieldValue);
  },[props.textFieldValue]);

  const handleValueChange: React.ChangeEventHandler<HTMLTextAreaElement | HTMLInputElement> = event => {
    setValue(event.target.value);
  };

  const handleSave = async () => {
   props.onConfirm(value);
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
      
      <Button
        style={{width: '90%', margin: '20px 5%'}}
        variant="contained"
        onClick={handleSave}
        >
        <Save />
      </Button>
    </div>
  );
}

export default CreateNote;
