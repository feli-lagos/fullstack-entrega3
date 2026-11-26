package com.huertoverde.exception;

public class NoAutorizadoException extends RuntimeException {

	public NoAutorizadoException(String mensaje) {
		super(mensaje);
	}
}
