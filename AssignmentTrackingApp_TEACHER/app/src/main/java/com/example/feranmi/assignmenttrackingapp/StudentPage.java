package com.example.feranmi.assignmenttrackingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import project.dto.Course;
import project.dto.Student;

public class StudentPage extends AppCompatActivity {

    String student_name;
    String studentNumber;
    Student student;

    TextView enrollLink;

    static JSONObject jObj = null;
    static String json = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_page);

        Intent intent = getIntent();
        student = (Student) intent.getExtras().getSerializable("student");

        student_name = student.getFirstName() + " " + student.getLastName();

        TextView studentName = (TextView) findViewById(R.id.studentName);
        studentName.append(" " + student_name);

        studentNumber = student.getStudentNumber();

        StudentPageBT studentPageBT = new StudentPageBT(this);
        studentPageBT.execute(studentNumber);

        studentEnrollment();
    }

    public void studentEnrollment(){
        enrollLink = (TextView)findViewById(R.id.studentEnroll);
        enrollLink.setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("com.example.feranmi.assignmenttrackingapp.CourseEnrollmentPage");

                        Bundle extras = new Bundle();
                        extras.putString("studentNumber",studentNumber);

                        intent.putExtras(extras);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
        );
    }

    /*public void Logout(){
        logoutLink = (TextView)findViewById(R.id.logout);
        logoutLink.setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                        SharedPreferences sharedpreferences = getSharedPreferences(StudentLogin.MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent("com.example.feranmi.assignmenttrackingapp.StudentLogin");
                        startActivity(intent);
                    }
                }
        );
    }*/

    //This is Background Task class that extends Async Task
    private class StudentPageBT extends AsyncTask<String, Void, JSONObject> {

        Activity activity;
        AlertDialog alertDialog;

        JSONArray studentCourses = null;
        String student_number;
        Course course= new Course();

        private static final String TAG_COURSENAME = "name";
        private static final String TAG_COURSEID = "id";
        ArrayList<HashMap<String, String>> courseList = new ArrayList<HashMap<String, String>>();

        ListView list;

        public StudentPageBT(Activity activity){
            this.activity = activity;
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            String student_page_url = "http://www.cc.puv.fi/~e1100617/" +
                    "AssignmentTrackingApp/studentPage.php";
            student_number = params[0];
            try {
                URL url = new URL(student_page_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("student_number", "UTF-8") + "=" + URLEncoder.encode(student_number, "UTF-8");

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
                studentCourses = json.getJSONArray("studentCourses");
                for (int i = 0; i < studentCourses.length(); i++) {
                    JSONObject j = studentCourses.getJSONObject(i);

                    course.setId(j.getString("id"));
                    course.setName(j.getString("name"));

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_COURSENAME, course.getName());
                    map.put(TAG_COURSEID, course.getId());

                    courseList.add(map);
                    list=(ListView) findViewById(R.id.studentListView);

                    ListAdapter adapter = new SimpleAdapter(StudentPage.this, courseList,
                            R.layout.studentpage_list_item,
                            new String[] {TAG_COURSENAME}, new int[] {R.id.studentCourseName});

                    list.setAdapter(adapter);

                    //Setting click listener for each item in Course List View
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            String courseName;
                            String courseId;
                            courseName = courseList.get(+position).get(TAG_COURSENAME);
                            courseId = courseList.get(+position).get(TAG_COURSEID);
                            Intent intent = new Intent("com.example.feranmi.assignmenttrackingapp.StudentAssignmentPage");
                            Bundle extras = new Bundle();

                            extras.putString("courseName", courseName);
                            extras.putString("courseId", courseId);
                            extras.putString("studentNumber",studentNumber);

                            intent.putExtras(extras);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        }
                    });

                }

            }catch(JSONException e) {
                Log.e("JSON Parsing Error", "My Error " + e.toString());
            }catch(NullPointerException e){
                Log.e("JSON Null Exception", "My Error: " + e.toString());
            }

        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }

    }

}
