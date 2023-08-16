package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class CreateNewNoteActivity extends AppCompatActivity {

    private EditText editTextNote;

    private RadioButton radioButtonLow;
    private RadioButton radioButtonMedium;

    private Button buttonSave;

//    private NoteDatabase noteDatabase;

    private CreateNewNoteViewModel viewModel;

//    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_note);

        viewModel = new ViewModelProvider(this).get(CreateNewNoteViewModel.class);

//        noteDatabase = NoteDatabase.getInstance(getApplication());

        viewModel.getShouldCloseScreen().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean shouldClose) {
                if (shouldClose) {
                    finish();
                }
            }
        });

        initViews();

        buttonSave.setOnClickListener(v -> {
            saveNote();
        });

    }

    private void saveNote() {
        var text = editTextNote.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(
                    this,
                    R.string.error_field_empty,
                    Toast.LENGTH_SHORT
            ).show();
        }
        else {
            int priority = getPriority();
            Note note = new Note(text, priority);

//            new Thread(() -> {
//                noteDatabase.notesDao().add(note);
//
//                handler.post(() -> {
//                    finish(); // закрывает текущую активити
//                });
//
//            }).start();

            viewModel.add(note);
        }

    }

    private int getPriority() {
        int priority;
        if (radioButtonLow.isChecked()) {
            priority = 0;
        }
        else if (radioButtonMedium.isChecked()) {
            priority = 1;
        }
        else {
            priority = 2;
        }
        return priority;
    }

    private void initViews() {
        editTextNote = findViewById(R.id.editTextNote);
        radioButtonLow = findViewById(R.id.radioButtonLow);
        radioButtonMedium = findViewById(R.id.radioButtonMedium);
        buttonSave = findViewById(R.id.buttonSave);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, CreateNewNoteActivity.class);
    }

}