package com.codycaughlan.smsbutler.realm;

import io.realm.RealmObject;

public class StockMessage extends RealmObject {
    
    private String message;

    public StockMessage() {
    }

    public StockMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
