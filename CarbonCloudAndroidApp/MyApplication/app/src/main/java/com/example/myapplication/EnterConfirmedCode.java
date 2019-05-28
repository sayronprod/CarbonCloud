package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class EnterConfirmedCode extends AppCompatActivity {
    EditText Code;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_confirmed_code);
        Code=findViewById(R.id.Code);
        progressBar=findViewById(R.id.progressBar);
    }

    public void ConfirmonOnClick(View view) {
        String code=Code.getText().toString();
        if(!code.isEmpty())
        {
            String email=BD.GetEmail(EnterConfirmedCode.this);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            new SentCode().execute(email,code);
        }
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

    public class SentCode extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... data) {
            try {
                return SentCodeStart(data[0], data[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                BD.SaveEmail(EnterConfirmedCode.this,"not");
                Intent intent=new Intent(EnterConfirmedCode.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(EnterConfirmedCode.this,"Not Correct Confirm Code",Toast.LENGTH_LONG).show();
            }
        }

        private String SentCodeStart(String email, String code) {

            try {
                URL url = new URL(MainUrl + "/api/Account/SentCode");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("code", code);
                postDataParams.put("email", email);
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
