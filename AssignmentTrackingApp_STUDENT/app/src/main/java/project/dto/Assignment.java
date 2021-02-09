package project.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Feranmi on 7/4/2016.
 */
public class Assignment implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String description;
    private Date startDateTime;
    private Date endDateTime;
    private Double completionRate;



    //This is empty constructor
    public Assignment(){
        this.id = "not_known";
        this.description = "not_known";
        this.startDateTime = null;
        this.endDateTime = null;
        this.completionRate = 0.0;

    }

    //This is non-empty constructor
    public Assignment(String id, String description, Double completionRate,
                      Date startDateTime, Date endDateTime){

        this.id = id;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.completionRate = completionRate;

    }

    //These are the get and set methods for private attributes

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }
    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }
    public void setEndDateTime(Date endDateTime) {

        this.endDateTime = endDateTime;
    }

    public Double getCompletionRate() {
        return completionRate;
    }
    public void setCompletionRate(Double completionRate) {
        this.completionRate = completionRate;
    }


}
