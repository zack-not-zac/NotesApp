package c.app.notesapp;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;

public class NoteRepo {
    private NoteDao noteDao;
    private LiveData<List<Note>> notes;

    public NoteRepo (Application app)
    {
        NoteDatabase database = NoteDatabase.getInstance(app);
        noteDao = database.noteDao();
        notes = noteDao.viewAllNotes();
    }

    //These are the functions visible to the app to create an abstraction layer
    public void insert (Note note)
    {
        new InsertNoteAsyncTask(noteDao).execute(note);
    }

    public void update (Note note)
    {
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    public void delete (Note note)
    {
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public void deleteAll (Note note)
    {
        new DeleteAllAsyncTask(noteDao).execute();
    }

    public LiveData<List<Note>> getNotes() {
        return notes;
    }

    //ASynchronous tasks must be used to use multi-threading and create a mutex so that multiple threads don't update the table at once
    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void>
    {
        private NoteDao noteDao;

        private InsertNoteAsyncTask (NoteDao noteDao){this.noteDao = noteDao;}

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void>
    {
        private NoteDao noteDao;

        private UpdateNoteAsyncTask (NoteDao noteDao){this.noteDao = noteDao;}

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void>
    {
        private NoteDao noteDao;

        private DeleteNoteAsyncTask (NoteDao noteDao){this.noteDao = noteDao;}

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

    private static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private NoteDao noteDao;

        private DeleteAllAsyncTask (NoteDao noteDao){this.noteDao = noteDao;}

        @Override
        protected Void doInBackground(Void... notes) {
            noteDao.delete_all();
            return null;
        }
    }
}
