package com.codycaughlan.smsbutler.util;

public class TextUtil {
    
    public static boolean isBlank(String text) {
        return(text == null || text.trim().length() == 0);
    }
}
