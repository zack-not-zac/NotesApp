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
    private int priority;                   //note priority

    public Note(String title, int priority) {
        this.title = title;
        this.priority = priority;
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
