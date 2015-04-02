package com.codycaughlan.smsbutler.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.codycaughlan.smsbutler.ChooseMessageAdapter;
import com.codycaughlan.smsbutler.Constants;
import com.codycaughlan.smsbutler.R;
import com.codycaughlan.smsbutler.events.CreateMessageRequest;
import com.codycaughlan.smsbutler.events.DidEditMessage;
import com.codycaughlan.smsbutler.events.EditMessageRequest;
import com.codycaughlan.smsbutler.events.MessageChosen;
import com.codycaughlan.smsbutler.fragment.EditMessage;
import com.codycaughlan.smsbutler.realm.StockMessage;
import com.du.android.recyclerview.SwipeToDismissTouchListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChooseMessageActivity extends BaseActionBarActivity {
    
    private static final String TAG = "ChooseMessageActivity";
    private RecyclerView mList;
    private ChooseMessageAdapter mAdapter;
    private RealmResults<StockMessage> mMessages;
    private Realm mRealm;
    private SwipeToDismissTouchListener mSwipeListener;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_message);
        
        setTitle("Manage Messages");
        
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mRealm = Realm.getInstance(this);

        mList = (RecyclerView)findViewById(R.id.list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setLayoutManager(layoutManager);

        refreshMessagesList();
        mAdapter = new ChooseMessageAdapter(mMessages);
        mList.setAdapter(mAdapter);

        mSwipeListener = new SwipeToDismissTouchListener(mList, new SwipeToDismissTouchListener.DismissCallbacks() {
            @Override
            public SwipeToDismissTouchListener.SwipeDirection canDismiss(int position) {
                return SwipeToDismissTouchListener.SwipeDirection.RIGHT;
            }
            @Override
            public void onDismiss(RecyclerView view, List<SwipeToDismissTouchListener.PendingDismissData> dismissData) {
                mRealm.beginTransaction();
                for (SwipeToDismissTouchListener.PendingDismissData data : dismissData) {
                    mMessages.remove(data.position);
                    mAdapter.notifyItemRemoved(data.position);
                }
                mRealm.commitTransaction();
                Toast.makeText(getApplicationContext(), "Deleted that message.", Toast.LENGTH_LONG).show();
            }
        });
        
        mList.addOnItemTouchListener(mSwipeListener);
    }
    
    private void refreshMessagesList() {
        mMessages = mRealm.where(StockMessage.class).findAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.choose_message_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add:
                promptForMessageInput();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }   
    
    private void promptForMessageInput() {
        FragmentManager fm = getSupportFragmentManager();
        EditMessage frag = EditMessage.newInstance(Constants.ACTION_CREATE, 0, null);
        frag.show(fm, "editor");
    }
    
    /* Event Subscriptions */
    
    @Subscribe
    public void onMessageChosen(MessageChosen event) {
        Log.i(TAG, "onMessageChosen: " + event.message);
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.KEY_MESSAGE_TEXT, event.message);
        setResult(Constants.DID_CHOOSE_MESSAGE, resultIntent);
        finish();
    }
    
    @Subscribe
    public void onEditMessageRequest(EditMessageRequest event) {
        Log.i(TAG, "onEditMessageRequest");
        FragmentManager fm = getSupportFragmentManager();
        EditMessage frag = EditMessage.newInstance(Constants.ACTION_EDIT, event.position, event.message);
        frag.show(fm, "editor");        
    }

    @Subscribe
    public void onCreateMessageRequest(CreateMessageRequest event) {
        Log.i(TAG, "onCreateMessageRequest");
        FragmentManager fm = getSupportFragmentManager();
        EditMessage frag = EditMessage.newInstance(Constants.ACTION_CREATE, 0, null);
        frag.show(fm, "editor");
    }

    @Subscribe
    public void onDidEditMessage(DidEditMessage event) {
        Log.i(TAG, "onDidEditMessage");
        
        if(event.action == Constants.ACTION_CREATE) {
            // Its an Add New
            mRealm.beginTransaction();
            mRealm.createObject(StockMessage.class).setMessage(event.message);
            mRealm.commitTransaction();
            refreshMessagesList();
        } else if(event.action == Constants.ACTION_EDIT) {
            StockMessage item = mAdapter.get(event.position);
            mRealm.beginTransaction();
            item.setMessage(event.message);
            mRealm.commitTransaction();
            mAdapter.notifyItemChanged(event.position);            
        }
    }
}