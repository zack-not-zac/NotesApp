package c.app.notesapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class fragment_shownotes extends Fragment {
    private NoteViewModel noteVM;           //variable for the viewmodel of the app

    //defines a listener so that createnote fragment can send note data back to this fragment to be added to db

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.shownotes_layout,container,false);

        RecyclerView recyclerView;      //variable for the recyclerView used to list the notes

        recyclerView = v.findViewById(R.id.note_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setHasFixedSize(true);

        final ListAdapter adapter = new ListAdapter();
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

        return v;
    }

    public void createNote(Note newNote){
        noteVM.insert(newNote);
        Toast.makeText(getContext(), "Note Saved", Toast.LENGTH_SHORT).show();
    }
}
