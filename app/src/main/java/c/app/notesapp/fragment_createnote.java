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
import androidx.fragment.app.FragmentManager;

public class fragment_createnote extends Fragment {
    private EditText editTexttitle;
    private EditText editTextDesc;
    private onNewNoteCreatedListener listener;

    public interface onNewNoteCreatedListener{
        void sendNoteFromCreateNote(Note newNote);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.createnote_layout,container,false);

        editTexttitle = v.findViewById(R.id.edittxt_title);
        editTextDesc = v.findViewById(R.id.edittxt_description);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Add New Note");

        setHasOptionsMenu(true);        //tells the system this fragment uses the OptionsMenu

        return v;
    }

    private void saveNote()
    {
        String title = editTexttitle.getText().toString();
        String desc = editTextDesc.getText().toString();

        if(title.trim().isEmpty() || desc.trim().isEmpty())     //dont save the note if it is empty (trim removes empty spaces)
        {
            Toast.makeText(getContext(), "Cannot add an empty note!", Toast.LENGTH_SHORT).show();
            return;
        }
        //send note to shownotes to be saved in db
        Note newNote = new Note(title,desc);

        listener.sendNoteFromCreateNote(newNote);

        //set edittext items back to empty once note has been saved
        editTexttitle.setText("");
        editTextDesc.setText("");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof onNewNoteCreatedListener)
        {
            listener = (onNewNoteCreatedListener) context;
        }
        else        //throws an exception if the attached fragment does not implement the shownotes_listener
        {
            throw new RuntimeException(context.toString() + " must implement shownotes_listener!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.createnote_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_note:
                saveNote();         //if the save button is clicked, then saveNote() is called
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStackImmediate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
