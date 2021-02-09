<?php

	require "connect.php"; 

	$courseStudents = array();  

/* 	for($i=0; $i< count($teacherCourses); $i++){
		   
		   $course_id = $teacherCourses[$i]['id']; */
		   
		   $course_id = "jsp";
		   
		   $selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
		   
		   $resultStudent = mysql_query("SELECT Student_student_No, SubmissionRate, (SELECT firstName FROM Student WHERE student_No = Student_student_No) AS firstName, 
								(SELECT lastName FROM Student WHERE student_No = Student_student_No) AS lastName FROM Enrollment WHERE Course_id = '".$course_id."' ");
		   
			if(mysql_num_rows($resultStudent) > 0)
				{
					echo "Result from Student Success";
				}
		
			else
				{
					echo "Result from Student not success \n\n";
                
				}     
		   
				while($row = mysql_fetch_array($resultStudent)){
					array_push($courseStudents,
					
						array('Student_student_No' => $row[0], 'SubmissionRate' => $row[1], 
						'firstName' => $row[2], 'lastName' => $row[3]
						)				
						 
					);
				}
				
			echo json_encode(array("courseStudents" => $courseStudents));
	   
			mysql_close($conn);
			
?>