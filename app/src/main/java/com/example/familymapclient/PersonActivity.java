package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {

    Person personSelected;

    String personID;
    String gender;

    DataCache dataCache = DataCache.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Bundle bundleExtras = getIntent().getExtras();

        if (bundleExtras != null) {
            personID = bundleExtras.getString("PersonID");
        }

        personSelected = dataCache.getPersonMap().get(personID);

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        ArrayList<FamilyRelations> familyRelations = dataCache.getFamilyRelations(personID);
        ArrayList<Event> allEvents = dataCache.getFilteredPersonEvents().get(personSelected.getPersonID());

        expandableListView.setAdapter(new ExpandableListAdapter(allEvents, familyRelations));

        /* Variables for UI*/
        TextView firstNameTextView = findViewById(R.id.ActualFirstName);
        TextView lastNameTextView = findViewById(R.id.ActualLastName);
        TextView genderTextView = findViewById(R.id.ActualGender);

        if (personSelected != null) {
            firstNameTextView.setText(personSelected.getFirstName());
            lastNameTextView.setText(personSelected.getLastName());

            if (personSelected.getGender().equalsIgnoreCase("m")) {
                gender = "Male";
            } else {
                gender = "Female";
            }
            genderTextView.setText(gender);
        }
        else {
            System.out.println("Person is null");
        }

    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private static final int LIFE_EVENTS_GROUP_POSITION = 0;
        private static final int FAMILY_RELATIONS_GROUP_POSITION = 1;

        private final ArrayList<Event> lifeEvents;
        private final ArrayList<FamilyRelations> familyRelations;

        ExpandableListAdapter(ArrayList<Event> lifeEvents, ArrayList<FamilyRelations> familyRelations) {
            this.lifeEvents = lifeEvents;
            this.familyRelations = familyRelations;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    return lifeEvents.size();
                case FAMILY_RELATIONS_GROUP_POSITION:
                    return familyRelations.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    return getString(R.string.lifeEventsTitle);
                case FAMILY_RELATIONS_GROUP_POSITION:
                    return getString(R.string.familyRelationsTitle);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    return lifeEvents.get(childPosition);
                case FAMILY_RELATIONS_GROUP_POSITION:
                    return familyRelations.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    titleView.setText(R.string.lifeEventsTitle);
                    break;
                case FAMILY_RELATIONS_GROUP_POSITION:
                    titleView.setText(R.string.familyRelationsTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item, parent, false);
                    initializeLifeEventsView(itemView, childPosition);
                    break;
                case FAMILY_RELATIONS_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_item, parent, false);
                    initializeFamilyRelationsView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group: " + groupPosition);
            }
            return itemView;
        }

        public void initializeLifeEventsView(View lifeEventsItemView, final int childPosition) {
            ImageView lifeEventImageView = lifeEventsItemView.findViewById(R.id.lifeEventImageView);
            lifeEventImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_marker_event, null));

            TextView lifeEventNameView = lifeEventsItemView.findViewById(R.id.lifeEventTextView);
            lifeEventNameView.setText(lifeEvents.get(childPosition).getEventType()
                    + "; " + lifeEvents.get(childPosition).getCity()
                    + ", " + lifeEvents.get(childPosition).getCountry()
                    + "(" + lifeEvents.get(childPosition).getYear() + ")"
                    + "\n" + personSelected.getFirstName() + " " + personSelected.getLastName());

            lifeEventsItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putString("EventID", lifeEvents.get(childPosition).getEventID());
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
            });
        }

        public void initializeFamilyRelationsView(View familyItemView, final int childPosition) {
            ImageView familyItemImageView = familyItemView.findViewById(R.id.personImageView);
            if (familyRelations.get(childPosition).getPersonSelected().getGender().equalsIgnoreCase("f")) {
                familyItemImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_person_female, null));
            } else {
                familyItemImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_person_male, null));
            }
            TextView familyItemNameView = familyItemView.findViewById(R.id.personTextView);
            familyItemNameView.setText(familyRelations.get(childPosition).getPersonSelected().getFirstName()
                    + " " + familyRelations.get(childPosition).getPersonSelected().getLastName()
                    + "\n" + familyRelations.get(childPosition).getRelationshipType());
            familyItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putString("PersonID", familyRelations.get(childPosition).getPersonSelected().getPersonID());
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;

        if (item.getItemId() == android.R.id.home)  {
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}