package com.codycaughlan.smsbutler.events;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public final class BusProvider {

    private static final Bus BUS = new SmsButlerBus(ThreadEnforcer.ANY);

    public static Bus instance(){
        return BUS;
    }

    private BusProvider(){}

}

