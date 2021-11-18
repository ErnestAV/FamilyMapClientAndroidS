package com.example.familymapclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.Map;

import model.Person;
import request.LoginRequest;
import request.PersonRequest;
import result.LoginResult;
import result.PersonResult;

public class LoginTask implements Runnable {
    private String serverIP;
    private String serverPort;
    private String usernameInput;
    private String passwordInput;

    Handler handler;

    private String signInCode = "Sign In";

    Person personLoggedIn;

    public LoginTask(String serverIP, String serverPort, String usernameInput, String passwordInput, Handler handler) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.usernameInput = usernameInput;
        this.passwordInput = passwordInput;
        this.handler = handler;
    }

    @Override
    public void run() {
        LoginRequest loginRequest = new LoginRequest(usernameInput, passwordInput);

        ServerProxy serverProxy = new ServerProxy();

        LoginResult loginResult = serverProxy.login(loginRequest, serverIP, serverPort);

        DataCache dataCache = new DataCache();

        if (loginResult.getSuccess()) {
            PersonRequest personRequest = new PersonRequest();
            PersonResult personResult = serverProxy.person(personRequest, serverIP, serverPort, loginResult.getAuthtoken());

            Map<String, Person> loginMap = new HashMap<>();

            for (int i = 0; i < personResult.getData().length; i++) {
                loginMap.put(personResult.getData()[i].getPersonID(), personResult.getData()[i]);
            }
            dataCache.setPersonMap(loginMap);

            personLoggedIn = dataCache.getPersonMap().get(loginResult.getPersonID());

            sendMessage("OK. Login was successful." +
                    "\nWelcome " + personLoggedIn.getFirstName() + " " + personLoggedIn.getLastName() + "!");
        }
        else {
            sendMessage(loginResult.getMessage());
        }
    }
    public void sendMessage(String message) {
        Message newMessage = Message.obtain();
        Bundle bundle = new Bundle();

        bundle.putString(signInCode, message);
        newMessage.setData(bundle);

        handler.sendMessage(newMessage);
    }
}
