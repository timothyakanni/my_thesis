<?php

require "connect.php";
 
$teacherInfo = array(); 

function check_input($data)
{
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    
    return $data;
}
        
    $teacher_username = $_POST["username"];
    $password = $_POST["password"]; 	
    
       
	$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
    
    $result= mysql_query("SELECT * FROM Teacher WHERE userName='".$teacher_username."' AND password= '".$password."' ");
    
    
    while($row = mysql_fetch_array($result)){
		   
		array_push($teacherInfo, 
			
			array('userName' => $row[0], 'firstName' => $row[1], 'lastName' => $row[2], 'password' => $row[3]
				
			)
		);
		}  	   
				
		 
	    echo json_encode(array("teacherInfo" => $teacherInfo));	    
	   
		mysql_close($conn);


?>