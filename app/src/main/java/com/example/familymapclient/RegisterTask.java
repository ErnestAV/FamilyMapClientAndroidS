package com.example.familymapclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.Map;

import model.Person;
import request.PersonRequest;
import request.RegisterRequest;
import result.PersonResult;
import result.RegisterResult;

public class RegisterTask implements Runnable {
    private String serverPort;
    private String serverIP;
    private String usernameInput;
    private String passwordInput;
    private String firstName;
    private String lastName;
    private String emailInput;
    private String gender;

    Handler handler;

    private String registerCode = "Register";

    Person personRegistered;

    public RegisterTask(String serverPort, String serverIP, String usernameInput, String passwordInput, String firstName, String lastName, String emailInput, String gender, Handler handler) {
        this.serverPort = serverPort;
        this.serverIP = serverIP;
        this.usernameInput = usernameInput;
        this.passwordInput = passwordInput;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailInput = emailInput;
        this.gender = gender;
        this.handler = handler;
    }

    @Override
    public void run() {
        RegisterRequest registerRequest = null;

        registerRequest.setUsername(usernameInput);
        registerRequest.setPassword(passwordInput);
        registerRequest.setFirstName(firstName);
        registerRequest.setLastName(lastName);
        registerRequest.setEmail(emailInput);
        registerRequest.setGender(gender);

        ServerProxy serverProxy = new ServerProxy();

        RegisterResult registerResult = serverProxy.register(registerRequest, serverIP, serverPort);

        DataCache dataCache = new DataCache();

        if (registerResult.getSuccess()) {
            PersonRequest personRequest = new PersonRequest();
            PersonResult personResult = serverProxy.person(personRequest, serverIP, serverPort, registerResult.getAuthtoken());

            Map<String, Person> registerMap = new HashMap<>();

            for (int i = 0; i < personResult.getData().length; i++) {
                registerMap.put(personResult.getData()[i].getPersonID(), personResult.getData()[i]);
            }
            dataCache.setPersonMap(registerMap);

            personRegistered = dataCache.getPersonMap().get(registerResult.getPersonID());

            sendMessage("OK. Register was successful. " +
                    "\nWelcome " + personRegistered.getFirstName() + " " + personRegistered.getLastName() + "!");
        }
        else {
            sendMessage(registerResult.getMessage());
        }
    }

    public void sendMessage(String message) {
        Message newMessage = Message.obtain();
        Bundle bundle = new Bundle();

        bundle.putString(registerCode, message);
        newMessage.setData(bundle);

        handler.sendMessage(newMessage);
    }
}
