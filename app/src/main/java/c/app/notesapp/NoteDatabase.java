package c.app.notesapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;
    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null)
        {
            //if no instance of the database exists, this statement will create it.
            //DestructiveMigration means that if the database version was changed, the app will destroy and rebuild the database.
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note_database").fallbackToDestructiveMigration().build();
        }

        return instance;
    }
}
