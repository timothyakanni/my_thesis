<?php

	require "connect.php"; 

	$availableAssignment = array();  

/* 	for($i=0; $i< count($teacherCourses); $i++){
		   
		   $course_id = $teacherCourses[$i]['id']; */
		   
		   //$course_id = "jsp";
		   $course_id = $_POST["course_id"];
		   
		   $selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
		   
		   $resultAssignment = mysql_query("SELECT description, id, Course_id FROM Assignment WHERE Course_id = '".$course_id."' ");		   
			
		   
				while($row = mysql_fetch_array($resultAssignment)){
					array_push($availableAssignment,
					
						array('description' => $row[0],'id' => $row[1], 
						'Course_id' => $row[2]
						)				
						 
					);
				}
				
			echo json_encode(array("availableAssignment" => $availableAssignment));
	   
			mysql_close($conn);
			
?>