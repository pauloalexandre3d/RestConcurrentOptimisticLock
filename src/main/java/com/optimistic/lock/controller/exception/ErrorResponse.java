package com.optimistic.lock.controller.exception;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ErrorResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String error;

	public ErrorResponse(String error) {
		this.error = error;
	}
}
