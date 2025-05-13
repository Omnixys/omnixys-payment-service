package com.omnixys.payment.exception;

public class InsufficientFundsException extends RuntimeException {

  public InsufficientFundsException() {
    super("Du hast nicht genügend Geld");
  }
}
