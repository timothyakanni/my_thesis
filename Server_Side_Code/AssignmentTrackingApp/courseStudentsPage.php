<?php

	require "connect.php"; 

	$courseStudents = array();  
		   
		//$course_id = "oop";
		$course_id = $_POST["course_id"];	
		   
		$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
		   
		$resultStudent = mysql_query("SELECT Student_student_No, Course_id, (SELECT firstName FROM Student WHERE student_No = Student_student_No) AS firstName, 
									(SELECT lastName FROM Student WHERE student_No = Student_student_No) AS lastName FROM Enrollment WHERE Course_id = '".$course_id."' ");
								
		   			//This we be used for submitted assignment part in different php file that will required student number and course_id
					
					/* $resultAssignment = mysql_query("SELECT Student_student_No, Assignment_id, (SELECT description FROM Assignment WHERE id = Assignment_id) AS description 
								FROM SubmittedAssignment WHERE Course_id = '".$course_id."' ");	
								
								while($row = mysql_fetch_array($resultAssignment)){
					
										array_push($courseAssignments,
					
										array('description' => $row[2]					
						
										)			
						 
									);
								} */
		   
				while($row = mysql_fetch_array($resultStudent)){
					
					array_push($courseStudents,
					
						array('Student_student_No' => $row[0], 'Course_id' => $row[1],
						'firstName' => $row[2], 'lastName' => $row[3]			
						
						)				
						 
					);
				}
				
			echo json_encode(array("courseStudents" => $courseStudents));
	   
			mysql_close($conn);
			
?>