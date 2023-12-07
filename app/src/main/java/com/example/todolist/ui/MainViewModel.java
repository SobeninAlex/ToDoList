package com.example.todolist.ui;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todolist.database.Note;
import com.example.todolist.database.NoteDatabase;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    private NoteDatabase noteDatabase;

    private int count = 0;
    private MutableLiveData<Integer> countLD = new MutableLiveData<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<List<Note>> notes = new MutableLiveData<>();


    public MainViewModel(@NonNull Application application) {
        super(application);
        noteDatabase = NoteDatabase.getInstance(application);
    }

    public void showCount() {
        count++;
        countLD.setValue(count);
    }

    public LiveData<Integer> getCount() {
        return countLD;
    }

    public LiveData<List<Note>> getNotes() {
        return notes;
    }

    public void refreshList() {
        var disposable = getNotesRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Note>>() {
                    @Override
                    public void accept(List<Note> notesFromDB) throws Throwable {
                        notes.setValue(notesFromDB);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.d("MainViewModel", "refreshList error");
                    }
                });
        compositeDisposable.add(disposable);
    }

    private Single<List<Note>> getNotesRx() {
        return Single.fromCallable(new Callable<List<Note>>() {
            @Override
            public List<Note> call() throws Exception {
                return noteDatabase.notesDao().getNotes();
//                throw new RuntimeException(); //test
            }
        });
    }

    public void remove(Note note) {
        var disposable = removeRx(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d("MainViewModel", "Drop Note");
                    refreshList();
                }, throwable -> {
                    Log.d("MainViewModel", "remove error");
                });
        compositeDisposable.add(disposable);
    }

    private Completable removeRx(Note note) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Throwable {
                noteDatabase.notesDao().remove(note.getId());
//                throw new RuntimeException(); //test
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
