package com.restsample.exercise.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.restsample.exercise.domain.ServiceError;
import com.restsample.exercise.domain.UserRealm;
import com.restsample.exercise.service.UserRealmService;
import com.restsample.exercise.util.InvalidUserRealmException;
import com.restsample.exercise.util.UserRealmValidator;

@Controller
@RequestMapping("/user/realm/**")
public class UserRealmController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	// Resource annotation will not work in JBoss as it has its own implementation
	/* Resource annotation is required for unit testing as EasyMock will create 
	proxy object for UserRealService and it can be autowired by name only*/ 
	@Resource(name = "userRealmService")
	@Autowired
	private UserRealmService userRealmService;
	
	@Autowired
	private UserRealmValidator userRealmValidator;
	
	
	@RequestMapping(value = "/user/realm", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody UserRealm create(@RequestBody @Valid UserRealm userRealm) {
		
		log.debug("create user realm");
		
		final BindingResult result = new BeanPropertyBindingResult(userRealm, "");
		userRealmValidator.validate(userRealm, result);

		if (result.hasErrors()) {
			throw new InvalidUserRealmException(new ServiceError(HttpStatus.BAD_REQUEST, "InvalidRealmName"));
        }
				
		log.debug("user realm object: " + userRealm.toString());
		
		userRealm = userRealmService.saveUserRealm(userRealm);
		
		
		return userRealm;
	}
	
	
	@RequestMapping(value = "/user/realm/{id}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody UserRealm read(@PathVariable("id") String strID){
		
		log.debug("get user realm");
		int id = 0;
		try{
			id = Integer.parseInt(strID);
		}catch(NumberFormatException ex){
			throw new InvalidUserRealmException(new ServiceError(HttpStatus.BAD_REQUEST, "InvalidArgument"));
		}
		
		UserRealm userRealm = userRealmService.getUserRealm(id);
		
		log.debug("user realm object: " + userRealm.toString());
		
		return userRealm;
	}
	
	@ExceptionHandler(InvalidUserRealmException.class)
	public @ResponseBody ServiceError handleException(InvalidUserRealmException exception, HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("error", exception.getError());
		response.setStatus(exception.getError().getHttpStatus().value());
		return exception.getError();
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public @ResponseBody ServiceError handleException(HttpMessageNotReadableException exception, HttpServletRequest request, HttpServletResponse response) {
		ServiceError error = new ServiceError(HttpStatus.BAD_REQUEST, "InvalidRequestData");
		request.setAttribute("error", error);
		response.setStatus(error.getHttpStatus().value());
		return error;
	}

	@ExceptionHandler(Exception.class)
	public @ResponseBody ServiceError handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
		ServiceError error = new ServiceError();
		request.setAttribute("error", error);
		response.setStatus(error.getHttpStatus().value());
		return error;
	}
}


