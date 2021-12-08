package com.example.familymapclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }


    /* DECLARATIONS OF DATA STRUCTURES */
    private Map<String, Person> personMap = new HashMap<>();
    private Map<String, Event> eventMap = new HashMap<>();
    private Map<String, ArrayList<Event>> personEvents = new HashMap<>();
    private Map<String, Float> typeEventMap = new HashMap<>();
    private ArrayList<Event> allFilteredParentEvents = new ArrayList<>();

    /* PERSON */
    public Map<String, Person> getPersonMap() {
        return personMap;
    }

    Person personRoot = personMap.get(getLoggedInUser());

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

    /* SETTINGS ACTIVITY */

    boolean showLifeStoryLines = true;
    boolean showFamilyTreeLines = true;
    boolean showSpouseLines = true;
    boolean showFatherSide = true;
    boolean showMotherSide = true;
    boolean showMaleEvents = true;
    boolean showFemaleEvents = true;

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

    public boolean isShowFatherSide() {
        return showFatherSide;
    }

    public void setShowFatherSide(boolean showFatherSide) {
        this.showFatherSide = showFatherSide;
    }

    public boolean isShowMotherSide() {
        return showMotherSide;
    }

    public void setShowMotherSide(boolean showMotherSide) {
        this.showMotherSide = showMotherSide;
    }

    public boolean isShowMaleEvents() {
        return showMaleEvents;
    }

    public void setShowMaleEvents(boolean showMaleEvents) {
        this.showMaleEvents = showMaleEvents;
    }

    public boolean isShowFemaleEvents() {
        return showFemaleEvents;
    }

    public void setShowFemaleEvents(boolean showFemaleEvents) {
        this.showFemaleEvents = showFemaleEvents;
    }

    public ArrayList<Event> categorizeEvents(Map<String, ArrayList<Event>> personEvents) {

        //Data Structures
        ArrayList<Event> motherSideEvents = new ArrayList<>();
        ArrayList<Event> fatherSideEvents = new ArrayList<>();
        ArrayList<Event> resultingEvents = new ArrayList<>();

        if (showFatherSide) { /* CASE FATHERS */
            String father = personMap.get(loggedInUser).getFatherID();
            fatherSideEvents = filterParents(father);

            for (int i = 0; i < fatherSideEvents.size(); i++) {
                Person currentPerson = personMap.get(fatherSideEvents.get(i).getPersonID());

                if (currentPerson.getGender().equalsIgnoreCase("m") && showMaleEvents) {
                    resultingEvents.add(fatherSideEvents.get(i));
                }
                else if (currentPerson.getGender().equalsIgnoreCase("f") && showFemaleEvents) {
                    resultingEvents.add(fatherSideEvents.get(i));
                }
            }
        }
        else if (showMotherSide) { /* CASE MOTHERS */
            String mother = personMap.get(loggedInUser).getMotherID();
            motherSideEvents = filterParents(mother);

            for (int i = 0; i < motherSideEvents.size(); i++) {
                Person currentPerson = personMap.get(motherSideEvents.get(i).getPersonID());

                if (currentPerson.getGender().equalsIgnoreCase("m") && showMaleEvents) {
                    resultingEvents.add(motherSideEvents.get(i));
                }
                else if (currentPerson.getGender().equalsIgnoreCase("f") && showFemaleEvents) {
                    resultingEvents.add(motherSideEvents.get(i));
                }
            }
        }
        else if (showMaleEvents && showFemaleEvents) { /* CASE MALE AND FEMALES */
            for (Person person : personMap.values()) {
                if (person.getGender().equalsIgnoreCase("m")) {
                    for (int i = 0; i < personEvents.get(person.getPersonID()).size(); i++) {
                        if (getAllPersonEvents().containsKey(person.getPersonID())) {
                            resultingEvents.add(getPersonEvents(person.getPersonID()).get(i));
                        }
                    }
                }
                if (person.getGender().equalsIgnoreCase("f")) {
                    for (int i = 0; i < personEvents.get(person.getPersonID()).size(); i++) {
                        if (getAllPersonEvents().containsKey(person.getPersonID())) {
                            resultingEvents.add(getPersonEvents(person.getPersonID()).get(i));
                        }
                    }
                }
            }
        }
        else if (showMaleEvents) { /* CASE MALES */
            for (Person person : personMap.values()) {
                if (person.getGender().equalsIgnoreCase("m")) {
                    for (int i = 0; i < personEvents.get(person.getPersonID()).size(); i++) {
                        if (getAllPersonEvents().containsKey(person.getPersonID())) {
                            resultingEvents.add(getPersonEvents(person.getPersonID()).get(i));
                        }
                    }
                }
            }
        }
        else if (showFemaleEvents) { /* CASE FEMALES */
            for (Person person : personMap.values()) {
                if (person.getGender().equalsIgnoreCase("f")) {
                    for (int i = 0; i < personEvents.get(person.getPersonID()).size(); i++) {
                        if (getAllPersonEvents().containsKey(person.getPersonID())) {
                            resultingEvents.add(getPersonEvents(person.getPersonID()).get(i));
                        }
                    }
                }
            }
        }

        return resultingEvents;
    }


    private ArrayList<Event> filterParents(String personID) {
        ArrayList<Event> parentEvents;

        Person tempPerson = personMap.get(personID);

        parentEvents = personEvents.get(personID);

        for (Event event : parentEvents) {
            allFilteredParentEvents.add(event);
        }

        if (tempPerson.getFatherID() != null) {
            filterParents(tempPerson.getFatherID());
        }
        if (tempPerson.getMotherID() != null) {
            filterParents(tempPerson.getMotherID());
        }

        return allFilteredParentEvents;
    }
}
