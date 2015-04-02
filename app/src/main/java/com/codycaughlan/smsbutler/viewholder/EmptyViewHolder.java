package com.codycaughlan.smsbutler.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.codycaughlan.smsbutler.R;
import com.codycaughlan.smsbutler.events.BusProvider;
import com.codycaughlan.smsbutler.events.CreateMessageRequest;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

public class EmptyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    
    private TextView mMessage;
    
    public EmptyViewHolder(View itemView) {
        super(itemView);
        
        mMessage = (TextView)itemView.findViewById(R.id.empty_view_add_message);
        mMessage.setOnClickListener(this);

        IconDrawable plusIcon = new IconDrawable(itemView.getContext(), Iconify.IconValue.fa_plus_circle)
                .colorRes(R.color.black)
                .sizeDp(20);
        mMessage.setCompoundDrawables(plusIcon, null, null, null);
    }

    @Override
    public void onClick(View view) {
        BusProvider.instance().post(new CreateMessageRequest());
    }
}
