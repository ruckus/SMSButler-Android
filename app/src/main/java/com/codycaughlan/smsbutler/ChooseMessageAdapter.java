package com.codycaughlan.smsbutler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codycaughlan.smsbutler.realm.StockMessage;
import com.codycaughlan.smsbutler.viewholder.EmptyViewHolder;
import com.codycaughlan.smsbutler.viewholder.StockMessageHolder;

import java.util.List;

public class ChooseMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<StockMessage> mMessages;
    private static final int EMPTY_VIEW = 10;
    
    public ChooseMessageAdapter(List<StockMessage> messages) {
        mMessages = messages;
    }

    @Override
    public int getItemCount() {
        return mMessages.size() > 0 ? mMessages.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessages.size() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }

    public void removeItem(int position) {
        mMessages.remove(position);
    }
    
    public StockMessage get(int index) {
        return mMessages.get(index);        
    }
    
    public void save(int index, StockMessage item) {
        mMessages.set(index, item);
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        
        if(viewType == EMPTY_VIEW) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_message_empty_view, parent, false);
            EmptyViewHolder evh = new EmptyViewHolder(v);
            return evh;
        }
        
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_stock_message, parent, false);
        return new StockMessageHolder(view);    
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
        if(holder instanceof StockMessageHolder) {
            StockMessage message = mMessages.get(pos);
            ((StockMessageHolder)holder).bindStockMessage(message);
        }
    }

}
