package com.codycaughlan.smsbutler.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.codycaughlan.smsbutler.Constants;
import com.codycaughlan.smsbutler.R;
import com.codycaughlan.smsbutler.realm.StockMessage;
import com.codycaughlan.smsbutler.util.TextUtil;
import com.codycaughlan.smsbutler.events.UserDidChooseMessage;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;


public class MainActivity extends BaseActionBarActivity {
    private static final String TAG = "MainActivity";
    
    @InjectView(R.id.message_edit)
    EditText mMessageEdit;
    
    @InjectView(R.id.save_button)
    Button mSave;
    
    @InjectView(R.id.choose_message)
    ImageView mChoose;
    
    @InjectView(R.id.toggle)
    ToggleButton mToggle;
    
    @InjectView(R.id.manage_messages)
    TextView mManage;
    
    @InjectView(R.id.manage_container)
    LinearLayout mManageContainer;
    
    SharedPreferences mPreferences;
    private Realm mRealm;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mPreferences = getSharedPreferences(Constants.PREFS_TITLE, Context.MODE_PRIVATE);
        ButterKnife.inject(this);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToggle.setChecked(isEnabled());
        
        mMessageEdit.setText(fetchMessage());

        IconDrawable icon = new IconDrawable(this, Iconify.IconValue.fa_inbox).colorRes(R.color.black).sizeDp(20);
        mManage.setCompoundDrawables(null, null, icon, null);
        mManage.setCompoundDrawablePadding(16);
        mManage.setPaintFlags(Paint.ANTI_ALIAS_FLAG | Paint.UNDERLINE_TEXT_FLAG);

        mSave.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = getEditMessage();
                Log.i(TAG, message);
                if(!TextUtil.isBlank(message)) {
                    dismissKeyboard();
                    persistMessage(message);
                }
            }
        });
        
        mMessageEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                dismissKeyboard();
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    persistMessage(getEditMessage());
                    handled = true;
                }
                return handled;
            }
        });
        
        mToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                toggleAutoReply(compoundButton.isChecked());
            }
        });

        mManageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchChooseMessage();
            }
        });
        //performFirstInstallActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateToggleColorState();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mRealm != null) {
            mRealm.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == Constants.DID_CHOOSE_MESSAGE) {
            if(data != null) {
                Bundle extras = data.getExtras();
                String message = extras.getString(Constants.KEY_MESSAGE_TEXT);
                mMessageEdit.setText(message);
            }
        }
    }

    private String getEditMessage() {
        return mMessageEdit.getText().toString();
    }
    
    private void persistMessage(String message) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(Constants.PREFS_AUTO_REPLY_KEY, message);
        editor.commit();
        Toast.makeText(getApplicationContext(), "Saved your message.", Toast.LENGTH_LONG).show();
    }
    
    private String fetchMessage() {
        return mPreferences.getString(Constants.PREFS_AUTO_REPLY_KEY, "");        
    }
    
    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mMessageEdit.getWindowToken(), 0);    
    }
    
    private void updateToggleColorState() {
        if(mToggle.isChecked()) {
            mToggle.setTextColor(getResources().getColor(R.color.toggle_on));
        } else {
            mToggle.setTextColor(getResources().getColor(R.color.toggle_off));
        }        
    }
    
    private void toggleAutoReply(boolean enable) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(Constants.PREFS_ENABLED, enable);
        editor.commit();
        updateToggleColorState();
    }
    
    private boolean isEnabled() {
        return mPreferences.getBoolean(Constants.PREFS_ENABLED, true);
    }

    private void launchChooseMessage() {
        Intent intent = new Intent(getApplicationContext(), ChooseMessageActivity.class);
        startActivityForResult(intent, Constants.DID_CHOOSE_MESSAGE);
    }
    
    private boolean isFirstRun() {
        return mPreferences.getBoolean(Constants.PREFS_FIRST_INSTALL, true);
    }
    
    private void setDidFirstInstall() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(Constants.PREFS_FIRST_INSTALL, false);
        editor.commit();
    }
    
    /*
    private void performFirstInstallActions() {
        if(isFirstRun()) {
            Realm.deleteRealmFile(this);

            mRealm = Realm.getInstance(this);
            mRealm.beginTransaction();

            String[] stockMessages = getResources().getStringArray(R.array.stock_messages);
            for(int i = 0; i < stockMessages.length; i++) {
                StockMessage message = mRealm.createObject(StockMessage.class);
                message.setMessage(stockMessages[i]);
                Log.i(TAG, "storing: " + stockMessages[i]);
            }
            mRealm.commitTransaction();
            
            setDidFirstInstall();
        }
    }
    */
    
    /* Event Subscriptions */
    
    @Subscribe
    public void onUserDidChooseMessage(UserDidChooseMessage event) {
        Log.i(TAG, "onUserDidChooseMessage: " + event.message);
    }

}
