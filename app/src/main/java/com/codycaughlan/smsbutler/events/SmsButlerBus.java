package com.codycaughlan.smsbutler.events;


// https://github.com/square/otto/issues/38

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class SmsButlerBus extends Bus {
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    public SmsButlerBus(ThreadEnforcer enforcer) {
        super(enforcer);
    }

    /*
    Post on the main thread if we are being invoked from the main thread.
    If we're not on the main thread than get a handler to the main thread
    and post it there.
     */
    @Override public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }

}
