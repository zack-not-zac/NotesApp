package c.app.notesapp;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;
    abstract NoteDao noteDao();

    static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null)
        {
            //if no instance of the database exists, this statement will create it.
            //DestructiveMigration means that if the database version was changed, the app will destroy and rebuild the database.
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note_database").fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }

        return instance;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback()
    {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulatedbAsyncTask(instance).execute();        //This will populate the database when it is first initialised
        }
    };

    private static class PopulatedbAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private final NoteDao noteDao;

        private PopulatedbAsyncTask(NoteDatabase database)
        {
            noteDao = database.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Remember This", "Thing to remember"));
            //Location example
            Note note = new Note("Parked Here", null);
            note.setLatitude(56.463066);
            note.setLongitude(-2.973907);
            noteDao.insert(note);
            //multi-line example
            noteDao.insert(new Note("My Life Story: A short novel by the creator of this app.","Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Commodo ullamcorper a lacus vestibulum sed. " +
                    "Erat pellentesque adipiscing commodo elit at imperdiet dui accumsan. Urna condimentum mattis pellentesque id nibh tortor id. " +
                    "Lacus luctus accumsan tortor posuere ac ut consequat semper. Cras semper auctor neque vitae tempus quam pellentesque nec nam. " +
                    "Platea dictumst quisque sagittis purus sit. Pellentesque habitant morbi tristique senectus et netus et. Fames ac turpis egestas " +
                    "integer eget aliquet nibh. Quisque non tellus orci ac auctor. Convallis a cras semper auctor neque vitae. Sit amet tellus cras adipiscing " +
                    "enim eu turpis egestas pretium. Aliquet nibh praesent tristique magna sit amet purus gravida. Eu mi bibendum neque egestas. " +
                    "Quis auctor elit sed vulputate."));
            return null;
        }
    }
}
