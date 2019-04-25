package c.app.notesapp;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey (autoGenerate = true)       //auto generates the PK of the table
    @NonNull                                //ensures the value is never null
    private int id;                         //id of each note in the database

    private String title;                   //note title
    private String description;             //note body
    private double latitude = 0.0;          //location latitude
    private double longitude = 0.0;         //location longitude

    public Note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    //getters and setters for the object
    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    //Location stuff

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLongitude()
    {
        return longitude;
    }
}
