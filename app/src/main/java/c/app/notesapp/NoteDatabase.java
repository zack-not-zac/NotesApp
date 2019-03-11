package c.app.notesapp;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Note.class}, version = 1)
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

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback()
    {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulatedbAsyncTask(instance).execute();        //This will populate the database when it is first initialised
        }
    };

    private static class PopulatedbAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private NoteDao noteDao;

        private PopulatedbAsyncTask(NoteDatabase database)
        {
            noteDao = database.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Title 1", "Desc 1"));
            noteDao.insert(new Note("Title 2", "Desc 2"));
            return null;
        }
    }
}
