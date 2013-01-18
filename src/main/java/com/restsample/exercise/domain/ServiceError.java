package com.restsample.exercise.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.http.HttpStatus;

@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceError {

	@XmlTransient
	private HttpStatus httpStatus;
	@XmlElement
	private String code;

	public ServiceError() {
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		this.code = "unknown_error";
	}

	public ServiceError(HttpStatus httpStatus, String code) {
		this.httpStatus = httpStatus;
		this.code = code;
	}
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public String getCode() {
		return code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result
				+ ((httpStatus == null) ? 0 : httpStatus.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceError other = (ServiceError) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (httpStatus != other.httpStatus)
			return false;
		return true;
	}

}
