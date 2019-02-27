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
    private int priority;                   //note priority

    public Note(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getPriority() {
        return priority;
    }

    public int getId() {
        return id;
    }
}
