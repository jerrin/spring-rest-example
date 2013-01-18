package com.restsample.exercise.service;

import com.restsample.exercise.domain.UserRealm;

public interface UserRealmService {

	public UserRealm getUserRealm(int id);

	public UserRealm saveUserRealm(UserRealm userRealm);

}
