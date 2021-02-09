package project.dto;

import java.io.Serializable;

/**
 * Created by Feranmi on 7/4/2016.
 */
public class Teacher implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String firstName;
    private String lastName;
    private String userName;
    private String password;

    //This is empty constructor
    public Teacher(){

        //this.id = 0;
        this.firstName = null;
        this.lastName = null;
        this.userName = null;
        this.password = null;

    }

    //This is another non-empty constructor
    public Teacher(String firstName, String lastName,
                   String userName, String password){

        //this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;

    }

    //These are get and set method for the private variables

	/*public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}*/

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

    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
}
