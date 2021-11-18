package com.example.familymapclient;

import java.util.Map;

import model.Person;

public class DataCache {

    Map<String, Person> personMap;

    public Map<String, Person> getPersonMap() {
        return personMap;
    }

    public void setPersonMap(Map<String, Person> personMap) {
        this.personMap = personMap;
    }

}
