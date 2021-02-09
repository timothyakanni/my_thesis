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

import org.json.JSONArray;
import org.json.JSONObject;

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

import project.dto.Student;

public class StudentRegistration extends AppCompatActivity {

    EditText studentUsername, studentFirstname, studentLastname, studentPassword;
    String studentNumber, student_firstname, student_lastname, studentPwd;

    TextView studentRegistrationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        studentUsername = (EditText)findViewById(R.id.studentEditText);
        studentFirstname = (EditText)findViewById(R.id.firstnameEditText);
        studentLastname = (EditText)findViewById(R.id.lastnameEditText);
        studentPassword = (EditText)findViewById(R.id.passwordEditText);

        studentRegistrationMessage = (TextView) findViewById(R.id.studentRegistrationError);

    }

    public void OnRegister(View v){

        studentNumber = studentUsername.getText().toString();
        student_firstname = studentFirstname.getText().toString();
        student_lastname = studentLastname.getText().toString();
        studentPwd = studentPassword.getText().toString();

        if(studentNumber.isEmpty() || student_firstname.isEmpty() || student_lastname.isEmpty() ||
            studentPwd.isEmpty()){

            studentRegistrationMessage.setText("Check Missing Input Value!!!");
        }else{

            StudentRegistrationBT studentRegistrationBT = new StudentRegistrationBT(this);
            studentRegistrationBT.execute(studentNumber, student_firstname, student_lastname, studentPwd);
        }
    }

    //This is Background Task class that extends Async Task for student registration
    private class StudentRegistrationBT extends AsyncTask<String, Void, String> {

        Activity activity;
        AlertDialog alertDialog;

        String username;
        String firstname;
        String lastname;
        String password;

        public StudentRegistrationBT(Activity activity){

            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {

            String student_register_url = "http://www.cc.puv.fi/~e1100617/AssignmentTrackingApp/studentRegistration.php";
            username = params[0];
            firstname = params[1];
            lastname = params[2];
            password = params[3];


            try {

                URL url = new URL(student_register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("student_No", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&"
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
            alertDialog.show();

            try{

                if(result.equalsIgnoreCase("Registration Success")) {

                    studentRegistrationMessage.setText("Your Registration was successful!!!");

                }else if(result.equalsIgnoreCase("Error")) {

                    studentRegistrationMessage.setText("Your Registration was not successful!!!");
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
