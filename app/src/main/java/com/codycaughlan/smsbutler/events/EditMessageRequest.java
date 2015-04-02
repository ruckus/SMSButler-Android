package com.codycaughlan.smsbutler.events;

public class EditMessageRequest {
    public int position;
    public String message;

    public EditMessageRequest(int position, String message) {
        this.position = position;
        this.message = message;
    }
}
