package br.com.dio.barber_shop_api.exception;

public class PhoneInUseException extends RuntimeException {

    public PhoneInUseException(String message) {
        super(message);
    }
}
