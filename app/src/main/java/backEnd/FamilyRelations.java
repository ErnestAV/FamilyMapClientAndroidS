package backEnd;

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

    public String getRelationshipType() {
        return relationshipType;
    }
}
