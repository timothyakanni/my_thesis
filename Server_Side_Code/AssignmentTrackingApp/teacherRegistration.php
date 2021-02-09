<?php

require "connect.php";

function check_input($data)
{
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    
    return $data;
}
        
    $username = $_POST["userName"];
	$firstname = $_POST["firstName"];
	$lastname = $_POST["lastName"];
    $password = $_POST["password"];      
	     
	$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
    
    $sql_query= mysql_query("INSERT INTO Teacher (userName,firstName,lastName,password) VALUES('$username','$firstname','$lastname','$password')");
        
        if($sql_query)
        {
            echo "Registration Successfull";
        }
        else
        {
            echo "Error: ";              
        }    
		mysql_close($conn);
    


?>