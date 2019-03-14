package c.app.notesapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class fragment_createnote extends Fragment {
    private EditText editTextTitle;
    private EditText editTextDesc;
    private onNewNoteCreatedListener listener;
    private View v;
    private MenuItem btn_delete;
    private int id = -1;

    public interface onNewNoteCreatedListener{
        void sendNoteFromCreateNote(Note newNote, boolean deleteNote);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.createnote_layout,container,false);

        editTextTitle = v.findViewById(R.id.edittxt_title);
        editTextDesc = v.findViewById(R.id.edittxt_description);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Add New Note");

        setHasOptionsMenu(true);        //tells the system this fragment uses the OptionsMenu

        return v;
    }

    private void saveNote()
    {
        String title = editTextTitle.getText().toString();
        String desc = editTextDesc.getText().toString();

        if(title.trim().isEmpty() || desc.trim().isEmpty())     //dont save the note if it is empty (trim removes empty spaces)
        {
            Toast.makeText(getContext(), "Cannot add an empty note!", Toast.LENGTH_SHORT).show();
            return;
        }
        //send note to shownotes to be saved in db
        Note newNote = new Note(title,desc);

        if(id != -1)
        {
            newNote.setId(id);      //sets the id if a note was passed into the editNote function
        }

        listener.sendNoteFromCreateNote(newNote, false);

        //set edittext items back to empty once note has been saved
        editTextTitle.setText("");
        editTextDesc.setText("");
    }

    public void editNote(Note note)
    {   //inserts the note data into the text views. The app then just calls saveNote as normal, then the DAO replaces the object if a conflict exists
        //if it does exist, then the note will appear edited but it is actually just replaced. Otherwise, it will be created.
        String title = note.getTitle();
        String desc = note.getDescription();
        id = note.getId();

        btn_delete.setVisible(true);        //shows the delete button if a note is being edited

        editTextTitle.setText(title);
        editTextDesc.setText(desc);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Edit Note");
    }

    private void deleteNote()
    {
        String title = editTextTitle.getText().toString();
        String desc = editTextDesc.getText().toString();

        Note note = new Note(title,desc);

        note.setId(id);

        listener.sendNoteFromCreateNote(note, true);

        //set edittext items back to empty once note has been deleted
        editTextTitle.setText("");
        editTextDesc.setText("");
    }

    //for fragment communication
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof onNewNoteCreatedListener)
        {
            listener = (onNewNoteCreatedListener) context;
        }
        else        //throws an exception if the attached fragment does not implement the onNewNoteCreatedListener
        {
            throw new RuntimeException(context.toString() + " must implement onNewNoteCreatedListener!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    //for options menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.createnote_menu, menu);
        btn_delete = menu.findItem(R.id.delete_note_btn).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_note_btn:
                saveNote();         //if the save button is clicked, then saveNote() is called
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                return true;
            case R.id.delete_note_btn:
                deleteNote();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
