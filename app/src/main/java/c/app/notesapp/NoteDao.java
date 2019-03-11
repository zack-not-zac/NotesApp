package c.app.notesapp;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update (Note note);

    @Delete
    void delete (Note note);

    @Query("DELETE FROM note_table")
    void delete_all();

    @Query("SELECT * FROM note_table ORDER BY id DESC") //will sort the database from newest to oldest
    LiveData<List<Note>> viewAllNotes();                //using a LiveData List allows the application to be aware of when data in the list changes and auto-update the GUI
}
