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
	$end_date_time = $_POST["end_date_time"]; 		
	$course_id = $_POST["course_id"]; 	
           
	$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
	
	$sql_query= mysql_query("SELECT * FROM Assignment WHERE id = '".$assignment_id."'  AND Course_id =
								'".$course_id."' ");
								
        if(mysql_num_rows($sql_query) > 0)
		{
			$sql_result= mysql_query("UPDATE Assignment SET description = '".$assignment_description."', endDateTime = '".$end_date_time."'
									WHERE id = '".$assignment_id."'  AND Course_id = '".$course_id."' ");
        
			if($sql_result)
			{
            echo "Assignment update Successfull";
			}
			else
			{
            echo "Error";            
			}    
			
		}
		else
			{
            echo "Error";            
			}    
    
		
		mysql_close($conn);    


?>