package c.app.notesapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class fragment_shownotes extends Fragment {
    private NoteViewModel noteVM;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.shownotes_layout,container,false);

        RecyclerView recyclerView = v.findViewById(R.id.note_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        //recyclerView.setHasFixedSize(true);

        final ListAdapter adapter = new ListAdapter();
        recyclerView.setAdapter(adapter);

        noteVM = ViewModelProviders.of(this).get(NoteViewModel.class);          //declares the application viewmodel

        noteVM.getAllNotes().observe(this, new Observer<List<Note>>() {         //tells the application what data to display and observe it for changes
            @Override
            public void onChanged(List<Note> notes) {                //this is updated when the data in the notes LiveData object changes
                //update the GUI when data changes
                adapter.setNotes(notes);
            }
        });

        return v;
    }
}
