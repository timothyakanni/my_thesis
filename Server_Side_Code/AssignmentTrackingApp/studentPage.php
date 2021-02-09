<?php

	require "connect.php"; 

	$studentCourses = array();  

/* 	for($i=0; $i< count($teacherCourses); $i++){
		   
		   $course_id = $teacherCourses[$i]['id']; */
		   
		   //$student_number = "e1000";
		   $student_number = $_POST["student_number"];
		   
		   $selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
		   
		   $resultCourse = mysql_query("SELECT Course_id, Student_student_No, (SELECT id FROM Course WHERE id = Course_id) AS id, 
								(SELECT name FROM Course WHERE id = Course_id) AS name FROM Enrollment WHERE Student_student_No = '".$student_number."' ");		   
			
		   
				while($row = mysql_fetch_array($resultCourse)){
					array_push($studentCourses,
					
						array('Course_id' => $row[0], 'Student_student_No' => $row[1], 
						'id' => $row[2], 'name' => $row[3]
						)				
						 
					);
				}
				
			echo json_encode(array("studentCourses" => $studentCourses));
	   
			mysql_close($conn);
			
?>