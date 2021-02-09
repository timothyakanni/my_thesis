<?php

require "connect.php"; 

function check_input($data)
{
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    
    return $data;
}
        
    $teacher_username = $_POST["username"];	
	//$teacher_username = "jk";    

	$teacherCourses = array();	
	      
	$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
    
    $resultCourse= mysql_query("SELECT * FROM Course WHERE Teacher_userName='".$teacher_username."' ");	
    
	
    
		while($row = mysql_fetch_array($resultCourse)){
		   
		array_push($teacherCourses, 
			
			array('id' => $row[0], 'name' => $row[1], 'description' => $row[2], 'startDate' => $row[3],
				'endDate' => $row[4], 'password' => $row[5], 'Teacher_userName' => $row[6]
			)
		);
		}  	   
		
		
		 
	    echo json_encode(array("teacherCourses" => $teacherCourses));	    
	   
		mysql_close($conn);

?>