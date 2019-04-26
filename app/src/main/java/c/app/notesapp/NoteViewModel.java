package c.app.notesapp;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

class NoteViewModel extends AndroidViewModel {
    private final NoteRepo repo;
    private final LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);

        repo = new NoteRepo(application);
        allNotes = repo.getNotes();
    }

    public void insert(Note note)
    {
        repo.insert(note);
    }

    public void update(Note note)
    {
        repo.update(note);
    }

    public void delete(Note note)
    {
        repo.delete(note);
    }

    public void deleteAll()
    {
        repo.deleteAll();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }
}
