package com.example.familymapclient;

import model.Person;

public class FamilyRelations {
    private Person personSelected;
    private String relationshipType;

    public FamilyRelations(Person personSelected, String relationshipType) {
        this.personSelected = personSelected;
        this.relationshipType = relationshipType;
    }

    public Person getPersonSelected() {
        return personSelected;
    }

    public void setPersonSelected(Person personSelected) {
        this.personSelected = personSelected;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }
}
