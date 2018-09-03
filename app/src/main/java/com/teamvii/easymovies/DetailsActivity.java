package com.teamvii.easymovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teamii.easymovies.R;
import com.teamvii.easymovies.adapters.ReviewsAdapter;
import com.teamvii.easymovies.adapters.TrailersAdapter;
import com.teamvii.easymovies.data.EasyMoviesContract.FavoriteMovieEntry;
import com.teamvii.easymovies.loaders.ReviewsLoader;
import com.teamvii.easymovies.loaders.TrailersLoader;
import com.teamvii.easymovies.models.Movie;
import com.teamvii.easymovies.models.Review;
import com.teamvii.easymovies.models.Trailer;
import com.teamvii.easymovies.utilities.EasyMoviesUtilities;
import com.teamvii.easymovies.utilities.NetworkUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks,
        TrailersAdapter.TrailerClickHandler {

    public final static String TRAILERS_KEY = "trailers";
    public final static String REVIEWS_KEY = "reviews";
    public final static String MOVIE_ID_KEY = "movieId";
    public final static String TRAILERS = "videos";
    public final static String REVIEWS = "reviews";
    public final static int TRAILERS_LOADER_ID = 13;
    public final static int REVIEWS_LOADER_ID = 14;
    private static Movie movie;
    @BindView(R.id.poster_iv)
    ImageView poster_iv;
    @BindView(R.id.overview_tv)
    TextView overview_tv;
    @BindView(R.id.release_date_tv)
    TextView release_date_tv;
    @BindView(R.id.vote_average_tv)
    TextView vote_average_tv;
    @BindView(R.id.favorites_button)
    FloatingActionButton favoritesButton;
    @BindView(R.id.trailers_rv)
    RecyclerView trailers_rv;
    @BindView(R.id.reviews_rv)
    RecyclerView reviews_rv;

    TrailersAdapter trailersAdapter;
    ReviewsAdapter reviewsAdapter;
    boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        trailersAdapter = new TrailersAdapter(this);
        trailers_rv.setHasFixedSize(true);
        trailers_rv.setAdapter(trailersAdapter);

        reviews_rv.setHasFixedSize(true);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle movieBundle = intent.getBundleExtra(MainActivity.MOVIE_BUNDLE_KEY);
            movie = movieBundle.getParcelable(MainActivity.MOVIE_KEY);
        }

        populateBasicInfo(movie);
        loadTrailers(movie);
        loadReviews(movie);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.MOVIE_KEY, movie);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movie = savedInstanceState.getParcelable(MainActivity.MOVIE_KEY);
        populateBasicInfo(movie);
        loadTrailers(movie);
        loadReviews(movie);
    }

    private void populateBasicInfo(Movie movie) {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        isFavorite = isFavorite(movie);
        String title = movie.getTitle();
        actionBar.setTitle(title);


        if (isFavorite) {
            favoritesButton.setImageResource(R.drawable.ic_favorite_heart);
        } else {
            favoritesButton.setImageResource(R.drawable.ic_unfavorite_heart);
        }

        String voteAverage = String.valueOf(movie.getVoteAverage());
        String posterPath = movie.getPosterPath();
        String overview = movie.getOverview();
        String releaseDate = movie.getReleaseDate();

        String screenWidth = getString(R.string.screen_width_photo);
        String posterUrl = NetworkUtils.postersUrlAuthority + screenWidth + posterPath;

        Picasso.get()
                .load(posterUrl)
                .into(poster_iv);

        vote_average_tv.setText(voteAverage);
        overview_tv.setText(overview);
        release_date_tv.setText(releaseDate);
    }

    private void loadTrailers(Movie movie) {
        String id = String.valueOf(movie.getId());

        Bundle trailersBundle = new Bundle();
        trailersBundle.putString(MOVIE_ID_KEY, id);
        trailersBundle.putString(TRAILERS_KEY, TRAILERS);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Trailer>> loader = loaderManager.getLoader(TRAILERS_LOADER_ID);
        if (loader == null) {
            loaderManager.initLoader(TRAILERS_LOADER_ID, trailersBundle, this);
        } else {
            loaderManager.restartLoader(TRAILERS_LOADER_ID, trailersBundle, this);
        }
    }

    private void loadReviews(Movie movie) {
        String id = String.valueOf(movie.getId());

        Bundle reviewsBundle = new Bundle();
        reviewsBundle.putString(MOVIE_ID_KEY, id);
        reviewsBundle.putString(REVIEWS_KEY, REVIEWS);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Review>> loader = loaderManager.getLoader(REVIEWS_LOADER_ID);
        if (loader == null) {
            loaderManager.initLoader(REVIEWS_LOADER_ID, reviewsBundle, this);
        } else {
            loaderManager.restartLoader(REVIEWS_LOADER_ID, reviewsBundle, this);
        }
    }

    public void favorite(View view) {
        if (isFavorite) {
            String id = String.valueOf(movie.getId());
            Uri uri = FavoriteMovieEntry.CONTENT_URI.buildUpon().appendEncodedPath(id).build();
            getContentResolver().delete(uri, null, null);
            isFavorite = false;
            favoritesButton.setImageResource(R.drawable.ic_unfavorite_heart);
        } else {
            ContentValues values = EasyMoviesUtilities.movieToContentValues(movie);
            getContentResolver().insert(FavoriteMovieEntry.CONTENT_URI, values);
            isFavorite = true;
            favoritesButton.setImageResource(R.drawable.ic_favorite_heart);
        }
    }

    private boolean isFavorite(Movie movie) {
        String id = String.valueOf(movie.getId());
        Uri uri = FavoriteMovieEntry.CONTENT_URI.buildUpon().appendEncodedPath(id).build();
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @NonNull
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case TRAILERS_LOADER_ID:
                return new TrailersLoader(this, args);
            case REVIEWS_LOADER_ID:
                return new ReviewsLoader(this, args);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        switch (loader.getId()) {
            case TRAILERS_LOADER_ID:
                List<Trailer> trailers = (List<Trailer>) data;
                trailersAdapter.setTrailers(trailers);
                break;
            case REVIEWS_LOADER_ID:
                List<Review> reviews = (List<Review>) data;
                if (reviews != null && reviews.size() > 0) {
                    reviewsAdapter = new ReviewsAdapter();
                    reviews_rv.setAdapter(reviewsAdapter);
                    reviewsAdapter.setReviews(reviews);
                } else {
                    reviews_rv.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onClickHandler(Trailer trailer) {
        Uri videoUrl = NetworkUtils.youTubeVideoUrlBuilder(trailer);
        Intent intent = new Intent(Intent.ACTION_VIEW, videoUrl);
        startActivity(intent);
    }
}
