package com.lirunlong.net.tool;

import java.util.HashMap;
import java.util.Map;

public class HttpHead {
    public HttpHead(byte[] bts){
        byte[] bodySplitArr  = {'\r','\n','\r','\n'};
        int btIndex = -1;
        for(byte bt:bts){
            btIndex++;
//            System.out.print((char)bts[btIndex]);
            if(bt == '\r' || bt == '\n'){
                boolean isSplit = true;
                for(int i =0;btIndex -4 >=0 && i<4;i++){
                    if(bodySplitArr[i] != bts[btIndex -3 + i]){
                        isSplit = false;
                        break;
                    }
                }

                if(isSplit){
                    break;
                }
            }
        }

        String head = new String(bts,0,btIndex+1);
//        String body = new String(bts,btIndex +1,bts.length - btIndex -1);

        String[] lines = head.split("\r\n");

        linesMap =  new HashMap<>();

        int index = 0;
        for (String line : lines) {
            if(index ==0){
                String[] line1 = line.split(" ");
                switch (line1[0]){
                    case "GET":
                        method = HttpMethod.GET;
                        break;
                    case "POST":
                        method = HttpMethod.POST;
                        break;
                    default:
                        method = HttpMethod.OTHER;
                }
            }else{
                String[] heads = line.split(": ");
                if(heads!=null && heads.length ==2){
                    linesMap.put(heads[0],heads[1]);
                }
            }

            index++;
        }

    }
    public Map<String,String> linesMap;
    public HttpMethod method;

    public enum HttpMethod{
        GET,
        POST,
        OTHER
    }
}


