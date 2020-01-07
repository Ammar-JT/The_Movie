package com.example.themovie;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.themovie.database.AppDatabase;
import com.example.themovie.database.Favorite;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<Favorite>> favorites;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the favorite movies from the DataBase");
        favorites = database.favoriteDao().loadAllFavorites();
    }

    public LiveData<List<Favorite>> getFavorites() {
        return favorites;
    }
}
