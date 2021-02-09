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
        
    $student_username = $_POST["username"];
    $password = $_POST["password"];        
       
	$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
    
    $result= mysql_query("SELECT * FROM Student WHERE student_No='".$student_username."' AND passord= '".$password."' ");
    
    
       /*  if(mysql_num_rows($result) > 0)
        {
            echo "Login Success";
        }
        else
        {
            echo "Login not success ";
               
        }     */

    while($row = mysql_fetch_array($result)){
		   
		array_push($studentInfo, 
			
			array('student_No' => $row[0], 'firstName' => $row[1], 'lastName' => $row[2], 'passord' => $row[3],
				'submissionRate' => $row[4], 'Teacher_userName' => $row[5]
			)
		);
		}  	   
				
		 
	    echo json_encode(array("studentInfo" => $studentInfo));	    
	   
		mysql_close($conn);


?>