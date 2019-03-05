package c.app.notesapp;

import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainActivity extends AppCompatActivity {
    private NoteViewModel noteVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.note_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final ListAdapter adapter = new ListAdapter();
        recyclerView.setAdapter(adapter);

        noteVM = ViewModelProviders.of(this).get(NoteViewModel.class);          //declares the application viewmodel

        noteVM.getAllNotes().observe(this, new Observer<List<Note>>() {         //tells the application what data to display and observe it for changes
            @Override
            public void onChanged(List<Note> notes) {                                  //this is updated when the data in the notes LiveData object changes
                //update the GUI when data changes
                adapter.setNotes(notes);
            }
        });
    }
}
