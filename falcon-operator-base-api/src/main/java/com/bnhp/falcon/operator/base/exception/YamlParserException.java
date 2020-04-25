package com.bnhp.falcon.operator.base.exception;

public class YamlParserException extends Exception{
    public YamlParserException() {
    }

    public YamlParserException(String message) {
        super(message);
    }

    public YamlParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public YamlParserException(Throwable cause) {
        super(cause);
    }

    public YamlParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
