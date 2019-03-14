package c.app.notesapp;

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

    //TODO: might be useful to add a date field if the application is to send reminder notifications - do more research

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
}
