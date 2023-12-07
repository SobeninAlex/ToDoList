package com.example.todolist.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.todolist.database.Note;
import com.example.todolist.adapters.NotesAdapter;
import com.example.todolist.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotes;
    private FloatingActionButton buttonAddNote;
    private NotesAdapter adapter;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //создаем viewModel с помощью андроид-провайдера
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.getCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                Toast.makeText(
                        MainActivity.this,
                        "Количество кликов по элементу = " + count,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        initViews();

        adapter = new NotesAdapter();

        adapter.setOnNoteClickListener(new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(Note note) {
                viewModel.showCount();
            }
        });

        recyclerViewNotes.setAdapter(adapter);

        //подписываемся на все изменения в таблице notes
        //каждый раз, когда будут происходить изменения будет вызываться метод onChanged, куда прилетят данные из таблицы
        //и новые данные будут установлены в адаптер
        //возможно подписаться на изменения т.к. метод getNotes возвращает объект класс LiveData
        viewModel.getNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.setNotes(notes);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT
                ) {
                    @Override
                    public boolean onMove(
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            @NonNull RecyclerView.ViewHolder target
                    ) {
                        return false;
                    }

                    @Override
                    public void onSwiped(
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            int direction
                    ) {
                        int position = viewHolder.getAdapterPosition();
                        Note note = adapter.getNotes().get(position);
                        viewModel.remove(note);
                    }
                }
        );
        itemTouchHelper.attachToRecyclerView(recyclerViewNotes);

        buttonAddNote.setOnClickListener(v -> {
            launchNextScreen();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.refreshList();
    }

    private void initViews() {
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        buttonAddNote = findViewById(R.id.buttonAddNote);
    }

    private void launchNextScreen() {
        var intent = CreateNewNoteActivity.newIntent(this);
        startActivity(intent);
    }
}