package com.example.feranmi.assignmenttrackingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.HashMap;

import project.dto.Course;


public class CourseEnrollmentPage extends AppCompatActivity {

    TextView courseEnrollmentMessage;
    EditText studentFirstname, studentLastname, coursePassword;

    String courseName;
    String studentNumber;
    String courseId;

    String firstname, lastname, coursePwd;

    static JSONObject jObj = null;
    static String json = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_enrollment_page);

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        studentNumber = extras.getString("studentNumber");

        courseEnrollmentMessage = (TextView) findViewById(R.id.courseEnrollmentError);

        studentFirstname = (EditText)findViewById(R.id.firstnameEditText);
        studentLastname = (EditText)findViewById(R.id.lastnameEditText);
        coursePassword = (EditText)findViewById(R.id.coursePasswordEditText);

        CourseNameBT courseNameBT = new CourseNameBT(this);
        courseNameBT.execute();

    }

    public void OnEnroll(View v){

        firstname = studentFirstname.getText().toString();
        lastname = studentLastname.getText().toString();
        coursePwd = coursePassword.getText().toString();

        if(firstname.isEmpty() || lastname.isEmpty() ||
                coursePwd.isEmpty() || courseId.isEmpty() ){

            courseEnrollmentMessage.setText("Check Missing Input Value!!!");
        }else{

            StudentEnrollmentBT studentEnrollmentBT = new StudentEnrollmentBT(this);
            studentEnrollmentBT.execute(studentNumber,courseId, coursePwd);
        }
    }


    //This is Background Task class that extends Async Task which populate
    //spinner with all courses names
    private class CourseNameBT extends AsyncTask<String, Void, JSONObject> {

        Activity activity;
        AlertDialog alertDialog;

        JSONArray allCourses = null;

        private static final String TAG_COURSENAME = "name";
        private static final String TAG_COURSEID = "id";




        ArrayList<String> courseNameList = new ArrayList<String>();
        ArrayList<Course> courseList = new ArrayList<Course>();
        Spinner courseNameSpinner;

        public CourseNameBT(Activity activity){

            this.activity = activity;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String all_courses_url = "http://www.cc.puv.fi/~e1100617/courseEnrollmentPage_1.php";

            try {

                URL url = new URL(all_courses_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);

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
            //alertDialog.show();


            //Changes start here
            try {
                allCourses = json.getJSONArray("allCourses");
                for (int i = 0; i < allCourses.length(); i++) {
                    JSONObject j = allCourses.getJSONObject(i);

                    Course course= new Course();

                    course.setId(j.optString(TAG_COURSEID));
                    course.setName(j.optString(TAG_COURSENAME));

                    //Here course object is added to array list of courses
                    courseList.add(course);

                    //This part is used to populate spinner with course name
                    courseNameList.add(course.getName());

                }
                courseNameSpinner = (Spinner) findViewById(R.id.course_name_spinner);

                //Here, I set the adapter for spinner to populate the spinner with
                //course names using array list of strings courseNameList
                courseNameSpinner
                        .setAdapter(new ArrayAdapter<String>(CourseEnrollmentPage.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                courseNameList ));


                // Spinner on item selected listener
                courseNameSpinner
                        .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> arg0,
                                                       View arg1, int position, long arg3) {

                                courseId = courseList.get(position).getId();
                                courseName = courseList.get(position).getName();

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {

                                courseEnrollmentMessage.setText("You have not selected any Course!!!");

                            }
                        });

            }catch(JSONException e) {
                Log.e("JSON Parsing Error", "My Error " + e.toString());
            }catch(NullPointerException e){
                Log.e("JSON Null Exception", "My Error Null Error: " + e.toString());
            }

        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }

    }


    //This is Background Task class that extends Async Task which inserts
    //enrollment data(s) to enrollment table for student to enroll for course
    private class StudentEnrollmentBT extends AsyncTask<String, Void, String> {

        Activity activity;
        AlertDialog alertDialog;

        String username;
        String course_id;
        String course_password;

        public StudentEnrollmentBT(Activity activity){

            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {

            String student_enrollment_url = "http://www.cc.puv.fi/~e1100617/courseEnrollmentPage_2.php";
            username = params[0];
            course_id = params[1];
            course_password = params[2];



            try {

                URL url = new URL(student_enrollment_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Student_student_No", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&"
                        + URLEncoder.encode("Course_id", "UTF-8") + "=" + URLEncoder.encode(course_id, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(course_password, "UTF-8");

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

                if(result.equalsIgnoreCase("Enrollment success")) {

                    courseEnrollmentMessage.setText("Your Enrollment was successful!!!");

                }else if(result.equalsIgnoreCase("Error")) {

                    courseEnrollmentMessage.setText("Your Enrollment was not successful!!!");
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
