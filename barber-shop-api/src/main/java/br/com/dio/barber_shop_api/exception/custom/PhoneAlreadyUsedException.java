package br.com.dio.barber_shop_api.exception.custom;

public class PhoneAlreadyUsedException extends RuntimeException {
  public PhoneAlreadyUsedException(String message) {
    super(message);
  }
}