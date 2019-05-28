package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private TextView errorMessage;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(IsAuth())
        {
            StartMainScreen();
            finish();
            return;
        }
        if(IsWaitCode())
        {
            Intent intent=new Intent(MainActivity.this, EnterConfirmedCode.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);

        setTitle("Sign In to Carbon Drive");

        errorMessage = findViewById(R.id.tv_error);
        email = findViewById(R.id.enterEmailEditText);
        password = findViewById(R.id.enterPasswordEditText);
    }

    private boolean IsWaitCode() {
        String bool=BD.GetEmail(MainActivity.this);
        if(bool.equals("not"))
        {
            return false;
        }
        return true;
    }

    private boolean IsAuth() {

        if(BD.CheckToken(MainActivity.this))
        {
            return true;
        }

        return false;
    }

    private void StartMainScreen() {
        Intent intent=new Intent(MainActivity.this, MyDiskActivity.class);
        startActivity(intent);
    }

    public void SignInOnClick(View view) {
        String useremail = email.getText().toString();
        String userpassword = password.getText().toString();
        if (!useremail.equals("") && !userpassword.equals("")) {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            new GetLoginToken().execute(useremail, userpassword);
        }else
        {
            errorMessage.setVisibility(View.VISIBLE);
        }
    }

    public void RegistrationOnClick(View view) {
        Intent intent=new Intent(MainActivity.this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    private String MainUrl = "https://carboncloudtest.azurewebsites.net/";

    @SuppressWarnings("MagicConstant")
    private String CreateString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            try {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    public class GetLoginToken extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... data) {
            try {
                return Login(data[0], data[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                AuthMsg msg = new Gson().fromJson(result, AuthMsg.class);
                BD.SaveToken(msg.getAccess_token(), msg.getToken_type(), msg.getUserName(), MainActivity.this);
                        new GetUserInfo().execute();

            } else {
                errorMessage.setVisibility(View.VISIBLE);
            }
        }

        private String Login(String username, String password) {

            try {
                URL url = new URL(MainUrl + "Token");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("grant_type", "password");
                postDataParams.put("username", username);
                postDataParams.put("password", password);
                writer.write(CreateString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                }
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
            return null;
        }
    }

    public class GetUserInfo extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... a) {
            URL url;
            HttpURLConnection urlConnection;
            BufferedReader in;
            int responseCode;
            String inputLine;
            StringBuffer response = new StringBuffer();
            try {
                url = new URL(MainUrl + "api/Account/GetInfo");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + BD.GetToken(MainActivity.this));
                responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            UserInfoModel info = new Gson().fromJson(result, UserInfoModel.class);
            BD.SaveUserInfo(MainActivity.this,info);
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            StartMainScreen();
            finish();
        }
    }
}
