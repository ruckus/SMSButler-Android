package com.codycaughlan.smsbutler.activities;

import android.support.v7.app.ActionBarActivity;

import com.codycaughlan.smsbutler.events.BusProvider;

public class BaseActionBarActivity extends ActionBarActivity {

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.instance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.instance().unregister(this);
    }
    
}
