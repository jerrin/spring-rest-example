package com.restsample.exercise.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.restsample.exercise.domain.UserRealm;

@Component
public class UserRealmValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return UserRealm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "name", "name.empty", "name is required");
	}

}
