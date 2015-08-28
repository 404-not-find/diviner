package com.diviner.core.exception;

public class DivinerException extends RuntimeException {
	public DivinerException(String message) {
		super(message);
	}

	public DivinerException(String message, Throwable cause) {
		super(message, cause);
	}
}
