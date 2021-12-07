package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;

public class EventActivity extends AppCompatActivity {
    DataCache dataCache = DataCache.getInstance();

    String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundleExtras = getIntent().getExtras();

        if (bundleExtras != null) {
            eventID = bundleExtras.getString("EventID");
        }

        MapsFragment googleMap = new MapsFragment();
        FragmentManager fragmentManager = this.getSupportFragmentManager();

        dataCache.setFromMap(true);
        dataCache.setEventID(eventID);
        fragmentManager.beginTransaction().replace(R.id.EventActivity, googleMap).commit();

        setContentView(R.layout.activity_event);
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