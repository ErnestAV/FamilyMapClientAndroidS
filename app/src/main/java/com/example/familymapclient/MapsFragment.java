package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
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
}