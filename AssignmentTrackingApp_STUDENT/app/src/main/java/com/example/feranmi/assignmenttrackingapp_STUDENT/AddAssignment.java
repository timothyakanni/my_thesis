package com.example.feranmi.assignmenttrackingapp_STUDENT;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import project.dto.Course;

public class AddAssignment extends AppCompatActivity implements View.OnClickListener{

    TextView addAssignmentMessage;

    EditText assignmentIdEditText, descriptionEditText;
    EditText startDateEditText, endDateEditText;
    EditText startTimeEditText, endTimeEditText;

    TextView homePageTextView;
    Intent teacherPage;

    String assignmentId, description, startDate, endDate;
    String startTime, endTime;

    String courseName;
    String courseId;
    String teacherID;

    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog endDatePickerDialog;

    private TimePickerDialog startTimePickerDialog;
    private TimePickerDialog endTimePickerDialog;

    private SimpleDateFormat dateFormatter;


    static JSONObject jObj = null;
    static String json = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);
        Intent intent = getIntent();
        teacherID = intent.getExtras().getString("teacherID");

        addAssignmentMessage = (TextView) findViewById(R.id.assignmentAddingError);
        assignmentIdEditText = (EditText)findViewById(R.id.assignmentIdEditText);
        descriptionEditText = (EditText)findViewById(R.id.descriptionEditText);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        startDateEditText = (EditText)findViewById(R.id.startDateEditText);
        startDateEditText.setInputType(InputType.TYPE_NULL);
        endDateEditText = (EditText) findViewById(R.id.endDateEditText);
        endDateEditText.setInputType(InputType.TYPE_NULL);

        startTimeEditText = (EditText) findViewById(R.id.startTimeEditText);
        startTimeEditText.setInputType(InputType.TYPE_NULL);
        endTimeEditText = (EditText) findViewById(R.id.endTimeEditText);
        endTimeEditText.setInputType(InputType.TYPE_NULL);

        seTDateTimeParameter();
        //calling method for returning to teacher home page
        onHomePage();
        onLogout();
        TeacherCoursesNameBT teacherCoursesNameBT = new TeacherCoursesNameBT(this);
        teacherCoursesNameBT.execute(teacherID);
    }

    public void onHomePage(){
        homePageTextView = (TextView)findViewById(R.id.homePage);
        homePageTextView.setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View v){
                        teacherPage = new Intent(AddAssignment.this, TeacherPage.class);
                        startActivity(teacherPage);
                    }
                }
        );
    }

    public void onLogout(){
       /* //Here we are just calling the logout method in Teacher Page
        TeacherPage teacherPage = new TeacherPage();
        teacherPage.Logout();*/
    }

    public void onAddAssignment(View v){

        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        startTime = startTimeEditText.getText().toString();
        endTime = endTimeEditText.getText().toString();

        assignmentId = courseId + "_" + assignmentIdEditText.getText().toString();
        description = descriptionEditText.getText().toString();
        startDate = startDateEditText.getText().toString() + " " + startTime;

        try{
            Date sourceStartDate;
            sourceStartDate = sourceFormat.parse(startDate);

            startDate = destFormat.format(sourceStartDate);

        }catch(ParseException e){
            e.printStackTrace();
        }

        endDate = endDateEditText.getText().toString() + " " + endTime;
        try{
            Date sourceEndDate;
            sourceEndDate = sourceFormat.parse(endDate);

            endDate = destFormat.format(sourceEndDate);

        }catch(ParseException e){
            e.printStackTrace();
        }

        try{
            if(assignmentId.isEmpty() || description.isEmpty() || startDate.isEmpty() ||
                startTime.isEmpty()|| endDate.isEmpty() || endTime.isEmpty() || courseId.isEmpty()){

                addAssignmentMessage.setText("Check Missing Input Value!!!");

            }else{

                AddAssignmentBT addAssignmentBT = new AddAssignmentBT(this);
                addAssignmentBT.execute(assignmentId,description,
                        startDate,endDate,courseId);
            }
        }catch(NullPointerException e){
            Log.e("Null Pointer Error", "My error: " + e.toString());
            addAssignmentMessage.setText("Check Missing Input Value!!!");
        }

    }

    @Override
    public void onClick(View v){
        if(v == startDateEditText){
            startDatePickerDialog.show();
        }else if(v == endDateEditText){
            endDatePickerDialog.show();
        }else if(v == startTimeEditText){
            startTimePickerDialog.show();
        }
        else if(v == endTimeEditText){
            endTimePickerDialog.show();
        }
    }

    //This method is for setting the date parameters when adding course
    private void seTDateTimeParameter(){

        startDateEditText.setOnClickListener(this);
        endDateEditText.setOnClickListener(this);

        startTimeEditText.setOnClickListener(this);
        endTimeEditText.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();

        startDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                startDateEditText.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        endDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                endDateEditText.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        startTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {

                String setTime = hour+":"+minute;
                startTimeEditText.setText(setTime);
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

        endTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {

                String setTime = hour+":"+minute;
                endTimeEditText.setText(setTime);
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

    }



    //This is Background Task class that extends Async Task which populate
    //spinner with courses names for the particular teacher that login
    private class TeacherCoursesNameBT extends AsyncTask<String, Void, JSONObject> {

        Activity activity;
        AlertDialog alertDialog;

        JSONArray teacherCourses = null;
        private static final String TAG_COURSENAME = "name";
        private static final String TAG_COURSEID = "id";

        ArrayList<String> courseNameList = new ArrayList<String>();
        ArrayList<Course> courseList = new ArrayList<Course>();
        Spinner courseNameSpinner;
        String teacher_id;

        public TeacherCoursesNameBT(Activity activity){
            this.activity = activity;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String teacher_courses_url = "http://www.cc.puv.fi/~e1100617/" +
                    "AssignmentTrackingApp/addAssignmentPage_1.php";
            teacher_id = params[0];

            try {

                URL url = new URL(teacher_courses_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("teacher_id", "UTF-8") + "=" + URLEncoder.encode(teacher_id, "UTF-8");

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
                teacherCourses = json.getJSONArray("teacherCourses");
                for (int i = 0; i < teacherCourses.length(); i++) {
                    JSONObject j = teacherCourses.getJSONObject(i);
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
                        .setAdapter(new ArrayAdapter<String>(AddAssignment.this,
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
                                addAssignmentMessage.setText("You have not selected any Course!!!");
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

    //This is Background Task class that extends Async Task for adding assignment
    private class AddAssignmentBT extends AsyncTask<String, Void, String> {

        Activity activity;
        AlertDialog alertDialog;

        String assignment_id;
        String assignment_description;
        String start_date_time;
        String end_date_time;
        String course_id;

        public AddAssignmentBT(Activity activity){
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {
            String add_assignment_url = "http://www.cc.puv.fi/~e1100617/" +
                    "AssignmentTrackingApp/addAssignmentPage_2.php";
            assignment_id = params[0];
            assignment_description = params[1];
            start_date_time = params[2];
            end_date_time = params[3];
            course_id = params[4];


            try {

                URL url = new URL(add_assignment_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("assignment_id", "UTF-8") + "=" + URLEncoder.encode(assignment_id, "UTF-8") + "&"
                        + URLEncoder.encode("assignment_description", "UTF-8") + "=" + URLEncoder.encode(assignment_description, "UTF-8") + "&"
                        + URLEncoder.encode("start_date_time", "UTF-8") + "=" + URLEncoder.encode(start_date_time, "UTF-8") + "&"
                        + URLEncoder.encode("end_date_time", "UTF-8") + "=" + URLEncoder.encode(end_date_time, "UTF-8") + "&"
                        + URLEncoder.encode("course_id", "UTF-8") + "=" + URLEncoder.encode(course_id, "UTF-8");

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
            alertDialog.setTitle("Add Assignment Status");

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try{

                if(result.equalsIgnoreCase("Assignment Adding Successfull")) {
                    addAssignmentMessage.setText("Successful!!!");
                }else if(result.equalsIgnoreCase("Error")) {
                    addAssignmentMessage.setText("Not successful!!!, Assignment already exists");
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
