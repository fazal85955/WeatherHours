package com.apps.sfrcreativity.weatherhours.utils;

import android.os.Handler;

public class Sync {
    private boolean start;
    private Handler someHandler;
    private Runnable runnable;
    private OnUpdateListener onUpdateListener;
    private long updateTime;

    public interface OnUpdateListener {
        void onUpdate();
    }

    public boolean isRunning() {
        return start;
    }

    public Sync() {
    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public void setMilliSeconds(long i) {
        updateTime = i;
    }

    public void turnOffSync() {
        if(start) {
            start = false;
            runnable = null;
            someHandler.removeCallbacks(runnable, updateTime);
        }
    }

    public void turnOnSync() {
        if(runnable==null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (start) {
                        onUpdateListener.onUpdate();
                        someHandler.postDelayed(this, updateTime);
                    } else {
                        someHandler.removeCallbacks(this);
                    }
                }
            };
        }
        start = true;
        someHandler = new Handler(/*ctx.getMainLooper()*/);
        someHandler.postDelayed(runnable, updateTime);
    }

}
