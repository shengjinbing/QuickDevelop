package com.modesty.utils.inject;

public class InjectException extends RuntimeException {

    private static final long serialVersionUID = 6764570896457507348L;

    public InjectException() {
        super();
    }

    public InjectException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InjectException(String detailMessage) {
        super(detailMessage);
    }

    public InjectException(Throwable throwable) {
        super(throwable);
    }

}
