package com.example.feranmi.assignmenttrackingapp_STUDENT;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddCourse extends AppCompatActivity implements View.OnClickListener {

    EditText idEditText, nameEditText, descriptionEditText;
    EditText startDateEditText, endDateEditText, coursePasswordEditText;

    TextView addCourseMessage;

    String teacherID;
    String courseId,courseName,description,startDate,endDate,coursePwd;

    TextView homePageTextView;
    Intent teacherPage;

    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog endDatePickerDialog;

    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        Intent intent = getIntent();
        teacherID = intent.getExtras().getString("teacherID");

        addCourseMessage = (TextView) findViewById(R.id.courseAddingError);

        idEditText = (EditText)findViewById(R.id.courseIdEditText);
        nameEditText = (EditText)findViewById(R.id.courseNameEditText);
        descriptionEditText = (EditText)findViewById(R.id.descriptionEditText);
        coursePasswordEditText = (EditText)findViewById(R.id.passwordEditText);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        startDateEditText = (EditText)findViewById(R.id.startDateEditText);
        startDateEditText.setInputType(InputType.TYPE_NULL);
        //startDateEditText.requestFocus();

        endDateEditText = (EditText) findViewById(R.id.endDateEditText);
        endDateEditText.setInputType(InputType.TYPE_NULL);

        seTDateTimeParameter();

        //calling method for returning to teacher home page or logout
        onHomePage();
        onLogout();
    }

    @Override
    public void onClick(View v){
        if(v == startDateEditText){
            startDatePickerDialog.show();
        }else if(v == endDateEditText){
            endDatePickerDialog.show();
        }
    }


    //This method is for setting the date parameters when adding course
    private void seTDateTimeParameter(){

        startDateEditText.setOnClickListener(this);
        endDateEditText.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();

        //This is the part for start date picker and setting the value
        startDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                startDateEditText.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        //This is the part for end date picker and setting the value
        endDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                endDateEditText.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


    }

    public void onHomePage(){
        homePageTextView = (TextView)findViewById(R.id.homePage);
        homePageTextView.setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View v){
                        teacherPage = new Intent(AddCourse.this, TeacherPage.class);
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


    public void onAddCourse(View v){

        courseId = idEditText.getText().toString();
        courseName = nameEditText.getText().toString();
        description = descriptionEditText.getText().toString();
        startDate = startDateEditText.getText().toString();
        endDate = endDateEditText.getText().toString();
        coursePwd = coursePasswordEditText.getText().toString();

        try{
            if(courseId.isEmpty()|| courseName.isEmpty() || description.isEmpty()
                    || startDate.isEmpty() || endDate.isEmpty() || coursePwd.isEmpty()){
                addCourseMessage.setText("Check Missing Input Value!!!");

            }else{
                AddCourseBT addCourseBT = new AddCourseBT(this);
                addCourseBT.execute(courseId,courseName,description,
                        startDate,endDate,coursePwd,teacherID);
            }
        }catch(NullPointerException e){
            Log.e("Null Pointer Error", "My error: " + e.toString());
            addCourseMessage.setText("Check Missing Input Value!!!");
        }
    }
    //This is Background Task class that extends Async Task for adding course
    private class AddCourseBT extends AsyncTask<String, Void, String> {

        Activity activity;
        AlertDialog alertDialog;

        String course_id;
        String course_name;
        String course_description;
        String start_date;
        String end_date;
        String password;
        String teacher_id;

        public AddCourseBT(Activity activity){
            this.activity = activity;
        }
        @Override
        protected String doInBackground(String... params) {

            String add_course_url = "http://www.cc.puv.fi/~e1100617/" +
                    "AssignmentTrackingApp/addCourse.php";
            course_id = params[0];
            course_name = params[1];
            course_description = params[2];
            start_date = params[3];
            end_date = params[4];
            password = params[5];
            teacher_id = params[6];


            try {

                URL url = new URL(add_course_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("course_id", "UTF-8") + "=" + URLEncoder.encode(course_id, "UTF-8") + "&"
                        + URLEncoder.encode("course_name", "UTF-8") + "=" + URLEncoder.encode(course_name, "UTF-8") + "&"
                        + URLEncoder.encode("course_description", "UTF-8") + "=" + URLEncoder.encode(course_description, "UTF-8") + "&"
                        + URLEncoder.encode("start_date", "UTF-8") + "=" + URLEncoder.encode(start_date, "UTF-8") + "&"
                        + URLEncoder.encode("end_date", "UTF-8") + "=" + URLEncoder.encode(end_date, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&"
                        + URLEncoder.encode("teacher_id", "UTF-8") + "=" + URLEncoder.encode(teacher_id, "UTF-8");

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
                if(result.equalsIgnoreCase("Course Adding Successfull")) {
                    addCourseMessage.setText("Successful!!!");
                }else if(result.equalsIgnoreCase("Error")) {
                    addCourseMessage.setText("Not successful!!!");
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
