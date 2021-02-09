package com.example.feranmi.assignmenttrackingapp_STUDENT;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
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

import project.dto.Teacher;

public class TeacherLogin extends AppCompatActivity {
    EditText teacherUsername;
    EditText teacherPassword;
    TextView registerLink;

    TextView teacherLoginMessage;

    static JSONObject jObj = null;
    static String json = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        teacherUsername= (EditText)findViewById(R.id.teacherEditText);
        teacherPassword= (EditText)findViewById(R.id.passwordEditText);
        teacherLoginMessage = (TextView)findViewById(R.id.teacherLoginError);

        teacherRegistration();

    }
    //This is the method for login button implementation
    public void onLogin(View v){

        String teacherID = teacherUsername.getText().toString();
        String password = teacherPassword.getText().toString();
        if(teacherID.isEmpty() || password.isEmpty()){

            teacherLoginMessage.setText("Check Missing Input Value!!!");
        }else {

            TeacherLoginBT teacherLoginBT = new TeacherLoginBT(this);
            teacherLoginBT.execute(teacherID, password);
        }
    }

    //This is the method that redirect teacher to student registration page
    public void teacherRegistration(){

        registerLink = (TextView)findViewById(R.id.teacherRegister);
        registerLink.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent("com.example.feranmi.assignmenttrackingapp.TeacherRegistration");
                        startActivity(intent);
                    }
                }
        );
    }

    //This is Background Task class that extends Async Task for teacher login
    private class TeacherLoginBT extends AsyncTask<String, Void, JSONObject> {

        Activity activity;
        AlertDialog alertDialog;
        JSONArray studentInfo = null;

        String username;
        String password;

        String DB_teacher_Id;
        String DB_password;

        Teacher teacher = new Teacher();

        public TeacherLoginBT(Activity activity){

            this.activity = activity;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String teacher_login_url = "http://www.cc.puv.fi/~e1100617/AssignmentTrackingApp/teacherLogin.php";
            username = params[0];
            password = params[1];

            try {

                URL url = new URL(teacher_login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&"
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

                json = sb.toString();
                jObj = new JSONObject(json);

                return jObj;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch(JSONException e) {
                Log.e("JSON Parsing Error", "My Error " + e.toString());
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
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            try {
                studentInfo = json.getJSONArray("teacherInfo");
                if(studentInfo.isNull(0)){
                    teacherLoginMessage.setText("Logging Error: Incorrect Username or Password");
                }
                JSONObject j = studentInfo.getJSONObject(0);

                //teacher object is created and set methods in teacher class are used
                //to set teacher variables
                teacher.setFirstName(j.optString("firstName"));
                teacher.setLastName(j.optString("lastName"));
                teacher.setPassword(j.optString("password"));
                teacher.setUserName(j.optString("userName"));

                DB_teacher_Id = teacher.getUserName();
                DB_password = teacher.getPassword();

                //Here we redirect the teacher to his/her page with teacher object
                // if the input username and password are equal to the correspond values in database.
                if (DB_teacher_Id.equalsIgnoreCase(username)
                        && DB_password.equalsIgnoreCase(password)) {

                    Intent intent = new Intent("com.example.feranmi.assignmenttrackingapp.TeacherPage");
                    intent.putExtra("teacher", teacher);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);

                }

            } catch (NullPointerException e) {
                Log.e("Null Pointer Error", "My error: " + e.toString());
            } catch (JSONException e) {
                Log.e("JSON Parsing Error", "My Error " + e.toString());
            }
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }

    }

}
