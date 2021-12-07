package com.example.familymapclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import model.Event;
import model.Person;
import request.EventRequest;
import request.LoginRequest;
import request.PersonRequest;
import result.EventResult;
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

        DataCache dataCache = DataCache.getInstance();

        if (loginResult.getSuccess()) {
            PersonRequest personRequest = new PersonRequest();
            PersonResult personResult = serverProxy.person(personRequest, serverIP, serverPort, loginResult.getAuthtoken());
            ArrayList<Person> personsToSearch = new ArrayList<>();

            // PERSON
            Map<String, Person> personMap = new HashMap<>();

            for (int i = 0; i < personResult.getData().length; i++) {
                personMap.put(personResult.getData()[i].getPersonID(), personResult.getData()[i]);
                personsToSearch.add(personResult.getData()[i]);
            }
            dataCache.setPersonsToSearch(personsToSearch);
            dataCache.setPersonMap(personMap);

            // EVENT
            EventRequest eventRequest = new EventRequest();
            EventResult eventResult = serverProxy.event(eventRequest, serverIP, serverPort, loginResult.getAuthtoken());
            ArrayList<Event> eventsToSearch = new ArrayList<>();

            Map<String, Event> eventMap = new HashMap<>();

            for (int i = 0; i < eventResult.getData().length; i++) {
                eventMap.put(eventResult.getData()[i].getEventID(), eventResult.getData()[i]);
                eventsToSearch.add(eventResult.getData()[i]);
            }
            dataCache.setEventsToSearch(eventsToSearch);
            dataCache.setEventMap(eventMap);

            // PERSON EVENTS
            for (int i = 0; i < personResult.getData().length; i++) {
                String personID = personResult.getData()[i].getPersonID();

                for (int j = 0; j < eventResult.getData().length; j++) {
                    if (eventResult.getData()[j].getPersonID().equals(personID)) {
                        if (dataCache.getAllPersonEvents().containsKey(personID)) {
                            dataCache.getAllPersonEvents().get(personID).add(eventResult.getData()[j]);
                        }
                        else {
                            ArrayList<Event> tempEventsArray = new ArrayList<>();
                            tempEventsArray.add(eventResult.getData()[j]);
                            dataCache.getAllPersonEvents().put(personID, tempEventsArray);
                        }
                    }
                }
            }

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
