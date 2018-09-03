package com.teamvii.easymovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.teamvii.easymovies.MainActivity;
import com.teamvii.easymovies.models.Movie;
import com.teamvii.easymovies.utilities.NetworkUtils;
import com.teamvii.easymovies.utilities.OpenJsonUtils;

import java.net.URL;
import java.util.List;

public class MoviesLoader extends AsyncTaskLoader<List<Movie>> {

    private List<Movie> movies = null;
    private Bundle args;

    public MoviesLoader(Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (args == null) {
            return;
        }
        if (movies != null) {
            deliverResult(movies);
        } else {
            forceLoad();
        }

    }

    @Override
    public List<Movie> loadInBackground() {
        String sortType = args.getString(MainActivity.SORT_TYPE_KEY);

        try {
            URL url = NetworkUtils.buildUrl(sortType);
            String moviesJsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
            movies = OpenJsonUtils.getSimpleMoviesData(moviesJsonResponse);
            return movies;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }

    @Override
    public void deliverResult(List<Movie> data) {
        movies = data;
        super.deliverResult(data);
    }
}
