<?php

require "connect.php"; 

function check_input($data)
{
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    
    return $data;
}
        
    $course_id = $_POST["course_id"];	
	//$course_id = "oop";    

	$courseAssignments = array();	
	      
	$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
    
    $resultCourse= mysql_query("SELECT * FROM Assignment WHERE Course_id = '".$course_id."' ");	
    
	
    
		while($row = mysql_fetch_array($resultCourse)){
		   
		array_push($courseAssignments, 
			
			array('id' => $row[0], 'description' => $row[1], 'startDateTime' => $row[2], 'endDateTime' => $row[3],
				'completionRate' => $row[4], 'Course_id' => $row[5]
			)
		);
		}  	   
		
		
		 
	    echo json_encode(array("courseAssignments" => $courseAssignments));	    
	   
		mysql_close($conn);

?>