package c.app.notesapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class fragment_shownotes extends Fragment {
    private NoteViewModel noteVM;           //variable for the viewmodel of the app
    private EditNoteListener listener;
    private ListAdapter adapter;

    public interface EditNoteListener{
        void editNoteFromNotes(Note editedNote);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.shownotes_layout,container,false);

        RecyclerView recyclerView;      //variable for the recyclerView used to list the notes
        adapter = new ListAdapter();

        recyclerView = v.findViewById(R.id.note_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);

        noteVM = ViewModelProviders.of(this).get(NoteViewModel.class);

        //tells the application what data to display and observe it for changes - getActivity is called as data on which note was selected will have to be passed between fragments
        noteVM.getAllNotes().observe(getActivity(), new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {                //this is updated when the data in the notes LiveData object changes
                //update the GUI when data changes
                adapter.setNotes(notes);
            }
        });

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Notes");

        adapter.setOnItemClickedListener(new ListAdapter.onItemClickedListener() {
            @Override
            public void onItemClick(Note note) {
                listener.editNoteFromNotes(note);
            }
        });

        return v;
    }

    public void createNote(final Note newNote){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {        //this creates a small delay between the fragment call and the data being inputted
            public void run() {                     //as before the data was being passed and set before the fragment fully initialised
                noteVM.insert(newNote);     //db will just replace note if it exists, meaning that the app doesn't need to know if it exists already
                Toast.makeText(getContext(), "Note Saved", Toast.LENGTH_SHORT).show();      //takes the note from the createnote fragment and saves it to the db
            }
        }, 100);   //50ms delay to allow the fragment to fully initialise before running the code
    }

    public void deleteNote(final Note note){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {        //this creates a small delay between the fragment call and the data being inputted
            public void run() {                     //as before the data was being passed and set before the fragment fully initialised
                noteVM.delete(note);
                Toast.makeText(getContext(), "Note Deleted", Toast.LENGTH_SHORT).show();
                adapter.removeItem(note);
            }
        }, 50);   //50ms delay to allow the fragment to fully initialise before running the code
    }

    //for fragment communication
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof EditNoteListener)
        {
            listener = (EditNoteListener) context;
        }
        else        //throws an exception if the attached fragment does not implement the EditNoteListener
        {
            throw new RuntimeException(context.toString() + " must implement EditNoteListener!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
