<?php

require "connect.php"; 

function check_input($data)
{
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    
    return $data;
}
         

	$allCourses = array();	
	      
	$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
    
    $resultCourse= mysql_query("SELECT * FROM Course");	    
	
    
		while($row = mysql_fetch_array($resultCourse)){
		   
		array_push($allCourses, 
			
			array('id' => $row[0], 'name' => $row[1], 'description' => $row[2], 'startDate' => $row[3],
				'endDate' => $row[4], 'password' => $row[5], 'Teacher_userName' => $row[6]
			)
		);
		}  	   
		
		
		 
	    echo json_encode(array("allCourses" => $allCourses));	    
	   
		mysql_close($conn);

?>