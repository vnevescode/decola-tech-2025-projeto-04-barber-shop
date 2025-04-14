package br.com.dio.barber_shop_api.exception.custom;

/**
 * Exceção lançada quando o limite de requisições por IP é excedido.
 * Resulta em um erro HTTP 429 (Too Many Requests).
 */
public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException(String message) {
        super(message);
    }

    public TooManyRequestsException(String message, Throwable cause) {
        super(message, cause);
    }
}
