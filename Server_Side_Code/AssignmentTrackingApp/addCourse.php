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
	$course_name = $_POST["course_name"];
	$course_description = $_POST["course_description"];
    $start_date = $_POST["start_date"];  
	$end_date = $_POST["end_date"]; 	
	$password = $_POST["password"]; 
	$teacher_id = $_POST["teacher_id"]; 	
           
	$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
    
    $sql_query= mysql_query("INSERT INTO Course (id,name,description,startDate,endDate,password,Teacher_userName) 
							VALUES('$course_id','$course_name','$course_description','$start_date','$end_date','$password','$teacher_id')");
        
        if($sql_query)
        {
            echo "Course Adding Successfull";
        }
        else
        {
            echo "Error";            
        }    
		mysql_close($conn);    


?>