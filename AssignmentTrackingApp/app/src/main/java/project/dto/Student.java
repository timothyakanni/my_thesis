package project.dto;

import java.io.Serializable;

/**
 * Created by Feranmi on 7/4/2016.
 */
public class Student implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private Double submissionRate;
    //This is empty constructor
    public Student(){


        this.firstName = "not_known";
        this.lastName = "not_known";
        this.userName = "not_known";
        this.password = "not_known";
        this.submissionRate = 0.0;

    }

    //This is another non-empty constructor
    public Student(String firstName, String lastName,
                   String studentNumber, String password, Double submissionRate){


        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = studentNumber;
        this.password = password;
        this.submissionRate = submissionRate;

    }

    //These are get and set method for the private variables


    public String getFirstName(){
        return firstName;
    }
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName(){
        return lastName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getStudentNumber(){
        return userName;
    }
    public void setStudentNumber(String studentNumber){
        this.userName = studentNumber;
    }

    public String getPassword(){ return password;  }
    public void setPassword(String password){
        this.password = password;
    }

    public Double getSubmissionRate(){
        return submissionRate;
    }
    public void setSubmissionRate(Double submissionRate){  this.submissionRate = submissionRate; }

}
