package com.teamvii.easymovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.teamvii.easymovies.DetailsActivity;
import com.teamvii.easymovies.models.Review;
import com.teamvii.easymovies.utilities.NetworkUtils;
import com.teamvii.easymovies.utilities.OpenJsonUtils;

import java.net.URL;
import java.util.List;

public class ReviewsLoader extends AsyncTaskLoader<List<Review>> {

    private List<Review> reviews = null;
    private Bundle args;

    public ReviewsLoader(Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (args == null) {
            return;
        }
        if (reviews != null) {
            deliverResult(reviews);
        } else {
            forceLoad();
        }

    }

    @Override
    public List<Review> loadInBackground() {
        String reviewsPath = args.getString(DetailsActivity.REVIEWS_KEY);
        String movieId = args.getString(DetailsActivity.MOVIE_ID_KEY);

        try {
            URL url = NetworkUtils.buildUrl(movieId, reviewsPath);
            String reviewsJsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
            reviews = OpenJsonUtils.getSimpleReviewsData(reviewsJsonResponse);
            return reviews;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    public void deliverResult(List<Review> data) {
        reviews = data;
        super.deliverResult(data);
    }
}
