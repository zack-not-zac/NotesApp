package c.app.notesapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.NoteViewHolder> {
    private List<Note>notes = new ArrayList<>();

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //this indicates which layout to use for each individual item in the recyclerview
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout,parent,false);
        return new NoteViewHolder(v);       //returns the created item
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        //sets the values of the TextViews in the note_layout
        holder.title_textview.setText(note.getTitle());
        holder.desc_textview.setText(note.getDescription());
        holder.priority_textview.setText(String.valueOf(note.getPriority()));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> notes)
    {
        this.notes = notes;
        notifyDataSetChanged();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder{
        private TextView title_textview;
        private TextView desc_textview;
        private TextView priority_textview;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title_textview = itemView.findViewById(R.id.txtTitle);
            desc_textview = itemView.findViewById(R.id.txtDescription);
            priority_textview = itemView.findViewById(R.id.txtPriority);


        }
    }
}