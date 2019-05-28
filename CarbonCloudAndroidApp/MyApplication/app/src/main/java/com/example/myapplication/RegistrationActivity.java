package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

public class RegistrationActivity extends AppCompatActivity {

    EditText Name,Email,Password,ConfirmPassword;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTitle("Registration");
        Name=findViewById(R.id.UserName);
        Email=findViewById(R.id.Email);
        Password=findViewById(R.id.Password);
        ConfirmPassword=findViewById(R.id.ConfirmPassword);
        progressBar=findViewById(R.id.progressBar);
    }

    public void RegistrationOnClick(View view) {
        String name=Name.getText().toString();
        String email=Email.getText().toString();
        String password=Password.getText().toString();
        String cp=ConfirmPassword.getText().toString();
        if(!name.isEmpty()&&!email.isEmpty()&&!password.isEmpty()&&!cp.isEmpty()) {

            progressBar.setVisibility(ProgressBar.VISIBLE);
            new Register().execute(name,email,password,cp);
        }else
        {
            Toast.makeText(this,"Enter all registration data",Toast.LENGTH_LONG).show();
        }
    }

    public void SignInOnClick(View view) {
        finish();
    }
    private String MainUrl = "https://carboncloudtest.azurewebsites.net/";


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

    public class Register extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... data) {
            try {
                return RegisterStart(data[0], data[1],data[2],data[3]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                BD.SaveEmail(RegistrationActivity.this,result);
                Intent intent=new Intent(RegistrationActivity.this, EnterConfirmedCode.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(RegistrationActivity.this,"Server Error",Toast.LENGTH_LONG).show();
            }
        }

        private String RegisterStart(String name, String email,String password,String cp) {

            try {
                URL url = new URL(MainUrl + "/api/Account/Register");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("Name", name);
                postDataParams.put("Email", email);
                postDataParams.put("Password", password);
                postDataParams.put("ConfirmPassword",cp);
                writer.write(CreateString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    return email;
                }
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
            return null;
        }
    }
}
