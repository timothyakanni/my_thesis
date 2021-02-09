<?php

	require "connect.php"; 

	$submittedAssignment = array();  

/* 	for($i=0; $i< count($teacherCourses); $i++){
		   
		   $course_id = $teacherCourses[$i]['id']; */
		   
		   //$course_id = "oop";
		   //$student_number = "e1000";
		   $course_id = $_POST["course_id"];
		   $student_number = $_POST["student_number"];
		   
		   $selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
		   
		   $resultAssignment = mysql_query("SELECT Assignment_id, (SELECT description FROM Assignment WHERE id = Assignment_id) AS description
										FROM SubmittedAssignment WHERE Student_student_No = '".$student_number."' AND Course_id = '".$course_id."' ");		   
			
		   
				while($row = mysql_fetch_array($resultAssignment)){
					array_push($submittedAssignment,
					
						array('Assignment_id' => $row[0],'description' => $row[1]
						)				
						 
					);
				}
				
			echo json_encode(array("submittedAssignment" => $submittedAssignment));
	   
			mysql_close($conn);
			
?>