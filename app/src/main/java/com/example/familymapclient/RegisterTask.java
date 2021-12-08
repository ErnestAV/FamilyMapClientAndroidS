package com.example.familymapclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.Event;
import model.Person;
import request.EventRequest;
import request.PersonRequest;
import request.RegisterRequest;
import result.EventResult;
import result.PersonResult;
import result.RegisterResult;

public class RegisterTask implements Runnable {
    private String serverIP;
    private String serverPort;
    private String usernameInput;
    private String passwordInput;
    private String firstName;
    private String lastName;
    private String emailInput;
    private String gender;

    Handler handler;

    private String registerCode = "Register";

    Person personRegistered;

    public RegisterTask(String serverIP, String serverPort, String usernameInput, String passwordInput, String firstName, String lastName, String emailInput, String gender, Handler handler) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
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
        RegisterRequest registerRequest = new RegisterRequest(usernameInput, passwordInput, emailInput, firstName, lastName, gender);

        ServerProxy serverProxy = new ServerProxy();

        RegisterResult registerResult = serverProxy.register(registerRequest, serverIP, serverPort);

        DataCache dataCache = DataCache.getInstance();

        if (registerResult.getSuccess()) {
            PersonRequest personRequest = new PersonRequest();
            PersonResult personResult = serverProxy.person(personRequest, serverIP, serverPort, registerResult.getAuthtoken());
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
            EventResult eventResult = serverProxy.event(eventRequest, serverIP, serverPort, registerResult.getAuthtoken());
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

            personRegistered = dataCache.getPersonMap().get(registerResult.getPersonID());
            dataCache.setLoggedInUser(personRegistered.getPersonID());

            sendMessage("OK. Register was successful." +
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
