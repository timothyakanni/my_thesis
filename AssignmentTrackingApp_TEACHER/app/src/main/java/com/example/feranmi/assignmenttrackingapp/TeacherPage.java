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
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import project.dto.Teacher;

public class TeacherPage extends AppCompatActivity {

    String teacher_name;
    String teacherID;
    Teacher teacher;

    static JSONObject jObj = null;
    static String json = "";

    TextView logoutLink;
    TextView addCourse;
    TextView addAssignment;
    
    SharedPreferences sharedPreferences;
    public static final String MySession = "MySession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_page);

        Intent intent = getIntent();
        teacher = (Teacher) intent.getExtras().getSerializable("teacher");

        teacher_name = teacher.getFirstName() + " " + teacher.getLastName();

        TextView teacherName = (TextView) findViewById(R.id.teacherName);
        teacherName.append(" " + teacher_name);

        teacherID = teacher.getUserName();

        TeacherPageCourseBT teacherPageCourseBT = new TeacherPageCourseBT(this);
        teacherPageCourseBT.execute(teacherID);


        sharedPreferences = getSharedPreferences(MySession, Context.MODE_PRIVATE);

        logout();
        addCourse();
        addAssignment();
    }



    //This is the method for logout implementation
    public void logout(){
        logoutLink = (TextView)findViewById(R.id.logout);
        logoutLink.setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View v){

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("teacher_name", teacher_name);
                        editor.commit();

                        Intent homePage = new Intent(TeacherPage.this, TeacherLogin.class);
                        startActivity(homePage);

                    }
                }
        );

    }

    //This is the method that redirect to add course page
    public void addCourse(){
        addCourse = (TextView)findViewById(R.id.add_course);
        addCourse.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v){

                        Intent intent = new Intent(TeacherPage.this, AddCourse.class);
                        intent.putExtra("teacherID", teacherID);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                }
        );

    }

    //This is the method that redirect to add assignment page
    public void addAssignment(){
        addAssignment = (TextView)findViewById(R.id.add_assignment);
        addAssignment.setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View v){

                        Intent intent = new Intent(TeacherPage.this, AddAssignment.class);
                        intent.putExtra("teacherID", teacherID);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                }
        );

    }

    //This is Background Task class that extends Async Task
    private class TeacherPageCourseBT extends AsyncTask<String, Void, JSONObject> {

        Activity activity;
        AlertDialog alertDialog;

        JSONArray teacherCourses = null;

        String teacher_number;
        Course course= new Course();

        private static final String TAG_COURSENAME = "name";
        private static final String TAG_COURSEID = "id";
        ArrayList<HashMap<String, String>> courseList = new ArrayList<HashMap<String, String>>();

        ListView list;

        public TeacherPageCourseBT(Activity activity){
            this.activity = activity;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String teacher_page_url = "http://www.cc.puv.fi/~e1100617/AssignmentTrackingApp/teacherPage.php";
            teacher_number = params[0];


            try {

                URL url = new URL(teacher_page_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(teacher_number, "UTF-8");

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

                    course.setId(j.getString("id"));
                    course.setName(j.getString("name"));

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_COURSENAME, course.getName());
                    map.put(TAG_COURSEID, course.getId());

                    courseList.add(map);
                    list=(ListView) findViewById(R.id.teacherListView);

                    ListAdapter adapter = new SimpleAdapter(TeacherPage.this, courseList,
                            R.layout.teacherpage_list_item,
                            new String[] {TAG_COURSENAME}, new int[] {R.id.teacherCourseName}){


                        public View getView(final int position, View convertView, ViewGroup parent){

                            final String courseName = courseList.get(position).get(TAG_COURSENAME);
                            final String courseId = courseList.get(position).get(TAG_COURSEID);

                            // get filled view from SimpleAdapter
                            View itemView = super.getView(position, convertView, parent);

                            // find text view and two button on each row
                            Button courseAssignment = (Button) itemView.findViewById(R.id.course_assignments_button);
                            Button studentAssignment = (Button) itemView.findViewById(R.id.course_students_button);
                            TextView markAssignment = (TextView) itemView.findViewById(R.id.teacherCourseName);

                            // add an onClickListener to when course name is clicked to show mark assignment page
                            //in order to make student assignment for the course
                            markAssignment.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent("com.example.feranmi.assignmenttrackingapp.AssignmentMarkingPage");
                                    Bundle extras = new Bundle();

                                    extras.putString("courseName", courseName);
                                    extras.putString("courseId", courseId);
                                    extras.putString("teacherID",teacherID);

                                    intent.putExtras(extras);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    activity.startActivity(intent);

                                }
                            });

                            // add an onClickListener to when course assignments button is clicked
                            courseAssignment.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent("com.example.feranmi.assignmenttrackingapp.CourseAssignmentsPage");
                                    Bundle extras = new Bundle();

                                    extras.putString("courseName", courseName);
                                    extras.putString("courseId", courseId);
                                    extras.putString("teacherID",teacherID);

                                    intent.putExtras(extras);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    activity.startActivity(intent);
                                }
                            });

                            // add an onClickListener to when course students button is clicked
                            studentAssignment.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent("com.example.feranmi.assignmenttrackingapp.CourseStudentsPage");
                                    Bundle extras = new Bundle();

                                    extras.putString("courseName", courseName);
                                    extras.putString("courseId", courseId);
                                    extras.putString("teacherID",teacherID);

                                    intent.putExtras(extras);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    activity.startActivity(intent);
                                }
                            });

                            return itemView;
                        }
                    };

                    list.setAdapter(adapter);

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
