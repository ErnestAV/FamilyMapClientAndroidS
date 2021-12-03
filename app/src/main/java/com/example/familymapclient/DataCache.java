package com.example.familymapclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.Event;
import model.Person;

public class DataCache {

    public static DataCache getInstance() {
        return instance;
    }

    private static DataCache instance = new DataCache();

    /* DECLARATIONS OF DATA STRUCTURES */
    Map<String, Person> personMap = new HashMap<>();
    Map<String, Event> eventMap = new HashMap<>();
    Map<String, ArrayList<Event>> personEvents = new HashMap<>();
    Map<String, Float> typeEventMap = new HashMap<>();

    /* PERSON */
    public Map<String, Person> getPersonMap() {
        return personMap;
    }

    public void setPersonMap(Map<String, Person> personMap) {
        this.personMap = personMap;
    }

    /* EVENT */
    public Map<String, Event> getEventMap() {
        return eventMap;
    }

    public void setEventMap(Map<String, Event> eventMap) {
        this.eventMap = eventMap;
    }

    /* PERSON EVENTS Data Structures */
    public Map<String, ArrayList<Event>> getPersonEvents() {
        return personEvents;
    }

    /* RANDOM EVENTS */
    public Map<String, Float> getTypeEventMap() { return typeEventMap; }
}
