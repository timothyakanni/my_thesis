package com.example.feranmi.assignmenttrackingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class TeacherRegistration extends AppCompatActivity {

    EditText teacherUsername, teacherFirstname, teacherLastname, teacherPassword;
    String username, firstname, lastname, password;

    TextView teacherRegistrationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);

        teacherUsername = (EditText)findViewById(R.id.teacherEditText);
        teacherFirstname = (EditText)findViewById(R.id.firstnameEditText);
        teacherLastname = (EditText)findViewById(R.id.lastnameEditText);
        teacherPassword = (EditText)findViewById(R.id.passwordEditText);

        teacherRegistrationMessage = (TextView) findViewById(R.id.teacherRegistrationError);
    }

    //This is the method for register button implementation
    public void onRegister(View v){

        username = teacherUsername.getText().toString();
        firstname = teacherFirstname.getText().toString();
        lastname = teacherLastname.getText().toString();
        password = teacherPassword.getText().toString();

        if(username.isEmpty() || firstname.isEmpty() || lastname.isEmpty() ||
                password.isEmpty()){

            teacherRegistrationMessage.setText("Check Missing Input Value!!!");
        }else{

            TeacherRegistrationBT teacherRegistrationBT = new TeacherRegistrationBT(this);
            teacherRegistrationBT.execute(username, firstname, lastname, password);
        }

    }


    //This is Background Task class that extends Async Task for teacher registration
    private class TeacherRegistrationBT extends AsyncTask<String, Void, String> {

        Activity activity;
        AlertDialog alertDialog;

        String username;
        String password;

        public TeacherRegistrationBT(Activity activity){

            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {

            String teacher_register_url = "http://www.cc.puv.fi/~e1100617/AssignmentTrackingApp/teacherRegistration.php";
            username = params[0];
            firstname = params[1];
            lastname = params[2];
            password = params[3];


            try {

                URL url = new URL(teacher_register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&"
                        + URLEncoder.encode("firstName", "UTF-8") + "=" + URLEncoder.encode(firstname, "UTF-8") + "&"
                        + URLEncoder.encode("lastName", "UTF-8") + "=" + URLEncoder.encode(lastname, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                StringBuilder sb = new StringBuilder();
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


                String result = sb.toString();

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch(NullPointerException e){
                Log.e("JSON Null Exception", "My Error: " + e.toString());
            }


            return null;

        }

        @Override
        protected void onPreExecute() {

            alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("Login Status");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            alertDialog.setMessage(result);
            //alertDialog.show();

            try{

                if(result.equalsIgnoreCase("Registration Success")) {
                    teacherRegistrationMessage.setText("Your Registration was successful!!!");
                }else if(result.equalsIgnoreCase("Error")) {
                    teacherRegistrationMessage.setText("Your Registration was not successful!!!");
                }
            }catch(NullPointerException e){
                Log.e("Null Pointer Error", "My error: " + e.toString());
            }



        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }

    }


}
