package com.optimistic.lock.controller;

public class AccountOldVersionedException extends RuntimeException {


	private static final long serialVersionUID = 1L;

	public AccountOldVersionedException(Long accountId) {
		super(String.format("You are trying to update a resource that has been modified by another user first."
				+ "Account with Id  is %s.", accountId));
	}
}
