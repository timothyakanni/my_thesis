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
	//$assignment_id = "oop_A2"; 
	//$assignment_description = "Assignment 2";
	
           
	$selectDatabase = mysql_select_db("e1100617_AssignmentTrackingApp",$conn);
    
    $sql_query= mysql_query("SELECT * FROM Assignment WHERE id = '".$assignment_id."'  AND description =
								'".$assignment_description."' ");
								
        if(mysql_num_rows($sql_query) > 0)
		{
			$sql_result= mysql_query("DELETE FROM Assignment WHERE id = '".$assignment_id."'  AND description =
								'".$assignment_description."' ");
			if($sql_result)
			{
            echo "Assignment Delete Successfull";
			}
			else
			{
			 echo "Error1";
			}
		}
       
        else
        {
            echo "Error";              
        }    
		mysql_close($conn);    

		
		
		

?>