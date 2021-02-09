package project.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Feranmi on 7/4/2016.
 */
public class Course implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private String password;

    //This is empty constructor
    public Course(){
        this.id = "not_known";
        this.name = "not_known";
        this.description = "not_known";
        this.startDate = null;
        this.endDate = null;
        this.password = "not_known";

    }

    //This is non-empty constructor
    public Course(String id, String name, String description,
                  Date startDate, Date endDate, String password){

        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.password = password;

    }

    //These are the get and set methods for private attributes
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public Date getStartDate(){
        return startDate;
    }
    public void setStartDate(Date startDate){
        this.startDate = startDate;
    }

    public Date getEndDate(){
        return endDate;
    }
    public void setEndDate(Date endDate){
        this.endDate = endDate;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password  = password;
    }
}
