package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import model.Event;
import model.Person;

public class MapsFragment extends Fragment {

    DataCache dataCache = DataCache.getInstance();

    TextView detailsText;
    ImageView genderImage;
    String personID;

    GoogleMap savedGoogleMap;
    Integer generation = 40;

    ArrayList<Polyline> allPolyLines = new ArrayList<>();


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        @Override
        public void onMapReady(GoogleMap googleMap) {
            savedGoogleMap = googleMap;

            dataCache.filterEvents();

            Float color = 0f;
            for (Event event : dataCache.getAllFilteredEvents().values()) {
                LatLng eventLocation = new LatLng(event.getLatitude(), event.getLongitude());
                Marker marker = googleMap.addMarker(new MarkerOptions().position(eventLocation).title(event.getCity() + ", " + event.getCountry()));
                if (marker != null) { // If event is already in the EventType map
                    if (dataCache.getTypeEventMap().containsKey(event.getEventType().toLowerCase())) {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(dataCache.getTypeEventMap().get(event.getEventType().toLowerCase())));
                    } else { // If event does not exist
                        color = color + 30f;
                        dataCache.getTypeEventMap().put(event.getEventType().toLowerCase(), color);
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(dataCache.getTypeEventMap().get(event.getEventType().toLowerCase())));
                    }
                    marker.setTag(event);
                } else {
                    System.out.println("Marker is null");
                }

                genderImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_person_default, null));

                if (dataCache.isFromMap()) {
                    Event eventFromEventActivity = dataCache.getEventMap().get(dataCache.getEventID());
                    setHasOptionsMenu(false);
                    LatLng eventFromEventActivityLocation = new LatLng(eventFromEventActivity.getLatitude(), eventFromEventActivity.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(eventFromEventActivityLocation));

                    personID = eventFromEventActivity.getPersonID();
                    Person personSelected = dataCache.getPersonMap().get(eventFromEventActivity.getPersonID());

                    if (personSelected != null) {
                        detailsText.setText(personSelected.getFirstName() + " " + personSelected.getLastName()
                                + "\n" + eventFromEventActivity.getEventType() + ": " + eventFromEventActivity.getCity() + ", " + eventFromEventActivity.getCountry()
                                + "\n" + "Year: " + eventFromEventActivity.getYear());
                    }

                    if (personSelected.getGender().equalsIgnoreCase("f")) {
                        genderImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_person_female, null));
                    } else {
                        genderImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_person_male, null));
                    }
                }
                else  {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(eventLocation));
                    setHasOptionsMenu(true);
                }
            }
            dataCache.getTypeEventMap().clear();

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Event eventSelected = (Event) marker.getTag();
                    DataCache dataCache = DataCache.getInstance();

                    personID = eventSelected.getPersonID();

                    Person personSelected = dataCache.getPersonMap().get(eventSelected.getPersonID());
                    if (personSelected != null) {
                        detailsText.setText(personSelected.getFirstName() + " " + personSelected.getLastName()
                        + "\n" + eventSelected.getEventType() + ": " + eventSelected.getCity() + ", " + eventSelected.getCountry()
                        + "\n" + "Year: " + eventSelected.getYear());
                    }

                    if (personSelected.getGender().equalsIgnoreCase("f")) {
                        genderImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_person_female, null));
                    } else {
                        genderImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_person_male, null));
                    }

                    drawLines(eventSelected);
                    return false;
                }
            });
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        detailsText = view.findViewById(R.id.detailsTextView);
        genderImage = view.findViewById(R.id.genderImageView);
        detailsText.setText("Click on a marker to learn more!");

        detailsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (personID != null) {
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putString("PersonID", personID);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    //Override onCreateOptionsMenu(Menu)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.searchMenuItem);
        searchMenuItem.setIcon(new IconDrawable(getContext(), FontAwesomeIcons.fa_search)
                .colorRes(R.color.white)
                .actionBarSize());

        MenuItem settingsMenuItem = menu.findItem(R.id.settingsMenuItem);
        settingsMenuItem.setIcon(new IconDrawable(getContext(), FontAwesomeIcons.fa_gear)
                .colorRes(R.color.white)
                .actionBarSize());
    }


    //Override onOptionsItemSelected(MenuItem)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {
            case R.id.searchMenuItem:
                Toast.makeText(getActivity(), getString(R.string.searchMenuSelectedMessage), Toast.LENGTH_SHORT).show();
                intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.settingsMenuItem:
                Toast.makeText(getActivity(), getString(R.string.settingsMenuSelectedMessage), Toast.LENGTH_SHORT).show();
                intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (savedGoogleMap != null) {
            savedGoogleMap.clear();

            Float color = 0f;

            dataCache.filterEvents();

//            if (dataCache.getAllFilteredEvents() == null) {
//                savedGoogleMap.clear();
//            }
            for (Event event : dataCache.getAllFilteredEvents().values()) {
                LatLng eventLocation = new LatLng(event.getLatitude(), event.getLongitude());
                Marker marker = savedGoogleMap.addMarker(new MarkerOptions().position(eventLocation).title(event.getCity() + ", " + event.getCountry()));
                if (marker != null) { // If event is already in the EventType map
                    if (dataCache.getTypeEventMap().containsKey(event.getEventType().toLowerCase())) {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(dataCache.getTypeEventMap().get(event.getEventType().toLowerCase())));
                    } else { // If event does not exist
                        color = color + 30f;
                        dataCache.getTypeEventMap().put(event.getEventType().toLowerCase(), color);
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(dataCache.getTypeEventMap().get(event.getEventType().toLowerCase())));
                    }
                    marker.setTag(event);
                } else {
                    System.out.println("Marker is null");
                }
            }
            dataCache.getTypeEventMap().clear();
        }
    }

    /* DRAWING LINES */
    private void drawSpouseLines(Event currentEvent) {
        for (Event event : dataCache.getAllFilteredEvents().values()) {
            String personID = currentEvent.getPersonID();
            String spouseID = dataCache.getPersonMap().get(event.getPersonID()).getSpouseID();

            if (spouseID.equals(personID)) {
                LatLng currentEventLocation = new LatLng(currentEvent.getLatitude(), currentEvent.getLongitude());
                Event spouseRootEvent = dataCache.getAllPersonEvents().get(event.getPersonID()).get(0);
                LatLng spouseRootEventLocation = new LatLng(spouseRootEvent.getLatitude(), spouseRootEvent.getLongitude());
                Polyline createdSpouseLine = savedGoogleMap.addPolyline(new PolylineOptions().add(currentEventLocation, spouseRootEventLocation).width(10).color(Color.BLUE));
                allPolyLines.add(createdSpouseLine);
            }
        }
    }

    private void drawLifeStoriesLines(Event currentEvent) {
        ArrayList<Event> eventsOfPerson = dataCache.getAllPersonEvents().get(currentEvent.getPersonID());
        ArrayList<LatLng> locations = new ArrayList<>();

        for (Event event : eventsOfPerson) {
            LatLng eventToAdd = new LatLng(event.getLatitude(), event.getLongitude());
            locations.add(eventToAdd);
        }

        for (int i = 0; i < locations.size() - 1; i++) {
            Polyline createLifeStoryLine = savedGoogleMap.addPolyline(new PolylineOptions().add(locations.get(i), locations.get(i+1)).width(20).color(Color.MAGENTA));
            allPolyLines.add(createLifeStoryLine);
        }
    }

    private void drawFamilyLines(Event currentEvent, int generationPassed) {
        Person selectedPerson = dataCache.getPersonMap().get(currentEvent.getPersonID());
        LatLng currentLocation = new LatLng(currentEvent.getLatitude(), currentEvent.getLongitude());

        if (selectedPerson.getFatherID() != null && dataCache.isMaleEventsToggled()) {
            if (generation == generationPassed && !dataCache.isFatherSideToggled()) {
                System.out.println("");
            } else {
                Event fatherFirstEvent = dataCache.filterPersonEvents(selectedPerson.getFatherID()).get(0);

                LatLng fatherLocation = new LatLng(fatherFirstEvent.getLatitude(), fatherFirstEvent.getLongitude());
                Polyline createFamilyLine = savedGoogleMap.addPolyline(new PolylineOptions().add(currentLocation, fatherLocation).width(generationPassed).color(Color.RED));
                allPolyLines.add(createFamilyLine);
                drawFamilyLines(fatherFirstEvent, generationPassed - 10);
            }
        }

        if (selectedPerson.getMotherID() != null && dataCache.isFemaleEventsToggled()) {
            if (generation == generationPassed && !dataCache.isMotherSideToggled()) {
                System.out.println("");
            } else {
                Event motherFirstEvent = dataCache.filterPersonEvents(selectedPerson.getMotherID()).get(0);

                LatLng motherLocation = new LatLng(motherFirstEvent.getLatitude(), motherFirstEvent.getLongitude());
                Polyline createFamilyLine = savedGoogleMap.addPolyline(new PolylineOptions().add(currentLocation, motherLocation).width(generationPassed).color(Color.RED));
                allPolyLines.add(createFamilyLine);
                drawFamilyLines(motherFirstEvent, generationPassed - 10);
            }
        }
    }

    private void drawLines(Event currentEvent) {
        for (Polyline polyline : allPolyLines) {
            polyline.remove();
        }
        allPolyLines.clear();

        if (dataCache.isShowSpouseLines()) {
            drawSpouseLines(currentEvent);
        }
        if (dataCache.isShowLifeStoryLines()) {
            drawLifeStoriesLines(currentEvent);
        }
        if (dataCache.isShowFamilyTreeLines()) {
            drawFamilyLines(currentEvent, generation);
        }
    }
}