package com.example.graduationprojectsingle.exception;

import java.io.Serial;

public class ServiceException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 5705473305050398167L;

    private ConsumerException consumerException;

    public ServiceException(ConsumerException consumerException) {
        this.consumerException = consumerException;
    }

    public ConsumerException getConsumerException() {
        return consumerException;
    }
}
