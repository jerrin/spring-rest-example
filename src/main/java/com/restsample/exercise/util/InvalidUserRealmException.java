package com.restsample.exercise.util;

import com.restsample.exercise.domain.ServiceError;

public class InvalidUserRealmException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private ServiceError error;

	public InvalidUserRealmException(ServiceError error) {
		this.error = error;
	}

	public ServiceError getError() {
		return error;
	}

}
