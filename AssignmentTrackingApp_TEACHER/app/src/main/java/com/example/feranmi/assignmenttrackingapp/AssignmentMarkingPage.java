package com.example.feranmi.assignmenttrackingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import project.dto.Assignment;
import project.dto.Student;

public class AssignmentMarkingPage extends AppCompatActivity {

    static JSONObject jObj = null;
    static String json = "";

    TextView selectedCourseName;
    TextView markAssignmentMessage;
    TextView logoutLink;

    SharedPreferences sharedPreferences;
    public static final String MySession = "MySession";

    String courseName;
    String courseId;
    String teacherID;

    String student_Number;
    String studentName;
    String assignmentId;
    String assignmentDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_marking_page);

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        courseName = extras.getString("courseName");
        courseId = extras.getString("courseId");
        teacherID = extras.getString("teacherID");

        markAssignmentMessage = (TextView) findViewById(R.id.markAssignmentError);

        selectedCourseName = (TextView) findViewById(R.id.selectedCourseName);
        selectedCourseName.setText(courseName);

        StudentIdBT studentIdBT = new StudentIdBT(this);
        studentIdBT.execute(courseId);

        AssignmentBT assignmentBT = new AssignmentBT(this);
        assignmentBT.execute(courseId);

        sharedPreferences = getSharedPreferences(MySession, Context.MODE_PRIVATE);
        //calling method for returning to teacher home page
        //onHomePage();
        onLogout();

    }

    public void onLogout(){
        logoutLink = (TextView)findViewById(R.id.logout);
        logoutLink.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.apply();

                        Intent loginPage = new Intent(AssignmentMarkingPage.this, TeacherLogin.class);
                        startActivity(loginPage);
                    }
                }
        );

    }

    public void OnMark(View v){

        try{
            if(student_Number.isEmpty()|| student_Number==null ||
                    assignmentId.isEmpty() || assignmentId==null ){

                markAssignmentMessage.setText("Check Missing Input Value!!!");
            }else{

                MarkAssignmentBT markAssignmentBT = new MarkAssignmentBT(this);
                markAssignmentBT.execute(student_Number,assignmentId, courseId);
            }

        }catch(NullPointerException e){
            Log.e("Null Pointer Exception", "For onMark data values: " + e.toString());
            markAssignmentMessage.setText("Check Missing Input Value!!!");
        }

    }


    public void OnUnMark(View v){

        try{
            if(student_Number.isEmpty()|| student_Number==null ||
                    assignmentId.isEmpty() || assignmentId==null ){

                markAssignmentMessage.setText("Check Missing Input Value!!!");
            }else{

                UnMarkAssignmentBT unmarkAssignmentBT = new UnMarkAssignmentBT(this);
                unmarkAssignmentBT.execute(student_Number,assignmentId, courseId);
            }

        }catch(NullPointerException e){
            Log.e("Null Pointer Exception", "For onUnMark data values: " + e.toString());
            markAssignmentMessage.setText("Check Missing Input Value!!!");
        }

    }

    private class StudentIdBT extends AsyncTask<String, Void, JSONObject> {

        Activity activity;
        AlertDialog alertDialog;

        String course_id;
        JSONArray courseStudents = null;
        private static final String TAG_STUDENTID = "Student_student_No";
        private static final String TAG_STUDENTFIRSTNAME = "firstName";
        private static final String TAG_STUDENTLASTNAME = "lastName";

        ArrayList<String> studentIdList = new ArrayList<String>();
        ArrayList<Student> studentList = new ArrayList<Student>();

        Spinner studentIdSpinner;
        public StudentIdBT(Activity activity){
            this.activity = activity;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String display_students_url = "http://www.cc.puv.fi/~e1100617/" +
                    "AssignmentTrackingApp/assignmentMarkingPage_1.php";
            course_id = params[0];

            try {

                URL url = new URL(display_students_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("course_id", "UTF-8") + "=" + URLEncoder.encode(course_id, "UTF-8");

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
            //alertDialog.show();

            try {
                courseStudents = json.getJSONArray("courseStudents");
                for (int i = 0; i < courseStudents.length(); i++) {
                    JSONObject j = courseStudents.getJSONObject(i);

                    Student student = new Student();
                    student.setStudentNumber(j.optString(TAG_STUDENTID));
                    student.setFirstName(j.optString(TAG_STUDENTFIRSTNAME));
                    student.setLastName(j.optString(TAG_STUDENTLASTNAME));

                    //Here student object is added to array list of student
                    studentList.add(student);

                    //This part is used to populate spinner with student number
                    studentIdList.add(student.getStudentNumber());

                }
                studentIdSpinner = (Spinner) findViewById(R.id.student_id_spinner);
                //Here, I set the adapter for spinner to populate the spinner with
                //course names using array list of strings courseNameList
                studentIdSpinner
                        .setAdapter(new ArrayAdapter<String>(AssignmentMarkingPage.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                studentIdList ));


                // Spinner on item selected listener
                studentIdSpinner
                        .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> arg0,
                                                       View arg1, int position, long arg3) {
                                student_Number = studentList.get(position).getStudentNumber();
                                studentName = studentList.get(position).getFirstName() + " " +
                                        studentList.get(position).getLastName();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                                student_Number = "";
                                markAssignmentMessage.setText("You have not selected any Student ID!!!");
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


    private class AssignmentBT extends AsyncTask<String, Void, JSONObject> {

        Activity activity;
        AlertDialog alertDialog;

        String course_id;
        JSONArray courseAssignments = null;

        private static final String TAG_ID = "id";
        private static final String TAG_DESCRIPTION = "description";

        ArrayList<String> assignmentDescriptionList = new ArrayList<String>();
        ArrayList<Assignment> assignmentList = new ArrayList<Assignment>();

        Spinner assignmentSpinner;

        public AssignmentBT(Activity activity){
            this.activity = activity;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String display_assignments_url = "http://www.cc.puv.fi/~e1100617/" +
                    "AssignmentTrackingApp/assignmentMarkingPage_2.php";
            course_id = params[0];

            try {

                URL url = new URL(display_assignments_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("course_id", "UTF-8") + "=" + URLEncoder.encode(course_id, "UTF-8");

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
            //alertDialog.show();

            try {
                courseAssignments = json.getJSONArray("courseAssignments");
                for (int i = 0; i < courseAssignments.length(); i++) {
                    JSONObject j = courseAssignments.getJSONObject(i);

                    Assignment assignment = new Assignment();

                    assignment.setId(j.optString(TAG_ID));
                    assignment.setDescription(j.optString(TAG_DESCRIPTION));

                    //Here assignment object is added to array list of assignment list
                    assignmentList.add(assignment);

                    //This part is used to populate spinner with assignment description
                    assignmentDescriptionList.add(assignment.getDescription());

                }
                assignmentSpinner = (Spinner) findViewById(R.id.assignment_spinner);

                //Here, I set the adapter for spinner to populate the spinner with
                //assignment description using array list of strings assignmentDescriptionList
                assignmentSpinner
                        .setAdapter(new ArrayAdapter<String>(AssignmentMarkingPage.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                assignmentDescriptionList ));


                // Spinner on item selected listener
                assignmentSpinner
                        .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> arg0,
                                                       View arg1, int position, long arg3) {

                                assignmentId = assignmentList.get(position).getId();
                                assignmentDescription = assignmentList.get(position).getDescription();
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                                assignmentId = "";
                                markAssignmentMessage.setText("You have not selected any Assignment!!!");

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
    //assignment and student data(s) to submitted assignment table for
    // student when teacher marks assignment for student
    private class MarkAssignmentBT extends AsyncTask<String, Void, String> {

        Activity activity;
        AlertDialog alertDialog;

        String username;
        String course_id;
        String assignment_id;

        public MarkAssignmentBT(Activity activity){

            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {

            String mark_assignment_url = "http://www.cc.puv.fi/~e1100617/" +
                    "AssignmentTrackingApp/assignmentMarkingPage_3.php";
            username = params[0];
            assignment_id = params[1];
            course_id = params[2];



            try {

                URL url = new URL(mark_assignment_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Student_student_No", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&"
                        + URLEncoder.encode("assignment_id", "UTF-8") + "=" + URLEncoder.encode(assignment_id, "UTF-8") + "&"
                        + URLEncoder.encode("Course_id", "UTF-8") + "=" + URLEncoder.encode(course_id, "UTF-8");

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
                if(result.equalsIgnoreCase("Marking successfull")) {
                    markAssignmentMessage.setText(assignmentDescription + "  marked for  " +
                            student_Number + " " + studentName);

                }else if(result.equalsIgnoreCase("Error")) {
                    markAssignmentMessage.setText("Marking FAILED, Assignment has been marked before!!!");
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


    //This is Background Task class that extends Async Task which deletes
    //assignment and student data(s) from submitted assignment table for
    // student when teacher un_marks assignment for student
    private class UnMarkAssignmentBT extends AsyncTask<String, Void, String> {

        Activity activity;
        AlertDialog alertDialog;

        String username;
        String course_id;
        String assignment_id;

        public UnMarkAssignmentBT(Activity activity){

            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {

            String mark_assignment_url = "http://www.cc.puv.fi/~e1100617/" +
                    "AssignmentTrackingApp/assignmentMarkingPage_4.php";
            username = params[0];
            assignment_id = params[1];
            course_id = params[2];



            try {

                URL url = new URL(mark_assignment_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Student_student_No", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&"
                        + URLEncoder.encode("assignment_id", "UTF-8") + "=" + URLEncoder.encode(assignment_id, "UTF-8") + "&"
                        + URLEncoder.encode("Course_id", "UTF-8") + "=" + URLEncoder.encode(course_id, "UTF-8");

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
                if(result.equalsIgnoreCase("UnMarking successfull")) {
                    markAssignmentMessage.setText(assignmentDescription + "  has been unmarked for  " +
                            student_Number + " " + studentName);

                }else if(result.equalsIgnoreCase("Error")) {
                    markAssignmentMessage.setText("Un Marking FAILED, Assignment has not been marked before!!!");
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
