package br.com.dio.barber_shop_api.exception;

public class EmailInUseException extends RuntimeException{

    public EmailInUseException(String message) {
        super(message);
    }
}
