package com.codycaughlan.smsbutler.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codycaughlan.smsbutler.Constants;
import com.codycaughlan.smsbutler.R;
import com.codycaughlan.smsbutler.events.BusProvider;
import com.codycaughlan.smsbutler.events.DidEditMessage;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EditMessage extends BaseDialogFragment {

    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.message)
    EditText mEditText;
    @InjectView(R.id.cancel)
    Button mCancel;
    @InjectView(R.id.save)
    Button mSave;
    
    private int mPosition;
    private int mAction;

    public static EditMessage newInstance(int action, int position, String message) {
        EditMessage fragment = new EditMessage();
        Bundle args = new Bundle();
        args.putInt(Constants.KEY_ACTION, action);
        args.putString(Constants.KEY_MESSAGE_TEXT, message);
        args.putInt(Constants.KEY_POSITION, position);
        fragment.setArguments(args);

        return fragment;
    }

    /*
    We override this method and specify no-title so we have more control
    over the dialog UI in our layout XML
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String message = getArguments().getString(Constants.KEY_MESSAGE_TEXT);
        mPosition = getArguments().getInt(Constants.KEY_POSITION, -1);
        mAction = getArguments().getInt(Constants.KEY_ACTION, -1);
        
        View rootView = inflater.inflate(R.layout.fragment_edit_message, null);
        ButterKnife.inject(this, rootView);

        mEditText.setText(message);
        
        if(mAction == Constants.ACTION_CREATE) {
            mTitle.setText("Create a New Message");
        } else if(mAction == Constants.ACTION_EDIT) {
            mTitle.setText("Edit your Message");
        }

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusProvider.instance().post(new DidEditMessage(mAction, mPosition, mEditText.getText().toString()));
                dismiss();
            }
        });

        return rootView;
    }

}
