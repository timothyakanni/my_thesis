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
        
	$course_password = $_POST["password"];  
    $student_username = $_POST["Student_student_No"];    
	$course_id = $_POST["Course_id"]; 	
	
	//$course_password = "jsp"; 	
       
	$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
    
    $result= mysql_query("SELECT * FROM Course WHERE password='".$course_password."' ");
    
    
        if(mysql_num_rows($result) > 0)
        {
			
			$sql_query= mysql_query("INSERT INTO Enrollment (Student_student_No,Course_id) VALUES('$student_username','$course_id')");
        
			if($sql_query)
			{
				echo "Enrollment success";
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