package com.example.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

public class MyDiskActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
String currentFolderName;
    ListView itemlist;
    ArrayList<Item> items=new ArrayList();
    ItemAdapter adapter;
    AlertDialog.Builder builder;
    public void delete(final String path)
    {
        builder = new AlertDialog.Builder(MyDiskActivity.this);
        builder.setTitle("Confirm");  // заголовок
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                String isFolder;
                if(path.contains(".")) {
                    isFolder="false";
                }else
                {
                    isFolder="true";
                }
                new Delete().execute(currentFolderName + path,isFolder );
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_disk);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] menuNames ={"Folder", "File"};
                builder = new AlertDialog.Builder(MyDiskActivity.this);
                builder.setTitle("Add");
                builder.setItems(menuNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (menuNames[item].equals("Folder")) {


                            final Dialog commentDialog = new Dialog(MyDiskActivity.this);
                            commentDialog.setContentView(R.layout.editfoldername);
                            final EditText text = commentDialog.findViewById(R.id.body);
                            Button okBtn = (Button) commentDialog.findViewById(R.id.ok);
                            text.setFocusable(true);
                            text.requestFocus();
                            commentDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                            okBtn.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    commentDialog.dismiss();
                                }
                            });
                            Button cancelBtn = commentDialog.findViewById(R.id.cancel);
                            cancelBtn.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    String textf=text.getText().toString();
                                    if (!textf.isEmpty()) {
                                        commentDialog.dismiss();
                                        new CreateFolder().execute(getCreatableFolder(textf));
                                    }else
                                    {
                                        text.setHint("Invalid folder name");
                                        text.setText("");
                                    }

                                }
                            });
                            commentDialog.setCancelable(false);
                            commentDialog.show();

                        } else
                        {
                            InitFileManeger();
                        }
                    }
                });
                builder.setCancelable(true);
                builder.show();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header= navigationView.getHeaderView(0);
        TextView name=header.findViewById(R.id.Name);
        TextView email= header.findViewById(R.id.Email);
        Menu menu=navigationView.getMenu();
        GetAndSetUserInfo(name,email,menu);
        itemlist=findViewById(R.id.itemList);
        currentFolderName="/";
        adapter=new ItemAdapter(this,R.layout.list_item,items,this);
        itemlist.setAdapter(adapter);
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                Item selectedState = (Item) parent.getItemAtPosition(position);
                currentFolderName+=selectedState.getName()+"/";
                new GetDiskState().execute(currentFolderName);
            }
        };
        itemlist.setOnItemClickListener(itemListener);
        setTitle("My Cloud");
        new GetDiskState().execute(currentFolderName);
    }
    private String getCreatableFolder(String newfolder)
    {
        String folder=currentFolderName+newfolder;
        return folder;
    }

    private void GetAndSetUserInfo(TextView name,TextView email,Menu menu) {
        UserInfoModel info=BD.GetUserInfo(MyDiskActivity.this);
        name.setText(info.Name);
        email.setText(info.Email);
        menu.findItem(R.id.text_account).setTitle(info.Email);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(currentFolderName.equals("/")) {
                super.onBackPressed();
            }else
            {
                currentFolderName=currentFolderName.substring(0,currentFolderName.length()-1);
                currentFolderName=currentFolderName.substring(0,currentFolderName.lastIndexOf("/")+1);
                new GetDiskState().execute(currentFolderName);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_disk, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            DeleteAll();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void DeleteAll()
    {
        builder = new AlertDialog.Builder(MyDiskActivity.this);
        builder.setTitle("Confirm");  // заголовок
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                new DeleteAllInCloud().execute();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
    public class DeleteAllInCloud extends AsyncTask<String, Integer, String> {
        String result="Ok";
        @Override
        protected String doInBackground(String... data) {
            try {
                URL url = new URL(MainUrl + "api/values/DeleteAll");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "Bearer " + BD.GetToken(MyDiskActivity.this));
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("confirm", "false");
                writer.write(CreateString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = urlConnection.getResponseCode();
                responseCode=responseCode+1;
            } catch (Exception e) {
                result=e.getMessage();
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Ok")) {
                new GetDiskState().execute();
            } else {
                Toast.makeText(MyDiskActivity.this,"Error",Toast.LENGTH_SHORT).show();
            }
        }

    }
    public void edit(final String name)
    {
        final Dialog commentDialog = new Dialog(MyDiskActivity.this);
        commentDialog.setContentView(R.layout.editfoldername);
        Button okBtn = (Button) commentDialog.findViewById(R.id.ok);
        final EditText text = commentDialog.findViewById(R.id.body);
        text.setText(name);
        text.selectAll();
        text.setFocusable(true);
        text.requestFocus();
        commentDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                commentDialog.dismiss();
            }
        });
        Button cancelBtn = commentDialog.findViewById(R.id.cancel);
        cancelBtn.setText("Rename");
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String textf = text.getText().toString();
                if (!textf.isEmpty()) {
                    String isFolder;
                    if(name.contains("."))
                    {
                        isFolder="false";
                    }else
                    {
                        isFolder="true";
                    }
                    new EditTitle().execute(currentFolderName+name, currentFolderName+textf,isFolder);
                    commentDialog.dismiss();
                } else {
                    text.setHint("Invalid Note name");
                    text.setText("");
                }

            }
        });
        commentDialog.setCancelable(false);
        commentDialog.show();
    }
    public class EditTitle extends AsyncTask<String, Integer, String> {
        String result = "Ok";

        @Override
        protected String doInBackground(String... data) {
            try {
                URL url = new URL(MainUrl + "api/values/Rename");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "Bearer " + BD.GetToken(MyDiskActivity.this));
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("oldpath", data[0]);
                postDataParams.put("newpath", data[1]);
                postDataParams.put("isFolder", data[2]);
                writer.write(CreateString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = urlConnection.getResponseCode();
            } catch (Exception e) {
                result = e.getMessage();
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Ok")) {
                new GetDiskState().execute(currentFolderName);
            } else {
                Toast.makeText(MyDiskActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == 1)
            {
                String path = result.getData().getPath();
                new UploadFile().execute(currentFolderName,path);
            }
        }
    }
    private  void InitFileManeger()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        }else
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, 1);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }

    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1
        );
    }
    public void DeleteAccountClick() {
        final Dialog commentDialog = new Dialog(MyDiskActivity.this);

        commentDialog.setContentView(R.layout.editfoldername);
        Button okBtn = (Button) commentDialog.findViewById(R.id.ok);
        final EditText text = commentDialog.findViewById(R.id.body);
        text.requestFocus();
        text.setHint("Enter your password");
        commentDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                commentDialog.dismiss();
            }
        });
        Button cancelBtn = commentDialog.findViewById(R.id.cancel);
        cancelBtn.setText("Delete");
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText text = commentDialog.findViewById(R.id.body);
                String textf = text.getText().toString();
                if (!textf.isEmpty()) {
                    commentDialog.dismiss();
                    new DeleteAccount().execute(textf);
                } else {
                    text.setHint("Invalid folder name");
                    text.setText("");
                }

            }
        });
        commentDialog.setCancelable(false);
        commentDialog.show();

    }
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
    public class DeleteAccount extends AsyncTask<String, Integer, String> {
        String result="Ok";
        @Override
        protected String doInBackground(String... data) {
            try {
                URL url = new URL(MainUrl + "api/Account/Delete");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "Bearer " + BD.GetToken(MyDiskActivity.this));
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("userPass", data[0]);
                writer.write(CreateString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = urlConnection.getResponseCode();
            } catch (Exception e) {
                result=e.getMessage();
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Ok")) {
                LogOut();
            } else {
                Toast.makeText(MyDiskActivity.this,"Error",Toast.LENGTH_SHORT).show();
            }
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mydrive) {
            // Handle the camera action
        }  else if (id == R.id.nav_account) {
            DeleteAccountClick();
        } else if (id == R.id.nav_logout) {
            LogOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void LogOut()
    {
        BD.DeleteToken(this);
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private final String MainUrl = "https://carboncloudtest.azurewebsites.net/";

    private class Delete extends AsyncTask<String,Void,Integer>
    {
        @Override
        protected Integer doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection;
            BufferedReader in;
            int responseCode;
            String inputLine;
            try {
                url = new URL(MainUrl + "api/values?path="+strings[0]+"&isFolder="+strings[1]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setRequestProperty("Authorization", "Bearer " + BD.GetToken(MyDiskActivity.this));
                responseCode = urlConnection.getResponseCode();
                return responseCode;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
        @Override
        protected void onPostExecute(Integer result)
        {
            new GetDiskState().execute(currentFolderName);
            Toast.makeText(MyDiskActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
        }
    }


    public class CreateFolder extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection;
            BufferedReader in;
            int responseCode;
            String inputLine;
            StringBuffer response = new StringBuffer();
            try {
                url = new URL(MainUrl + "api/values/CreateFolder?path="+strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + BD.GetToken(MyDiskActivity.this));
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
            return "Bad";
        }

        protected void onPostExecute(String result) {
            if(result.equals("\"Ok\""))
            {
                new GetDiskState().execute(currentFolderName);
            }else
            {
                Toast.makeText(MyDiskActivity.this,result,Toast.LENGTH_LONG).show();
            }

        }
    }
    public class UploadFile extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String fileName = strings[1];

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(strings[1]);
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(MainUrl + "api/values/Upload");

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + BD.GetToken(MyDiskActivity.this));
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"" + strings[0] + "\";filename=\""
                        + sourceFile.getName() + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                int serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();


                if (serverResponseCode == 200) {

                    return "OK";
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                return "Bad";
            } catch (Exception e) {

                return "Bad";
            }

            return "OK";
        }

        @Override
        protected void onPostExecute(String result) {
            new GetDiskState().execute(currentFolderName);
        }

    }


    public class GetDiskState extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection;
            BufferedReader in;
            int responseCode;
            String inputLine;
            StringBuffer response = new StringBuffer();
            try {
                url = new URL(MainUrl + "api/values?path="+strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + BD.GetToken(MyDiskActivity.this));
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
            FilesMsg msg = new Gson().fromJson(result, FilesMsg.class);
            items.clear();
            for(int i=0;i<msg.Dirs.length;i++)
            {
                items.add(new Item(msg.Dirs[i],R.drawable.folder));
            }
            for (int i=0;i<msg.Files.length;i++)
            {
                items.add(new Item(msg.Files[i],R.drawable.ic_description_black_24dp));
            }
            adapter.notifyDataSetChanged();

        }
    }
}
