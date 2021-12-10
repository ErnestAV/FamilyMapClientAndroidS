package com.example.familymapclient;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import request.EventRequest;
import request.LoginRequest;
import request.PersonRequest;
import request.RegisterRequest;
import result.EventResult;
import result.LoginResult;
import result.PersonResult;
import result.RegisterResult;

public class ServerProxy {

    LoginResult login(LoginRequest loginR, String serverHost, String serverPort) {
        LoginResult loginResult = new LoginResult(null);

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");

            http.setDoOutput(true);

            http.addRequestProperty("Accept", "application/json");

            Gson gson = new Gson();
            String requestData = gson.toJson(loginR);
            OutputStream requestBody = http.getOutputStream();
            writeString(requestData, requestBody);

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();

                String respData = readString(respBody);

                loginResult = gson.fromJson(respData, LoginResult.class);

                System.out.println(respData);
            }
            else {

                System.out.println("ERROR: " + http.getResponseMessage());

                InputStream respBody = http.getErrorStream();

                String respData = readString(respBody);

                loginResult = gson.fromJson(respData, LoginResult.class);

                System.out.println(respData);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return loginResult;
    }

    RegisterResult register(RegisterRequest registerRequest, String serverHost, String serverPort) {
        RegisterResult registerResult = new RegisterResult();

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");

            http.setDoOutput(true);

            http.addRequestProperty("Accept", "application/json");

            Gson gson = new Gson();
            String requestData = gson.toJson(registerRequest);
            OutputStream requestBody = http.getOutputStream();
            writeString(requestData, requestBody);

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();

                String respData = readString(respBody);

                registerResult = gson.fromJson(respData, RegisterResult.class);

                System.out.println(respData);
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());

                InputStream respBody = http.getErrorStream();

                String respData = readString(respBody);

                registerResult = gson.fromJson(respData, RegisterResult.class);

                System.out.println(respData);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return registerResult;
    }

    PersonResult person(PersonRequest personRequest, String serverHost, String serverPort, String authToken) {
        PersonResult personResult = new PersonResult("Person result is Null");
        Gson gson = new Gson();

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");

            http.setDoOutput(false);

            http.addRequestProperty("Authorization", authToken);

            http.addRequestProperty("Accept", "application/json");

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();

                String respData = readString(respBody);

                personResult = gson.fromJson(respData, PersonResult.class);

                System.out.println(respData);
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());

                InputStream respBody = http.getErrorStream();

                String respData = readString(respBody);

                personResult = gson.fromJson(respData, PersonResult.class);

                System.out.println(respData);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return personResult;
    }

    EventResult event(EventRequest eventRequest, String serverHost, String serverPort, String authToken) {
        EventResult eventResult = new EventResult("Event Result is Null");
        Gson gson = new Gson();

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");

            http.setDoOutput(false);


            http.addRequestProperty("Authorization", authToken);

            http.addRequestProperty("Accept", "application/json");

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();

                String respData = readString(respBody);

                eventResult = gson.fromJson(respData, EventResult.class);

                System.out.println(respData);
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());

                InputStream respBody = http.getErrorStream();

                String respData = readString(respBody);

                eventResult = gson.fromJson(respData, EventResult.class);

                System.out.println(respData);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return eventResult;
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

}
