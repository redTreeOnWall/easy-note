package com.lirunlong.util;

public class Log{

    public static int thisLevel = 5;
    public static void info(Object o){
        info(o,0);
    }

    public static void info(Object o,int level){
        if(level <= thisLevel){
            System.out.println(o);
        }
    }
}