<?php

require "connect.php";

function check_input($data)
{
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    
    return $data;
}
        		
    $assignment_id = $_POST["assignment_id"];
	$assignment_description = $_POST["assignment_description"];	
    $start_date_time = $_POST["start_date_time"];  
	$end_date_time = $_POST["end_date_time"]; 	
	$completion_rate = "0"; 
	$course_id = $_POST["course_id"]; 	
           
	$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
    
    $sql_query= mysql_query("INSERT INTO Assignment (id,description,startDateTime,endDateTime,completionRate,Course_id) 
							VALUES('$assignment_id','$assignment_description','$start_date_time','$end_date_time','$completion_rate','$course_id')");
        
        if($sql_query)
        {
            echo "Assignment Adding Successfull";
        }
        else
        {
            echo "Error";            
        }    
		mysql_close($conn);    


?>