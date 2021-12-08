package com.example.familymapclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    Switch lifeStorySwitch;
    Switch familyTreeSwitch;
    Switch spouseSwitch;
    Switch fatherSwitch;
    Switch motherSwitch;
    Switch maleEventsSwitch;
    Switch femaleEventsSwitch;

    DataCache dataCache = DataCache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button logoutButton = findViewById(R.id.LogoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                DataCache dataCache = DataCache.getInstance();
                dataCache = new DataCache();
                startActivity(intent);
            }
        });

        lifeStorySwitch = findViewById(R.id.LifeStoryLineSwitch);
        familyTreeSwitch = findViewById(R.id.FamilyTreeLinesSwitch);
        spouseSwitch = findViewById(R.id.SpouseLinesSwitch);
        fatherSwitch = findViewById(R.id.FatherSideSwitch);
        motherSwitch = findViewById(R.id.MotherSideSwitch);
        maleEventsSwitch = findViewById(R.id.MaleEventsSwitch);
        femaleEventsSwitch = findViewById(R.id.FemaleEventsSwitch);

        lifeStorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataCache.setShowLifeStoryLines(true);
                }
                else {
                    dataCache.setShowLifeStoryLines(false);
                }
            }
        });
        lifeStorySwitch.setChecked(dataCache.isShowLifeStoryLines());

        familyTreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataCache.setShowFamilyTreeLines(true);
                } else {
                    dataCache.setShowFamilyTreeLines(false);
                }
            }
        });
        familyTreeSwitch.setChecked(dataCache.isShowFamilyTreeLines());

        spouseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataCache.setShowSpouseLines(true);
                } else {
                    dataCache.setShowSpouseLines(false);
                }
            }
        });
        spouseSwitch.setChecked(dataCache.isShowSpouseLines());

        fatherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataCache.setFatherSideToggled(true);
                } else {
                    dataCache.setFatherSideToggled(false);
                }
            }
        });
        fatherSwitch.setChecked(dataCache.isFatherSideToggled());

        motherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataCache.setMotherSideToggled(true);
                } else {
                    dataCache.setMotherSideToggled(false);
                }
            }
        });
        motherSwitch.setChecked(dataCache.isMotherSideToggled());

        maleEventsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataCache.setMaleEventsToggled(true);
                } else {
                    dataCache.setMaleEventsToggled(false);
                }
            }
        });
        maleEventsSwitch.setChecked(dataCache.isMaleEventsToggled());

        femaleEventsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataCache.setFemaleEventsToggled(true);
                } else {
                    dataCache.setFemaleEventsToggled(false);
                }
            }
        });
        femaleEventsSwitch.setChecked(dataCache.isFemaleEventsToggled());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;

        if(item.getItemId() == android.R.id.home)  {
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}