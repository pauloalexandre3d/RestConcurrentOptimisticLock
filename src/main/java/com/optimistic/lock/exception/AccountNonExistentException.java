package com.optimistic.lock.exception;

public class AccountNonExistentException extends RuntimeException {


	private static final long serialVersionUID = 1L;

	public AccountNonExistentException(Long accountId) {
		super(String.format("Account with Id  %s not exists.", accountId));
	}
}
