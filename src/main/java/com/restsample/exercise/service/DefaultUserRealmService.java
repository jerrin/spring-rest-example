package com.restsample.exercise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restsample.exercise.dao.UserRealmDAO;
import com.restsample.exercise.domain.ServiceError;
import com.restsample.exercise.domain.UserRealm;
import com.restsample.exercise.util.InvalidUserRealmException;

@Service("userRealmService")
public class DefaultUserRealmService implements UserRealmService{
	
	@Autowired
	private UserRealmDAO userRealmDAO;
	
	public UserRealm getUserRealm(int id){
		UserRealm userRealm = null;
		try{
			userRealm = userRealmDAO.find(id);
		}catch(EmptyResultDataAccessException ex){
			throw new InvalidUserRealmException(new ServiceError(HttpStatus.NOT_FOUND, "RealmNotFound"));
		}
		return userRealm;
	}

	@Transactional
	public UserRealm saveUserRealm(UserRealm userRealm){
		userRealm.setKey(generateEncryptionKey());
		UserRealm returnedUserRealm = userRealmDAO.save(userRealm);
		if(returnedUserRealm.equals(userRealm))
			throw new InvalidUserRealmException(new ServiceError(HttpStatus.BAD_REQUEST, "DuplicateRealmName"));
		return returnedUserRealm;
	}

	
	private String generateEncryptionKey(){
		// TODO
		return "temp_encryption_key";
	}

}
