<?php

	require "connect.php"; 

	$courseAssignments = array();  
		   
		//$course_id = "jsp";
		$course_id = $_POST["course_id"];	
		   
		$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
		   
		$resultStudent = mysql_query("SELECT id, description FROM Assignment WHERE Course_id = '".$course_id."' ");
								
		   			
		   
				while($row = mysql_fetch_array($resultStudent)){
					
					array_push($courseAssignments,
					
						array('id' => $row[0], 'description' => $row[1]									
						
						)				
						 
					);
				}
				
			echo json_encode(array("courseAssignments" => $courseAssignments));			
	   
			mysql_close($conn);
			
?>