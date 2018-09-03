package com.teamvii.easymovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.teamvii.easymovies.DetailsActivity;
import com.teamvii.easymovies.models.Trailer;
import com.teamvii.easymovies.utilities.NetworkUtils;
import com.teamvii.easymovies.utilities.OpenJsonUtils;

import java.net.URL;
import java.util.List;

public class TrailersLoader extends AsyncTaskLoader<List<Trailer>> {

    private List<Trailer> trailers = null;
    private Bundle args;

    public TrailersLoader(Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (args == null) {
            return;
        }
        if (trailers != null) {
            deliverResult(trailers);
        } else {
            forceLoad();
        }

    }

    @Override
    public List<Trailer> loadInBackground() {
        String trailersPath = args.getString(DetailsActivity.TRAILERS_KEY);
        String movieId = args.getString(DetailsActivity.MOVIE_ID_KEY);

        try {
            URL url = NetworkUtils.buildUrl(movieId, trailersPath);
            String reviewsJsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
            trailers = OpenJsonUtils.getSimpleTrailersData(reviewsJsonResponse);
            return trailers;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trailers;
    }

    @Override
    public void deliverResult(List<Trailer> data) {
        trailers = data;
        super.deliverResult(data);
    }
}
