package com.example.familymapclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import model.Event;
import model.Person;

public class DataCache {

    public static DataCache getInstance() {
        return instance;
    }

    private static DataCache instance = new DataCache();

    /* DECLARATIONS OF DATA STRUCTURES */
    private Map<String, Person> personMap = new HashMap<>();
    private Map<String, Event> eventMap = new HashMap<>();
    private Map<String, ArrayList<Event>> personEvents = new HashMap<>();
    private Map<String, Float> typeEventMap = new HashMap<>();

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
    public ArrayList<Event> getPersonEvents(String personID) {
        sortEvents(personEvents.get(personID));

        return personEvents.get(personID);
    }
    public Map<String, ArrayList<Event>> getAllPersonEvents() { return personEvents; }

    /* EVENT TYPES MAP */
    public Map<String, Float> getTypeEventMap() { return typeEventMap; }

    /* SORT EVENTS IN ORDER CHRONO */
    public void sortEvents(ArrayList<Event> allEvents) {
        Collections.sort(allEvents, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                if (o1.getYear() < o2.getYear()) {
                    return -1;
                }
                else if (o1.getYear() == o2.getYear()) {
                    return o1.getEventType().toLowerCase().compareTo(o2.getEventType().toLowerCase());
                }
                else {
                    return 1;
                }
            }
        });
    }

    /* HELPER FUNCTION FOR FAMILY RELATIONS */
    public ArrayList<FamilyRelations> getFamilyRelations(String personID) {
        ArrayList<FamilyRelations> tempArray = new ArrayList<>();
        Person tempPerson = personMap.get(personID);

        for (Person person : personMap.values()) {
            if (tempPerson.getFatherID() != null && tempPerson.getFatherID().equals(person.getPersonID())) {
                FamilyRelations father = new FamilyRelations(person, "Father");
                tempArray.add(father);
            }
            else if (tempPerson.getMotherID() != null && tempPerson.getMotherID().equals(person.getPersonID())) {
                FamilyRelations mother = new FamilyRelations(person, "Mother");
                tempArray.add(mother);
            }
            else if (tempPerson.getSpouseID() != null && tempPerson.getSpouseID().equals(person.getPersonID())) {
                FamilyRelations spouse = new FamilyRelations(person, "Spouse");
                tempArray.add(spouse);
            }
            else if ((person.getFatherID() != null && person.getFatherID().equals(tempPerson.getPersonID()))
                    || (person.getMotherID() != null && person.getMotherID().equals(tempPerson.getPersonID())))  {
                FamilyRelations child = new FamilyRelations(person, "Child");
                tempArray.add(child);
            }
        }

        return tempArray;
    }

    /* EVENT ACTIVITY */

    private boolean fromMap = false;

    public boolean isFromMap() {
        return fromMap;
    }

    public void setFromMap(boolean fromMap) {
        this.fromMap = fromMap;
    }


    private String eventID;

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /* SEARCH ACTIVITY */

    public ArrayList<Person> getPersonsToSearch() {
        return personsToSearch;
    }

    public void setPersonsToSearch(ArrayList<Person> personsToSearch) {
        this.personsToSearch = personsToSearch;
    }

    public ArrayList<Event> getEventsToSearch() {
        return eventsToSearch;
    }

    public void setEventsToSearch(ArrayList<Event> eventsToSearch) {
        this.eventsToSearch = eventsToSearch;
    }

    ArrayList<Person> personsToSearch = new ArrayList<>();

    ArrayList<Event> eventsToSearch = new ArrayList<>();
}
