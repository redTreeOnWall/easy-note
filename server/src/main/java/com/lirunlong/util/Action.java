package com.lirunlong.util;

public interface Action{
    public interface A1<T>{
        public void invoke(T t);
    }

    public interface A2<T1,T2>{
        public void invoke(T1 t1,T2 t2);
    }

    public interface A3<T1,T2,T3>{
        public void invoke(T1 t1,T2 t2,T3 t3);
    }

    public interface A4<T1,T2,T3,T4>{
        public void invoke(T1 t1,T2 t2,T3 t3,T4 t4);
    }
}
