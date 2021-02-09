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

import project.dto.Student;

public class StudentLogin extends AppCompatActivity {
    EditText studentUsername;
    EditText studentPassword;
    TextView registerLink;

    static JSONObject jObj = null;
    static String json = "";

    TextView studentLoginMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        studentUsername= (EditText)findViewById(R.id.studentEditText);
        studentPassword= (EditText)findViewById(R.id.passwordEditText);
        studentLoginMessage = (TextView)findViewById(R.id.studentLoginError);

        StudentRegistration();
    }
    //This is the method for login implementation
    public void OnLogin(View v){

        String studentNumber = studentUsername.getText().toString();
        String password = studentPassword.getText().toString();
        if(studentNumber.isEmpty() || password.isEmpty()){

            studentLoginMessage.setText("Check Missing Input Value");
        }else {

            StudentLoginBT studentLoginBT = new StudentLoginBT(this);
            studentLoginBT.execute(studentNumber, password);
        }
    }

    //This is the method that redirect student to student registration page
    public void StudentRegistration(){
        registerLink = (TextView)findViewById(R.id.studentRegister);
        registerLink.setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("com.example.feranmi.assignmenttrackingapp.StudentRegistration");
                        startActivity(intent);
                    }
                }
        );
    }

    //This is Background Task class that extends Async Task for student login
    private class StudentLoginBT extends AsyncTask<String, Void, JSONObject> {

        Activity activity;
        AlertDialog alertDialog;

        JSONArray studentInfo = null;

        String username;
        String password;

        String DB_student_No;
        String DB_password;

        Student student= new Student();

        public StudentLoginBT(Activity activity){

            this.activity = activity;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String student_login_url = "http://www.cc.puv.fi/~e1100617/AssignmentTrackingApp/studentLogin.php";
            username = params[0];
            password = params[1];

            try {

                URL url = new URL(student_login_url);
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
            alertDialog.setMessage(json.toString());

                try {
                    studentInfo = json.getJSONArray("studentInfo");
                    if(studentInfo.isNull(0)){

                        studentLoginMessage.setText("Logging Error: Incorrect Username or Password");
                    }
                    JSONObject j = studentInfo.getJSONObject(0);

                    //student object is created and set methods in student class are used
                    //to set student variables
                    student.setFirstName(j.optString("firstName"));
                    student.setLastName(j.optString("lastName"));
                    student.setPassword(j.optString("passord"));
                    student.setStudentNumber(j.optString("student_No"));
                    //student.setSubmissionRate(j.getDouble("SubmissionRate"));

                    DB_student_No = student.getStudentNumber();
                    DB_password = student.getPassword();

                    //Here we redirect the student to his/her page with student object
                    // if the input username and password are equal to the correspond values in database.
                    if (DB_student_No.equalsIgnoreCase(username)
                            && DB_password.equalsIgnoreCase(password)) {

                        Intent intent = new Intent("com.example.feranmi.assignmenttrackingapp.StudentPage");
                        intent.putExtra("student", student);
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
