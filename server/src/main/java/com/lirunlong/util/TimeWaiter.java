package com.lirunlong.util;

import java.util.ArrayList;
import java.util.HashSet;

public class TimeWaiter {

    public long createTime = System.currentTimeMillis();
    class TimeWaiterElem {
        public float endTime;
        public Runnable onTime;
        public boolean active = false;
    }

    HashSet<TimeWaiterElem> times = new HashSet<>();
    ArrayList<TimeWaiterElem> timesNeedRemove = new ArrayList<>();
    float nowTime;

    public void updateTime(final float time) {
        timesNeedRemove.clear();
        nowTime = time;
        for (final TimeWaiterElem t : times) {
            if (nowTime >= t.endTime) {
                t.active = false;
                t.onTime.run();
                timesNeedRemove.add(t);
            }
        }

        for (final var t : timesNeedRemove) {
            if(!t.active){
                times.remove(t);
            }
        }
    }

    public void addTimeWaiter(final float waitTime, final Runnable onTime) {
        final var t = new TimeWaiterElem();
        t.endTime = nowTime + waitTime;
        t.onTime = onTime;
        t.active = true;
        this.times.add(t);
    }
}