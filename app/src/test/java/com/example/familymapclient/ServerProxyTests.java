package com.example.familymapclient;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileNotFoundException;
import java.util.UUID;

import backEnd.ServerProxy;
import request.EventRequest;
import request.LoginRequest;
import request.PersonRequest;
import request.RegisterRequest;
import result.EventResult;
import result.LoginResult;
import result.PersonResult;
import result.RegisterResult;

public class ServerProxyTests {

    private static String host = "localhost";
    private static String port = "8080";
    private ServerProxy serverProxy = new ServerProxy();

    @Test
    @DisplayName("Register Valid New User Test")
    public void registerSuccess() {
        RegisterRequest registerRequest = new RegisterRequest(UUID.randomUUID().toString(), "password", "test@test.com", "test", "test", "m");
        assertNotNull(registerRequest);

        RegisterResult registerResult = serverProxy.register(registerRequest, host, port);
        assertNotNull(registerResult);

        assertEquals(true, registerResult.getSuccess());

    }

    @Test
    @DisplayName("Re-Register User Test")
    public void registerFail() {
        RegisterRequest registerRequest = new RegisterRequest(UUID.randomUUID().toString(), "password", "test@test.com", "test", "test", "m");
        assertNotNull(registerRequest);

        RegisterResult registerResult = serverProxy.register(registerRequest, host, port);
        assertEquals(null, registerResult.getMessage());
        assertTrue(registerResult.getSuccess());

        RegisterResult registerResultToReRegister = serverProxy.register(registerRequest, host, port);
        assertEquals("Error: Username is already used!", registerResultToReRegister.getMessage());
        assertFalse(registerResultToReRegister.getSuccess());
    }

    @Test
    @DisplayName("Login Valid User Test") //TODO: Can we do login like this?
    public void loginSuccessUser() {
        LoginRequest loginRequest = new LoginRequest("sheila", "parker");
        assertNotNull(loginRequest);

        LoginResult loginResult = serverProxy.login(loginRequest, host, port);
        assertNotNull(loginResult);
        assertTrue(loginResult.getSuccess());
    }

    @Test
    @DisplayName("Login User Invalid Password Test")
    public void loginInvalidUserPasswordFail() {
        LoginRequest loginRequest = new LoginRequest("sheila", "WrongPassword" + UUID.randomUUID().toString());
        assertNotNull(loginRequest);

        LoginResult loginResult = serverProxy.login(loginRequest, host, port);
        assertNotNull(loginResult);
        assertFalse(loginResult.getSuccess());
    }

    @Test
    @DisplayName("Person Valid Test")
    public void testValidPerson() {
        LoginRequest loginRequest = new LoginRequest("sheila", "parker");
        LoginResult loginResult = serverProxy.login(loginRequest, host, port);
        assertEquals("sheila", loginResult.getUsername());

        PersonRequest personRequest = new PersonRequest();
        PersonResult personResult = serverProxy.person(personRequest , host, port, loginResult.getAuthtoken());

        assertNotNull(personResult);
        assertTrue(personResult.getSuccess());
    }

    @Test
    @DisplayName("Person Invalid Test")
    public void testInvalidPerson() {
        LoginRequest loginRequest = new LoginRequest("sheila", "parker");
        LoginResult loginResult = serverProxy.login(loginRequest, host, port);
        assertNotNull(loginResult);

        PersonRequest personRequest = new PersonRequest();
        PersonResult personResult = serverProxy.person(personRequest, host, port, loginResult.getAuthtoken() + UUID.randomUUID().toString());

        assertNotNull(personResult);
        assertFalse(personResult.getSuccess());
        assertEquals("Error: Data is empty", personResult.getMessage());
    }

    @Test
    @DisplayName("Event Valid Test")
    public void testValidEvent() throws FileNotFoundException {
        LoginRequest loginRequest = new LoginRequest("sheila", "parker");
        LoginResult loginResult = serverProxy.login(loginRequest, host, port);
        assertNotNull(loginResult);

        EventRequest eventRequest = new EventRequest();
        EventResult eventResult = serverProxy.event(eventRequest, host, port, loginResult.getAuthtoken());

        assertNotNull(eventResult);
        assertTrue(eventResult.getSuccess());
    }

    @Test
    @DisplayName("Event Invalid Test")
    public void testInvalidEvent() throws FileNotFoundException {
        LoginRequest loginRequest = new LoginRequest("sheila", "parker");
        LoginResult loginResult = serverProxy.login(loginRequest, host, port);
        assertNotNull(loginResult);

        EventRequest eventRequest = new EventRequest();
        EventResult eventResult = serverProxy.event(eventRequest, host, port, loginResult.getAuthtoken() + UUID.randomUUID().toString());

        assertFalse(eventResult.getSuccess());
        assertEquals("Error: Data is empty", eventResult.getMessage());
    }
}
