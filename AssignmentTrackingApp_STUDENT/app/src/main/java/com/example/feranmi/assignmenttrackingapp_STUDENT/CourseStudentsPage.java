package com.example.feranmi.assignmenttrackingapp_STUDENT;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import project.dto.Student;

public class CourseStudentsPage extends AppCompatActivity {

    static JSONObject jObj = null;
    static String json = "";

    TextView selectedCourseName;

    String courseName;
    String courseId;
    String teacherID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_students_page);

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        courseName = extras.getString("courseName");
        courseId = extras.getString("courseId");
        teacherID = extras.getString("teacherID");

        selectedCourseName = (TextView) findViewById(R.id.selectedCourseName);
        selectedCourseName.setText(courseName);

        CourseStudentsBT courseStudentsBT = new CourseStudentsBT(this);
        courseStudentsBT.execute(courseId);

    }

    private class CourseStudentsBT extends AsyncTask<String, Void, JSONObject> {

        Activity activity;
        AlertDialog alertDialog;

        JSONArray courseStudents = null;

        String course_id;
        Student student = new Student();

        private static final String TAG_STUDENTID = "Student_student_No";
        private static final String TAG_STUDENTNAME = "";


        ArrayList<HashMap<String, String>> studentList = new ArrayList<HashMap<String, String>>();
        ListView list;


        public CourseStudentsBT(Activity activity){

            this.activity = activity;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String course_students_url = "http://www.cc.puv.fi/~e1100617/AssignmentTrackingApp/courseStudentsPage.php";
            course_id = params[0];

            try {

                URL url = new URL(course_students_url);
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


                courseStudents = json.getJSONArray("courseStudents");
                for (int i = 0; i < courseStudents.length(); i++) {

                    JSONObject j = courseStudents.getJSONObject(i);

                    student.setStudentNumber(j.optString("Student_student_No"));
                    student.setFirstName(j.optString("firstName"));
                    student.setLastName(j.optString("lastName"));

                    String studentName =  student.getFirstName() + " " + student.getLastName();

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_STUDENTID, student.getStudentNumber());
                    map.put(TAG_STUDENTNAME, studentName);

                    studentList.add(map);

                    list=(ListView) findViewById(R.id.courseStudentsListView);

                    ListAdapter adapter = new SimpleAdapter(CourseStudentsPage.this, studentList,
                            R.layout.teachercourse_studentspage_list_item,
                            new String[] {TAG_STUDENTID,TAG_STUDENTNAME},
                            new int[] {R.id.studentId,
                                    R.id.studentName});

                    list.setAdapter(adapter);

                    //Setting click listener for each item in Course List View
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            String studentId;
                            studentId = studentList.get(+position).get(TAG_STUDENTID);

                            Intent intent = new Intent("com.example.feranmi.assignmenttrackingapp.StudentAssignmentPage");
                            Bundle extras = new Bundle();

                            extras.putString("studentNumber", studentId);
                            extras.putString("courseId", courseId);
                            extras.putString("courseName", courseName);

                            intent.putExtras(extras);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        }
                    });

                }



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
