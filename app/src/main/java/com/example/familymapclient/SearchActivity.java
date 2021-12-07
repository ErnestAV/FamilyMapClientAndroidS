package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    DataCache dataCache = DataCache.getInstance();
    String personID;

    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView = findViewById(R.id.RecyclerViewID);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        ArrayList<Person> allPeople = dataCache.getPersonsToSearch();
        ArrayList<Event> allEvents = dataCache.getEventsToSearch();

        searchView = findViewById(R.id.SearchViewID);
        searchView.setQueryHint("Search...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (TextUtils.isEmpty(newText)) {
                    SearchActivityAdapter newAdapterReset = new SearchActivityAdapter();
                    recyclerView.setAdapter(newAdapterReset);
                }
                else {
                    //Add FILTERING functions (people & events)
                    final ArrayList<Person> filteredPeople = filterPeople(allPeople, newText); //USE FILTERING FUNCTIONS
                    final ArrayList<Event> filteredEvents = filterEvents(allEvents, newText); //USE FILTERING FUNCTIONS

                    SearchActivityAdapter newAdapterFilter = new SearchActivityAdapter(filteredPeople, filteredEvents);
                    recyclerView.setAdapter(newAdapterFilter);
                }
                return false;
            }
        });
    }

    private ArrayList<Person> filterPeople(ArrayList<Person> people, String toFind) {
        toFind = toFind.toLowerCase();
        ArrayList<Person> filteredPeople = new ArrayList<>();
        String currentPersonSearch;

        for (Person person : people) {
            currentPersonSearch = person.getFirstName().toLowerCase() + " " + person.getLastName().toLowerCase();
            if (currentPersonSearch.contains(toFind)) {
                filteredPeople.add(person);
            }
        }
        return filteredPeople;
    }

    private ArrayList<Event> filterEvents(ArrayList<Event> events, String toFind) {
        toFind = toFind.toLowerCase();
        ArrayList<Event> filteredEvents = new ArrayList<>();
        String currentEventSearch;

        for (Event event : events) {
            Person personAssociated = dataCache.getPersonMap().get(event.getPersonID());
            currentEventSearch = event.getEventType().toLowerCase() + ": "
                    + event.getCity().toLowerCase() + ", " + event.getCountry().toLowerCase()
                    + "(" + event.getYear() + ")" + personAssociated.getFirstName().toLowerCase()
                    + " " + personAssociated.getLastName().toLowerCase();
            if (currentEventSearch.contains(toFind)) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
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

    private class SearchActivityAdapter extends RecyclerView.Adapter<SearchActivityViewHolder> {
        private final ArrayList<Person> people;
        private final ArrayList<Event> events;

        SearchActivityAdapter(ArrayList<Person> people, ArrayList<Event> events) {
            this.people = people;
            this.events = events;
        }

        public SearchActivityAdapter() {
            this.people = new ArrayList<>();
            this.events = new ArrayList<>();
        }

        @Override
        public int getItemViewType(int position) {
            return position < people.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person_item, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            }

            return new SearchActivityViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchActivityViewHolder holder, int position) {
            if(position < people.size()) {
                holder.bind(people.get(position));
            } else {
                holder.bind(events.get(position - people.size()));
            }
        }

        @Override
        public int getItemCount() {
            return people.size() + events.size();
        }
    }

    private class SearchActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView titleToDisplay;
        private int viewType;
        private ImageView imageView;
        private Event event;
        private Person person;

        SearchActivityViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                imageView = view.findViewById(R.id.personImageView);
                titleToDisplay = itemView.findViewById(R.id.personTextView);
            } else if (viewType == EVENT_ITEM_VIEW_TYPE) {
                imageView = view.findViewById(R.id.lifeEventImageView);
                titleToDisplay = itemView.findViewById(R.id.lifeEventTextView);
            }
        }

        private void bind(Person person) {
            this.person = person;

            titleToDisplay.setText(person.getFirstName() + " " + person.getLastName());

            if (person.getGender().equalsIgnoreCase("m")) {
                imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_person_male, null));
            }
            else if (person.getGender().equalsIgnoreCase("f")) {
                imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_person_female, null));
            }
        }

        private void bind(Event event) {
            this.event = event;
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_marker_event, null));

            Person personAssociated = dataCache.getPersonMap().get(event.getPersonID());

            titleToDisplay.setText(event.getEventType() + ": "
                    + event.getCity() + ", " + event.getCountry()
                    + "(" + event.getYear() + ")"
                    + "\n" + personAssociated.getFirstName() + " " + personAssociated.getLastName());
        }

        @Override
        public void onClick(View view) {
            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("PersonID", person.getPersonID());
                intent.putExtras(bundle);

                startActivity(intent);
            } else {
                Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("EventID", event.getEventID());
                intent.putExtras(bundle);

                startActivity(intent);
            }
        }
    }
}