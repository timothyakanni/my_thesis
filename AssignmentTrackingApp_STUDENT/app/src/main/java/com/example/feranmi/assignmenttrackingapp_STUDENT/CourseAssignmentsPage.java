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



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import project.dto.Assignment;

public class CourseAssignmentsPage extends AppCompatActivity {

    static JSONObject jObj = null;
    static String json = "";

    TextView selectedCourseName;

    String courseName;
    String courseId;
    String teacherID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_assignments_page);

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        courseName = extras.getString("courseName");
        courseId = extras.getString("courseId");
        teacherID = extras.getString("teacherID");

        selectedCourseName = (TextView) findViewById(R.id.selectedCourseName);
        selectedCourseName.setText(courseName);

        CourseAssignmentsBT courseAssignmentsBT = new CourseAssignmentsBT(this);
        courseAssignmentsBT.execute(courseId);

    }


    private class CourseAssignmentsBT extends AsyncTask<String, Void, JSONObject> {

        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat destFormat = new SimpleDateFormat("d MMM, yyyy hh:mm:ss a");

        Activity activity;
        AlertDialog alertDialog;

        JSONArray courseAssignments = null;
        String course_id;
        Assignment assignment = new Assignment();

        private static final String TAG_COURSEDESCRIPTION = "description";
        private static final String TAG_DUEDATETIME = "endDateTime";
        private static final String TAG_COMPLETIONRATE = "completionRate";

        ArrayList<HashMap<String, String>> assignmentList = new ArrayList<HashMap<String, String>>();
        ListView list;

        public CourseAssignmentsBT(Activity activity){
            this.activity = activity;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String course_assignments_url = "http://www.cc.puv.fi/~e1100617/" +
                    "AssignmentTrackingApp/courseAssignmentsPage.php";
            course_id = params[0];

            try {

                URL url = new URL(course_assignments_url);
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

            //Changes start here
            try {

                courseAssignments = json.getJSONArray("courseAssignments");
                for (int i = 0; i < courseAssignments.length(); i++) {

                    JSONObject j = courseAssignments.getJSONObject(i);

                    assignment.setDescription(j.optString("description"));
                    assignment.setCompletionRate(j.optDouble("completionRate"));


                    String endDateTime = j.optString("endDateTime");
                    Date date = null;
                    try {

                        date = sourceFormat.parse(endDateTime);
                        assignment.setEndDateTime(date);

                    }catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //String formattedDate = destFormat.format(date);

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_COURSEDESCRIPTION, assignment.getDescription());
                    map.put(TAG_COMPLETIONRATE, assignment.getCompletionRate().toString());
                    map.put(TAG_DUEDATETIME, destFormat.format(assignment.getEndDateTime()));

                    assignmentList.add(map);
                    list=(ListView) findViewById(R.id.teacherCourseAssignmentsListView);

                    ListAdapter adapter = new SimpleAdapter(CourseAssignmentsPage.this, assignmentList,
                            R.layout.teachercourse_assignmentspage_list_item,
                            new String[] {TAG_COURSEDESCRIPTION, TAG_DUEDATETIME, TAG_COMPLETIONRATE},
                            new int[] {R.id.assignmentDescription, R.id.assignmentDueDate,
                            R.id.assignmentCompletionRate});

                    list.setAdapter(adapter);
                }


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


}
