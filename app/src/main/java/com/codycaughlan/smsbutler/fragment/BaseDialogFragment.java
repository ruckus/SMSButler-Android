package com.codycaughlan.smsbutler.fragment;

import android.app.ProgressDialog;
import android.support.v4.app.DialogFragment;

import com.codycaughlan.smsbutler.events.BusProvider;
import com.codycaughlan.smsbutler.util.AlertUtil;

public class BaseDialogFragment extends DialogFragment {
    protected ProgressDialog mProgressDialog;

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.instance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.instance().unregister(this);
    }

    protected void startProgressSpinner(String title, String message) {
        mProgressDialog = AlertUtil.createProgressSpinner(getActivity(), title, message);
    }

    protected void stopProgressSpinner() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

}
