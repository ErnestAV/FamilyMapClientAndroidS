package com.example.familymapclient;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    Listener listener;

    //Global variables
    private String genderString = "M";

    public LoginFragment() {
        // Required empty public constructor
    }

    //Interface
    public interface Listener {

        void notifyDone();
        void makeToast(String message);
    }

    public void registerListener(Listener listenerObject) {
        this.listener = listenerObject;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View viewToReturn = inflater.inflate(R.layout.fragment_login, container, false);

        EditText serverPort = viewToReturn.findViewById(R.id.hostPortInput);
        EditText serverIP = viewToReturn.findViewById(R.id.hostIPInput);
        EditText usernameInput = viewToReturn.findViewById(R.id.usernameInput);
        EditText passwordInput = viewToReturn.findViewById(R.id.passwordInput);
        EditText firstName = viewToReturn.findViewById(R.id.firstNameInput);
        EditText lastName = viewToReturn.findViewById(R.id.lastNameInput);
        EditText emailInput = viewToReturn.findViewById(R.id.emailInput);
        RadioGroup gender = viewToReturn.findViewById(R.id.genderRadioGrouping);

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getCheckedRadioButtonId() == R.id.maleGenderButton) {
                    genderString = "M";
                }
                else {
                    genderString = "F";
                }
            }
        });

        Button signInButton = viewToReturn.findViewById(R.id.signInButton);
        Button registerButton = viewToReturn.findViewById(R.id.registerButton);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(serverIP.getText().toString().equals("")) && !(serverPort.getText().toString().equals(""))
                && !(usernameInput.getText().toString().equals("")) && !(passwordInput.getText().toString().equals(""))
                        && !(firstName.getText().toString().equals("")) && !(lastName.getText().toString().equals(""))
                        && !(emailInput.getText().toString().equals(""))) {
                    registerButton.setEnabled(true);
                }
                else {
                    registerButton.setEnabled(false);
                }

                if (!(serverIP.getText().toString().equals("")) && !(serverPort.getText().toString().equals(""))
                        && !(usernameInput.getText().toString().equals("")) && !(passwordInput.getText().toString().equals(""))) {
                    signInButton.setEnabled(true);
                }
                else {
                    signInButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        serverPort.addTextChangedListener(textWatcher);
        serverIP.addTextChangedListener(textWatcher);
        usernameInput.addTextChangedListener(textWatcher);
        passwordInput.addTextChangedListener(textWatcher);
        firstName.addTextChangedListener(textWatcher);
        lastName.addTextChangedListener(textWatcher);
        emailInput.addTextChangedListener(textWatcher);

        //Disable both buttons until proper fields are filled
        signInButton.setEnabled(false);
        registerButton.setEnabled(false);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler registerHandler = new Handler() {
                    String registerCode = "Register";
                    @Override
                    public void handleMessage(Message msg) {
                        Bundle bundle = msg.getData();

                        String message = bundle.getString(registerCode);

                        if (message.contains("OK")) {
                            if (listener == null) {
                                listener.notifyDone();
                            }
                            else {
                                listener.makeToast(message);
                                listener.notifyDone();
                            }
                        }
                        else {
                            listener.makeToast(message);
                        }
                    }
                };

                RegisterTask registerTask = new RegisterTask(serverPort.getText().toString(), serverIP.getText().toString(), usernameInput.getText().toString(), passwordInput.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), emailInput.getText().toString(), genderString, registerHandler);
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.submit(registerTask);
            }
        });

        //Do the Login set On Click Listener thing


        // Inflate the layout for this fragment
        return viewToReturn;
    }
}