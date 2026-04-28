package net.veramendi.fullstackbpapi.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BusinessException {

    public NotFoundException(String entity, Object id) {
        super("%s with id '%s' not found".formatted(entity, id));
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.NOT_FOUND;
    }
}
