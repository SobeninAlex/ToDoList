package com.example.todolist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.database.Note;
import com.example.todolist.R;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private List<Note> notes = new ArrayList<>();

    private OnNoteClickListener onNoteClickListener;

    public void setOnNoteClickListener(OnNoteClickListener onNoteClickListener) {
        this.onNoteClickListener = onNoteClickListener;
    }

    public List<Note> getNotes() {
        return new ArrayList<>(notes);
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);

        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotesViewHolder viewHolder, int position) {
        Note note = notes.get(position);
        viewHolder.textViewNote.setText(note.getText());

        //получаем id цвета
        int colorResId;
        switch (note.getPriority()) {
            case 0 -> colorResId = android.R.color.holo_green_light;
            case 1 -> colorResId = android.R.color.holo_orange_light;
            default -> colorResId = android.R.color.holo_red_light;
        }

        //получаем цвет по id
        int color = ContextCompat.getColor(viewHolder.itemView.getContext(), colorResId);
        //устанавливаем цвет фона у Text View
        viewHolder.textViewNote.setBackgroundColor(color);

        viewHolder.itemView.setOnClickListener(v -> {
            if (onNoteClickListener != null) {
                onNoteClickListener.onNoteClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


    static class NotesViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNote;
        public NotesViewHolder(@NonNull View itemView)  {
            super(itemView);
            textViewNote = itemView.findViewById(R.id.textViewNote);
        }
    }

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

}
