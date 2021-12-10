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

    private String loggedInUser;

    public void setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }


    /* DECLARATIONS OF DATA STRUCTURES */
    private Map<String, Person> personMap = new HashMap<>();
    private Map<String, Event> eventMap = new HashMap<>();
    private Map<String, ArrayList<Event>> personEvents = new HashMap<>();

    private Map<String, ArrayList<Event>> filteredPersonEvents = new HashMap<>();

    public Map<String, Event> getAllFilteredEvents() {
        return allFilteredEvents;
    }

    private Map<String, Event> allFilteredEvents = new HashMap<>();
    private Map<String, Float> typeEventMap = new HashMap<>();

    private ArrayList<Event> fatherSideEvents = new ArrayList<>();
    private ArrayList<Event> motherSideEvents = new ArrayList<>();
    private ArrayList<Event> maleEvents = new ArrayList<>();
    private ArrayList<Event> femaleEvents = new ArrayList<>();


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
    public Map<String, ArrayList<Event>> getAllPersonEvents() { return personEvents; }

    /* EVENT TYPES MAP */
    public Map<String, Float> getTypeEventMap() { return typeEventMap; }

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

    public void setEventsToSearch(ArrayList<Event> eventsToSearch) {
        this.eventsToSearch = eventsToSearch;
    }

    ArrayList<Person> personsToSearch = new ArrayList<>();

    ArrayList<Event> eventsToSearch = new ArrayList<>();

    /* SETTINGS ACTIVITY */

    boolean showLifeStoryLines = true;
    boolean showFamilyTreeLines = true;
    boolean showSpouseLines = true;
    boolean fatherSideToggled = true;
    boolean motherSideToggled = true;
    boolean maleEventsToggled = true;
    boolean femaleEventsToggled = true;

    public boolean isShowLifeStoryLines() {
        return showLifeStoryLines;
    }

    public void setShowLifeStoryLines(boolean showLifeStoryLines) {
        this.showLifeStoryLines = showLifeStoryLines;
    }

    public boolean isShowFamilyTreeLines() {
        return showFamilyTreeLines;
    }

    public void setShowFamilyTreeLines(boolean showFamilyTreeLines) {
        this.showFamilyTreeLines = showFamilyTreeLines;
    }

    public boolean isShowSpouseLines() {
        return showSpouseLines;
    }

    public void setShowSpouseLines(boolean showSpouseLines) {
        this.showSpouseLines = showSpouseLines;
    }

    public boolean isFatherSideToggled() {
        return fatherSideToggled;
    }

    public void setFatherSideToggled(boolean fatherSideToggled) {
        this.fatherSideToggled = fatherSideToggled;
    }

    public boolean isMotherSideToggled() {
        return motherSideToggled;
    }

    public void setMotherSideToggled(boolean motherSideToggled) {
        this.motherSideToggled = motherSideToggled;
    }

    public boolean isMaleEventsToggled() {
        return maleEventsToggled;
    }

    public void setMaleEventsToggled(boolean maleEventsToggled) {
        this.maleEventsToggled = maleEventsToggled;
    }

    public boolean isFemaleEventsToggled() {
        return femaleEventsToggled;
    }

    public void setFemaleEventsToggled(boolean femaleEventsToggled) {
        this.femaleEventsToggled = femaleEventsToggled;
    }

    /* FILTERING */

    private boolean isMaleEventFunction(Event event) {
        Person currentPerson = personMap.get(event.getPersonID());

        if (currentPerson.getGender().equalsIgnoreCase("m")) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isFemaleEventFunction(Event event) {
        Person currentPerson = personMap.get(event.getPersonID());

        if (currentPerson.getGender().equalsIgnoreCase("f")) {
            return true;
        }
        else {
            return false;
        }
    }

    public void findFatherSideEvents(String personID) {
        Person currentPerson = personMap.get(personID);
        findFatherSideEventsHelper(currentPerson.getFatherID());
    }

    private void findFatherSideEventsHelper(String personID) {
        Person currentPerson = personMap.get(personID);
        fatherSideEvents.addAll(personEvents.get(currentPerson.getPersonID()));
        if (currentPerson.getFatherID() != null) {
            findFatherSideEventsHelper(currentPerson.getFatherID());
        }
        if (currentPerson.getMotherID() != null) {
            findFatherSideEventsHelper(currentPerson.getMotherID());
        }
    }

    public void findMotherSideEvents(String personID) {
        Person currentPerson = personMap.get(personID);
        findMotherSideEventsHelper(currentPerson.getMotherID());
    }

    private void findMotherSideEventsHelper(String personID) {
        Person currentPerson = personMap.get(personID);
        motherSideEvents.addAll(personEvents.get(currentPerson.getPersonID()));
        if (currentPerson.getFatherID() != null) {
            findMotherSideEventsHelper(currentPerson.getFatherID());
        }
        if (currentPerson.getMotherID() != null) {
            findMotherSideEventsHelper(currentPerson.getMotherID());
        }
    }

    public void findMaleEvents() {
        for (Event event : eventMap.values()) {
            if (isMaleEventFunction(event)) {
                maleEvents.add(event);
            }
        }
    }

    public void findFemaleEvents() {
        for (Event event : eventMap.values()) {
            if (isFemaleEventFunction(event)) {
                femaleEvents.add(event);
            }
        }
    }

    public void filterEvents() {
        allFilteredEvents.putAll(eventMap);
        for (Event currentEvent : eventMap.values()) {
            if (!femaleEventsToggled) {
                if (femaleEvents.contains(currentEvent)) {
                    allFilteredEvents.remove(currentEvent.getEventID());
                }
            }
            if (!maleEventsToggled) {
                if (maleEvents.contains(currentEvent)) {
                    allFilteredEvents.remove(currentEvent.getEventID());
                }
            }
            if (!motherSideToggled) {
                if (motherSideEvents.contains(currentEvent)) {
                    allFilteredEvents.remove(currentEvent.getEventID());
                }
            }
            if (!fatherSideToggled) {
                if (fatherSideEvents.contains(currentEvent)) {
                    allFilteredEvents.remove(currentEvent.getEventID());
                }
            }
        }
    }

    public ArrayList<Event> filterPersonEvents(String personID) {
        ArrayList<Event> resultArray = new ArrayList<>(personEvents.get(personID));
        for (Event event : resultArray) {
            if (!femaleEventsToggled) {
                if (femaleEvents.contains(event)) {
                    return new ArrayList<>();
                }
            }
            if (!maleEventsToggled) {
                if (maleEvents.contains(event)) {
                    return new ArrayList<>();
                }
            }
            if (!motherSideToggled) {
                if (motherSideEvents.contains(event)) {
                    return new ArrayList<>();
                }
            }
            if (!fatherSideToggled) {
                if (fatherSideEvents.contains(event)) {
                    return new ArrayList<>();
                }
            }
        }
        return resultArray;
    }

    public Event getStoredEventGlobal() {
        return storedEventGlobal;
    }

    public void setStoredEventGlobal(Event storedEventGlobal) {
        this.storedEventGlobal = storedEventGlobal;
    }

    Event storedEventGlobal;
}
