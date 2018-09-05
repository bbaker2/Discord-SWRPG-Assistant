package com.bbaker.exceptions;

public class BadArgumentException extends Exception {


    public BadArgumentException(String messageTemplate, Object... args) {
        super(String.format(messageTemplate, args));
    }

}
