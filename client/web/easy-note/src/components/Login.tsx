import {Button, Dialog, TextField} from '@mui/material';
import React, {useState} from 'react';
import {DataManager} from '../module/data';

interface LoginProps{
  showLogin: boolean;
}

export const Login: React.FC<LoginProps> = (props) => {

  const [userNameText, setUserNameText] = useState<string>('');
  const [passwordText, setPasswordText] = useState<string>('');

  return (
    <Dialog
      open={props.showLogin}
      fullScreen
    >
      login
      <br />
      <br />

      <TextField
        variant="outlined"
        label="User name"
        onChange={(e) => {setUserNameText(e.target.value)}}
      />
      <br />

      <TextField
        variant="outlined"
        label="Password"
        type="password"
        onChange={(e) => {setPasswordText(e.target.value)}}
      />
      <br />

      <Button
        onClick={() => {
          DataManager.getInstance().changeData(d => {
            d.userInfo = {
              userName: userNameText,
              password: passwordText,
              sessionId: undefined,
            };
          });
        }}
        variant="contained"
      >
        login
      </Button>
    </Dialog>

  );


};

function setUserNameText(value: string) {
  throw new Error('Function not implemented.');
}
