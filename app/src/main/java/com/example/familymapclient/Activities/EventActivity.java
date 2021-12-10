package com.example.familymapclient.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.familymapclient.Fragments.MapsFragment;
import com.example.familymapclient.R;

import backEnd.DataCache;

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