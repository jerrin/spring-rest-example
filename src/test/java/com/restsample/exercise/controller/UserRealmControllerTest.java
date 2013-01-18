package com.restsample.exercise.controller;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.xml.transform.stream.StreamResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.restsample.exercise.controller.UserRealmController;
import com.restsample.exercise.domain.ServiceError;
import com.restsample.exercise.domain.UserRealm;
import com.restsample.exercise.service.UserRealmService;
import com.restsample.exercise.util.InvalidUserRealmException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class UserRealmControllerTest{

	@Autowired
	private UserRealmController userRealmController;

	@Autowired
	private UserRealmService userRealmService;

	@Autowired
	private Jaxb2Marshaller jaxbMarshaller;

	@Autowired
	private ApplicationContext context;

	private DispatcherServlet servlet;

	@SuppressWarnings("serial")
	@Before
	public void setup() {

		servlet = new DispatcherServlet() {

			@Override
			protected WebApplicationContext createWebApplicationContext(
					WebApplicationContext parent) throws BeansException {
				GenericWebApplicationContext genericContext = new GenericWebApplicationContext();
				genericContext.setParent(context);
				genericContext.refresh();
				return genericContext;
			}

		};

		reset(userRealmService);
	}

	@Test
	public void testRead() {

		UserRealm userRealm = new UserRealm(100, "name", "description", "key");
		expect(userRealmService.getUserRealm(100)).andReturn(userRealm);
		replay(userRealmService);

		UserRealm returnedUserRealm = userRealmController.read("100");

		assertEquals(userRealm.getId(), returnedUserRealm.getId());
		assertEquals(userRealm.getName(), returnedUserRealm.getName());
		assertEquals(userRealm.getDescription(),
				returnedUserRealm.getDescription());
		assertEquals(userRealm.getKey(), returnedUserRealm.getKey());

		verify(userRealmService);

	}

	@Test
	public void testCreate() {

		UserRealm userRealm = new UserRealm("name", "description");
		UserRealm savedUserRealm = new UserRealm(101, "name", "description",
				"temp_encryption_key");

		expect(userRealmService.saveUserRealm(userRealm)).andReturn(
				savedUserRealm);
		replay(userRealmService);

		UserRealm returnedUserRealm = userRealmController.create(userRealm);

		assertEquals(savedUserRealm.getId(), returnedUserRealm.getId());
		assertEquals(savedUserRealm.getName(), returnedUserRealm.getName());
		assertEquals(savedUserRealm.getDescription(),
				returnedUserRealm.getDescription());
		assertEquals(savedUserRealm.getKey(), returnedUserRealm.getKey());

		verify(userRealmService);

	}
	

	@Test
	public void testHttpRead() throws ServletException, IOException {
		UserRealm userRealm = new UserRealm(100, "name", "description", "key");
		expect(userRealmService.getUserRealm(100)).andReturn(userRealm);
		replay(userRealmService);

		MockHttpServletRequest request = new MockHttpServletRequest("GET",
				"/user/realm/100");
		request.addHeader("Accept", "application/xml");
		MockHttpServletResponse response = new MockHttpServletResponse();

		MockServletConfig servletConfig = new MockServletConfig();

		servlet.init(servletConfig);
		servlet.service(request, response);

		StringWriter stringWriter = new StringWriter();
		StreamResult result = new StreamResult(stringWriter);
		jaxbMarshaller.marshal(userRealm, result);

		assertEquals(200, response.getStatus());
		assertEquals(stringWriter.toString(), response.getContentAsString());

		verify(userRealmService);

	}

	@Test
	public void testHttpReadUserRealmNotFound() throws ServletException, IOException {
		//UserRealm userRealm = new UserRealm(100, "name", "description", "key");
		ServiceError error = new ServiceError(HttpStatus.NOT_FOUND, "RealmNotFound");
		
		expect(userRealmService.getUserRealm(100)).andThrow(new InvalidUserRealmException(error));
		replay(userRealmService);

		MockHttpServletRequest request = new MockHttpServletRequest("GET",
				"/user/realm/100");
		request.addHeader("Accept", "application/xml");
		MockHttpServletResponse response = new MockHttpServletResponse();

		MockServletConfig servletConfig = new MockServletConfig();

		servlet.init(servletConfig);
		servlet.service(request, response);

		StringWriter stringWriter = new StringWriter();
		StreamResult result = new StreamResult(stringWriter);
		jaxbMarshaller.marshal(error, result);
		
		assertEquals(404, response.getStatus());
		assertEquals(stringWriter.toString(), response.getContentAsString());

		verify(userRealmService);

	}

	@Test
	public void testHttpReadInvalidArg() throws ServletException, IOException {
		//UserRealm userRealm = new UserRealm(100, "name", "description", "key");
		ServiceError error = new ServiceError(HttpStatus.BAD_REQUEST, "InvalidArgument");
		
		MockHttpServletRequest request = new MockHttpServletRequest("GET",
				"/user/realm/abc");
		request.addHeader("Accept", "application/xml");
		MockHttpServletResponse response = new MockHttpServletResponse();

		MockServletConfig servletConfig = new MockServletConfig();

		servlet.init(servletConfig);
		servlet.service(request, response);

		StringWriter stringWriter = new StringWriter();
		StreamResult result = new StreamResult(stringWriter);
		jaxbMarshaller.marshal(error, result);
		
		assertEquals(400, response.getStatus());
		assertEquals(stringWriter.toString(), response.getContentAsString());

	}
	
	@Test
	public void testHttpCreate() throws ServletException, IOException {

		UserRealm userRealm = new UserRealm("name", "description");
		UserRealm savedUserRealm = new UserRealm(100, "name", "description", "key");
		
		expect(userRealmService.saveUserRealm(userRealm)).andReturn(savedUserRealm);
		replay(userRealmService);
		
		StringWriter stringWriter = new StringWriter();
		StreamResult result = new StreamResult(stringWriter);
		jaxbMarshaller.marshal(userRealm, result);


		MockHttpServletRequest request = new MockHttpServletRequest("POST",
				"/user/realm");
		request.addHeader("Accept", "application/xml");
		request.addHeader("Content-Type", "application/xml");
		request.setContent(stringWriter.toString().getBytes());
		
		MockHttpServletResponse response = new MockHttpServletResponse();

		MockServletConfig servletConfig = new MockServletConfig();

		servlet.init(servletConfig);
		servlet.service(request, response);

		stringWriter = new StringWriter();
		result = new StreamResult(stringWriter);
		jaxbMarshaller.marshal(savedUserRealm, result);

		assertEquals(201, response.getStatus());
		assertEquals(stringWriter.toString(), response.getContentAsString());

		verify(userRealmService);

	}


	@Test
	public void testHttpCreateWithInvalidName() throws ServletException, IOException {
		
		ServiceError error = new ServiceError(HttpStatus.BAD_REQUEST, "InvalidRealmName");

		UserRealm userRealm = new UserRealm();
		userRealm.setDescription("description");
		
		StringWriter stringWriter = new StringWriter();
		StreamResult result = new StreamResult(stringWriter);
		jaxbMarshaller.marshal(userRealm, result);


		MockHttpServletRequest request = new MockHttpServletRequest("POST",
				"/user/realm");
		request.addHeader("Accept", "application/xml");
		request.addHeader("Content-Type", "application/xml");
		request.setContent(stringWriter.toString().getBytes());
		
		MockHttpServletResponse response = new MockHttpServletResponse();

		MockServletConfig servletConfig = new MockServletConfig();

		servlet.init(servletConfig);
		servlet.service(request, response);
		
		stringWriter = new StringWriter();
		result = new StreamResult(stringWriter);
		jaxbMarshaller.marshal(error, result);


		assertEquals(400, response.getStatus());
		assertEquals(stringWriter.toString(), response.getContentAsString());

	}

	@Test
	public void testHttpCreateDuplicateRealm() throws ServletException, IOException {
		
		ServiceError error = new ServiceError(HttpStatus.BAD_REQUEST, "DuplicateRealmName");
		
		UserRealm userRealm = new UserRealm("name", "description");
		
		expect(userRealmService.saveUserRealm(userRealm)).andThrow(new InvalidUserRealmException(error));
		replay(userRealmService);
		
		StringWriter stringWriter = new StringWriter();
		StreamResult result = new StreamResult(stringWriter);
		jaxbMarshaller.marshal(userRealm, result);


		MockHttpServletRequest request = new MockHttpServletRequest("POST",
				"/user/realm");
		request.addHeader("Accept", "application/xml");
		request.addHeader("Content-Type", "application/xml");
		request.setContent(stringWriter.toString().getBytes());
		
		MockHttpServletResponse response = new MockHttpServletResponse();

		MockServletConfig servletConfig = new MockServletConfig();

		servlet.init(servletConfig);
		servlet.service(request, response);

		stringWriter = new StringWriter();
		result = new StreamResult(stringWriter);
		jaxbMarshaller.marshal(error, result);

		assertEquals(400, response.getStatus());
		assertEquals(stringWriter.toString(), response.getContentAsString());

	}

}
