package com.example.feranmi.assignmenttrackingapp_STUDENT;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

import project.dto.Assignment;


public class StudentAssignmentPage extends AppCompatActivity {

    TextView assignmentCourseName;
    TextView submissionRate;

    String courseName;
    String courseId;
    String studentNumber;

    static JSONObject jObj = null;
    static String json = "";

    public static int availableAssignmentCount;
    public static int submittedAssignmentCount = 0;
    public static Integer submissionRateValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assignment_page);

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        courseName = extras.getString("courseName");
        courseId = extras.getString("courseId");
        studentNumber = extras.getString("studentNumber");

        assignmentCourseName = (TextView) findViewById(R.id.assignmentCourseName);
        assignmentCourseName.setText(courseName);

        submissionRate = (TextView) findViewById(R.id.submissionRateValue);

        AvailableAssignmentBT availableAssignmentBT = new AvailableAssignmentBT(this);
        availableAssignmentBT.execute(courseId);

        SubmittedAssignmentBT submittedAssignmentBT = new SubmittedAssignmentBT(this);
        submittedAssignmentBT.execute(courseId,studentNumber);

    }


    //This is Background Task class that extends Async Task
    private class AvailableAssignmentBT extends AsyncTask<String, Void, JSONObject> {

        Activity activity;
        AlertDialog alertDialog;

        JSONArray availableAssignment = null;
        String course_id;
        Assignment assignment = new Assignment();

        private static final String TAG_COURSEDESCRIPTION = "description";
        ArrayList<HashMap<String, String>> assignmentList = new ArrayList<HashMap<String, String>>();
        ListView list;

        public AvailableAssignmentBT(Activity activity){
            this.activity = activity;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String available_assignments_url = "http://www.cc.puv.fi/~e1100617/" +
                    "AssignmentTrackingApp/studentAssignmentPage_1.php";
            course_id = params[0];

            try {

                URL url = new URL(available_assignments_url);
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
            //alertDialog.setMessage(json.toString());
            //alertDialog.show();

            //Changes start here
            try {

                availableAssignment = json.getJSONArray("availableAssignment");
                for (int i = 0; i < availableAssignment.length(); i++) {

                    JSONObject j = availableAssignment.getJSONObject(i);

                    assignment.setDescription(j.getString("description"));

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_COURSEDESCRIPTION, assignment.getDescription());

                    assignmentList.add(map);
                    list=(ListView) findViewById(R.id.studentAvailableAssignmentListView);

                    ListAdapter adapter = new SimpleAdapter(StudentAssignmentPage.this, assignmentList,
                            R.layout.available_assignment_list_item,
                            new String[] {TAG_COURSEDESCRIPTION}, new int[] {R.id.studentAvailableAssignment});

                    list.setAdapter(adapter);
                }

                availableAssignmentCount = list.getAdapter().getCount();
                //assignmentCourseName.append(" " + availableAssignmentCount);
                //alertDialog.setMessage((Integer.toString(list.getAdapter().getCount())));
                //alertDialog.show();

            }catch(JSONException e) {
                Log.e("JSON Parsing Error", "My Error: Post Execute" + e.toString());
            }catch(NullPointerException e){
                Log.e("JSON Null Exception", "My Error: Post Execute" + e.toString());
            }

        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }

    }

    //This is Submitted Assignment Background Task class that extends Async Task
    private class SubmittedAssignmentBT extends AsyncTask<String, Void, JSONObject> {

        Activity activity;
        AlertDialog alertDialog;

        JSONArray submittedAssignment = null;
        String course_id;
        String student_number;
        Assignment assignment = new Assignment();

        private static final String TAG_COURSEDESCRIPTION = "description";
        ArrayList<HashMap<String, String>> assignmentList = new ArrayList<HashMap<String, String>>();

        ListView list;

        public SubmittedAssignmentBT(Activity activity){
            this.activity = activity;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String submitted_assignments_url = "http://www.cc.puv.fi/~e1100617/" +
                    "AssignmentTrackingApp/studentAssignmentPage_2.php";
            course_id = params[0];
            student_number = params[1];


            try {

                URL url = new URL(submitted_assignments_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("course_id", "UTF-8") + "=" + URLEncoder.encode(course_id, "UTF-8") + "&"
                        + URLEncoder.encode("student_number", "UTF-8") + "=" + URLEncoder.encode(student_number, "UTF-8");

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

                submittedAssignment = json.getJSONArray("submittedAssignment");
                for (int i = 0; i < submittedAssignment.length(); i++) {

                    JSONObject j = submittedAssignment.getJSONObject(i);

                    assignment.setDescription(j.getString("description"));

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_COURSEDESCRIPTION, assignment.getDescription());

                    assignmentList.add(map);
                    list=(ListView) findViewById(R.id.studentSubmittedAssignmentListView);

                    ListAdapter adapter = new SimpleAdapter(StudentAssignmentPage.this, assignmentList,
                            R.layout.submitted_assignment_list_item,
                            new String[] {TAG_COURSEDESCRIPTION}, new int[] {R.id.studentSubmittedAssignment});

                    list.setAdapter(adapter);
                }
                //This is where submission Rate Value is calculated and set
                submittedAssignmentCount = list.getAdapter().getCount();
                submissionRateValue = (int)(((double)submittedAssignmentCount/(double)availableAssignmentCount)*100);
                submissionRate.setText(" " + submissionRateValue + "%");


            }catch(JSONException e) {
                Log.e("JSON Parsing Error", "My Error: Post Execute" + e.toString());
            }catch(NullPointerException e){
                Log.e("JSON Null Exception", "My Error: Post Execute" + e.toString());
            }

        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }

    }

}
