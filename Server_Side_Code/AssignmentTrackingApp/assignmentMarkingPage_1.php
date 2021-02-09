<?php

	require "connect.php"; 

	$courseStudents = array();  
		   
		//$course_id = "jsp";
		$course_id = $_POST["course_id"];	
		   
		$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
		   
		$resultStudent = mysql_query("SELECT Student_student_No, Course_id, (SELECT firstName FROM Student WHERE student_No = Student_student_No) AS firstName, 
									(SELECT lastName FROM Student WHERE student_No = Student_student_No) AS lastName
									FROM Enrollment WHERE Course_id = '".$course_id."' ");
								
		   			
		   
				while($row = mysql_fetch_array($resultStudent)){
					
					array_push($courseStudents,
					
						array('Student_student_No' => $row[0], 'Course_id' => $row[1],					
								'firstName' => $row[2],'lastName' => $row[3]
						)				
						 
					);
				}
				
			echo json_encode(array("courseStudents" => $courseStudents));
			
			
	   
			mysql_close($conn);
			
?>