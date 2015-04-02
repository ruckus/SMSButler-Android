package com.codycaughlan.smsbutler.events;

public class DidEditMessage {
    public int action;
    public int position;
    public String message;

    public DidEditMessage(int action, int position, String message) {
        this.action = action;
        this.position = position;
        this.message = message;
    }
}
