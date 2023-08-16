package com.example.todolist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface NotesDao {

    //т.к. метод возвращает объект класса LiveData, мы можем подписаться на этот метод в Активити (observe())
    //и при любом изменении в базе будут обновляться данные
    //еще одно преимущество LiveData -> запрос в базу будет автоматически происходить в фоновом потоке
    @Query("SELECT * FROM notes")
    List<Note> getNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(Note note);

    @Query("DELETE FROM notes WHERE id = :id")
    void remove(int id);

}
