package com.example.familymapclient;

import java.util.Map;

import model.Person;

public class DataCache {

    Map<String, Person> familyTree;

    public Map<String, Person> getFamilyTree() {
        return familyTree;
    }

    public void setFamilyTree(Map<String, Person> familyTree) {
        this.familyTree = familyTree;
    }

}
