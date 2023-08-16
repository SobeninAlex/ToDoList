package com.example.todolist;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreateNewNoteViewModel extends AndroidViewModel {

    //другой вариант создания переменной БД
    private NotesDao notesDao;

    private MutableLiveData<Boolean> shouldCloseScreen = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CreateNewNoteViewModel(@NonNull Application application) {
        super(application);
        notesDao = NoteDatabase.getInstance(application).notesDao();
    }

    public LiveData<Boolean> getShouldCloseScreen() {
        return shouldCloseScreen;
    }

    public void add(Note note) {
        var disposable = addNoteRx(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Throwable {
                        Log.d("CreateNewNoteViewModel", "subscribe");
                        shouldCloseScreen.setValue(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.d("CreateNewNoteViewModel", "add error");
                    }
                });
        compositeDisposable.add(disposable);

        //setValue() -> только для главного потока
        //postValue() -> для любого потока

        /**У объекта notesDao вызывается метод add() - добавление заметки в БД
         * подписываемся и запускаем метод в работу
         * что бы проделать какое либо действие после завершения работы метода, в метод subscribe()
         * передаем объект анонимного класса Action
         * Теперь после того как метод add() завершит свою работу выполнится метод run()
         *
         * метод add() выполниться в том потоке, который мы указали в качестве параметра
         * в методе subscribeOn(). Если хотим использовать фоновый поток то
         * передаем Schedulers.io() -> input - output
         *
         * .observeOn(AndroidSchedulers.mainThread()) -> переключаем поток обратно на главный, теперь
         * все методы ниже будут происходить в потоке main*/
    }

    private Completable addNoteRx(Note note) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Throwable {
                notesDao.add(note);
//                throw new RuntimeException(); //test
            }
        });
    }

    //данны метод вызывается в момент когда будет уничтожена viewModel
    @Override
    protected void onCleared() {
        super.onCleared();

        //отменяем подписку
        compositeDisposable.dispose();
    }
}
