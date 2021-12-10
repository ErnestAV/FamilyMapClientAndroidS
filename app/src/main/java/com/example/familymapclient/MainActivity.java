package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.widget.Toast;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Iconify.with(new FontAwesomeModule());

        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.main_activity);

        if (frag == null) {
            frag = createLoginFragment();
            fm.beginTransaction()
                    .add(R.id.main_activity, frag)
                    .commit();
        }
        else {
            if (frag instanceof LoginFragment) {
                ((LoginFragment) frag).registerListener(this);
            }
        }
    }

    private Fragment createLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();

        loginFragment.registerListener(this);

        return loginFragment;
    }

    @Override
    public void notifyDone() {
        FragmentManager fm = this.getSupportFragmentManager();
        Fragment frag = new MapsFragment();

        fm.beginTransaction()
                .replace(R.id.main_activity, frag)
                .commit();
    }

    @Override
    public void makeToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}