<?php

require "connect.php";

$studentInfo = array(); 

$student_username = $password = "";

function check_input($data)
{
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    
    return $data;
}
        
	$student_id = $_POST["Student_student_No"];  
    $assignment_id = $_POST["assignment_id"];    
	$course_id = $_POST["Course_id"]; 		
		
	//$student_id = "e2000";  
    //$assignment_id = "oop_A2";    
	//$course_id = "oop"; 	
       
	$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);         
        
			
		$sql_query= mysql_query("INSERT INTO SubmittedAssignment (Student_student_No,Assignment_id,Course_id) VALUES('$student_id','$assignment_id','$course_id')");
        
		if($sql_query)
		{
			echo "Marking successfull";
			$sql_query1= mysql_query("SELECT * FROM SubmittedAssignment WHERE Assignment_id = '".$assignment_id."' AND Course_id ='".$course_id."' ");
			$sql_query2= mysql_query("SELECT * FROM Enrollment WHERE Course_id ='".$course_id."' ");
			
			$No_of_submission = mysql_num_rows($sql_query1);
			$No_of_enrollment = mysql_num_rows($sql_query2);
			
			$completion_rate = ($No_of_submission/$No_of_enrollment) * 100;			
			
			$sql_update= mysql_query("UPDATE Assignment SET completionRate = '".$completion_rate."'
									WHERE id = '".$assignment_id."'  AND Course_id = '".$course_id."' ");
									
			
		}
		else
		{
			echo "Error";                
		}            
        	        
	   
		mysql_close($conn);


?>