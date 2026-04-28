package net.veramendi.fullstackbpapi.exception;

import org.springframework.http.HttpStatus;

public class DailyLimitExceededException extends BusinessException {

    public DailyLimitExceededException() {
        super("Daily withdrawal limit exceeded");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }
}
