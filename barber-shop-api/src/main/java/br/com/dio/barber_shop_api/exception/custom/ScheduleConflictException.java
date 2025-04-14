package br.com.dio.barber_shop_api.exception.custom;

public class ScheduleConflictException extends RuntimeException {
    public ScheduleConflictException(String message) {
        super(message);
    }
}
