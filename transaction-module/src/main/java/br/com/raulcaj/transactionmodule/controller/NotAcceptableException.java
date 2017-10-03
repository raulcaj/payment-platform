package br.com.raulcaj.transactionmodule.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class NotAcceptableException extends Exception {
	private static final long serialVersionUID = -387824066493565184L;
	
	public NotAcceptableException() {
	}
	
	public NotAcceptableException(final String message) {
		super(message);
	}
}
