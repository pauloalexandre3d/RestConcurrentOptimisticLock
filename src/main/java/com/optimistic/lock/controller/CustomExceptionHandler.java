package com.optimistic.lock.controller;

import com.optimistic.lock.exception.AccountNonExistentException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(AccountNonExistentException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorResponse handleAccountNonexistentException(AccountNonExistentException ex) {
		return createErrorResponse(ex.getMessage());
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> createErrorResponse(fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
    }
	
	@ExceptionHandler(AccountOldVersionedException.class)
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	@ResponseBody
	public ErrorResponse handleAccountOldVersionedException(AccountOldVersionedException ex) {
		return createErrorResponse(ex.getMessage());
	}

	private ErrorResponse createErrorResponse(String message) {
		return new ErrorResponse(message);
	}

}