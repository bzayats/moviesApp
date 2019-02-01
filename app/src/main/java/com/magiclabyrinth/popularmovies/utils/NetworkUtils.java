package com.magiclabyrinth.popularmovies.utils;

import android.net.Uri;
import android.util.Base64;

import com.magiclabyrinth.popularmovies.database.MovieReviewEntry;
import com.magiclabyrinth.popularmovies.database.MovieTrailerEntry;
import com.magiclabyrinth.popularmovies.database.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185";
    private static final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
    private static final String API_KEY_ENCODED = "NTk5ZGZlZjM0ZWVlMWJhYTY1MjhkOWNlNDRiZGJjYjA=";
    private static final byte[] data = Base64.decode(API_KEY_ENCODED, Base64.DEFAULT);
    private static final String API_KEY_DECODED = new String(data, StandardCharsets.UTF_8);

    private static final String LANGUAGE = "en-US";
    private static final String SORT_BY_POPULARITY_DESC = "popular";
    private static final String SORT_BY_TOP_RATED = "top_rated";
    private static final String GET_TRAILERS = "videos";
    private static final String GET_REVIEWS = "reviews";

    final static String API_KEY_PARAM = "api_key";
    final static String LANGUAGE_PARAM = "language";
    final static String YOUTUBE_Q_PARAM = "v";

    private static URL createMovieDBURL(String sortType){
        return createMovieDBURL(sortType, null);
    }

    private static URL createMovieDBURL(String sortType, String movieId){
        Uri buildUri = null;

        switch (sortType) {
            case "popularity":
                buildUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon().appendPath(SORT_BY_POPULARITY_DESC).appendQueryParameter(API_KEY_PARAM, API_KEY_DECODED).
                    appendQueryParameter(LANGUAGE_PARAM, LANGUAGE).build();
                break;

            case "vote":
                buildUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon().appendPath(SORT_BY_TOP_RATED).appendQueryParameter(API_KEY_PARAM, API_KEY_DECODED).
                        appendQueryParameter(LANGUAGE_PARAM, LANGUAGE).build();
                break;

            case "trailers":
                if (movieId != null) {
                    buildUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon().appendPath(movieId).appendPath(GET_TRAILERS).appendQueryParameter(API_KEY_PARAM, API_KEY_DECODED).
                            appendQueryParameter(LANGUAGE_PARAM, LANGUAGE).build();
                }
                break;

            case "reviews":
                if (movieId != null) {
                    buildUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon().appendPath(movieId).appendPath(GET_REVIEWS).appendQueryParameter(API_KEY_PARAM, API_KEY_DECODED).
                            appendQueryParameter(LANGUAGE_PARAM, LANGUAGE).build();
                }
                break;


            default:
                    buildUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon().appendPath(SORT_BY_POPULARITY_DESC).appendQueryParameter(API_KEY_PARAM, API_KEY_DECODED).
                            appendQueryParameter(LANGUAGE_PARAM, LANGUAGE).build();
                    break;
        }

        URL url = null;

        try{
            Logger.getLogger(TAG).log(Level.INFO,"URL to execute: " + buildUri.toString());
            url = new URL(buildUri.toString());
        } catch (MalformedURLException mue){
            Logger.getLogger(TAG).log(Level.SEVERE, null, mue);
        }

        return url;
    }

    private static URL createYouTubeURL(String key){
        Uri buildUri;

        buildUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon().appendQueryParameter(YOUTUBE_Q_PARAM, key).build();

        URL url = null;

        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException mue) {
            Logger.getLogger(TAG).log(Level.SEVERE, null, mue);
        }

        return url;
    }

    private static String getJSON(URL url) throws IOException {
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
        int timeout = 30000;
        String resultJSON = null;

        try {
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setRequestProperty("Content-length", "0");
            httpsURLConnection.setUseCaches(false);
            httpsURLConnection.setAllowUserInteraction(false);
            httpsURLConnection.setConnectTimeout(timeout);
            httpsURLConnection.setReadTimeout(timeout);
            httpsURLConnection.connect();
            int status = httpsURLConnection.getResponseCode();

            System.out.println("HTTP response status: " + status);

            switch (status) {
                case 200:
                case 201:
                    InputStream in = httpsURLConnection.getInputStream();
                    Scanner scanner = new Scanner(in);
                    scanner.useDelimiter("\\A");

                    boolean hasInput = scanner.hasNext();

                    if (hasInput) {
                        resultJSON = scanner.next();
                    }
            }
        } finally {
            httpsURLConnection.disconnect();
        }

        System.out.print("JSON: " + resultJSON);

        return resultJSON;
    }

    public static List<MovieEntry> getAllMovieDetails(String sortType){
        String movieDetailsJSON = getMovieDetailsJSON(sortType).toString();
        return createPopularMovies(movieDetailsJSON, sortType);
    }

    public static List<MovieReviewEntry> getAllMovieReviews(String movieId){
        String movieReviewsJSON = getMovieReviewsJSON(movieId);
        return createMovieReviews(movieReviewsJSON);
    }

    public static List<MovieTrailerEntry> getAllMovieTrailers(String movieId){
        String movieDetailsJSON = getMovieTrailersJSON(movieId);
        return createMovieTrailers(movieDetailsJSON);
    }

    private static List<MovieEntry> createPopularMovies(String resultsJson, String movieType){
        List<MovieEntry> popularMovies = new LinkedList<>();
        MovieEntry popularMovie;
        try {
            JSONObject object = new JSONObject(resultsJson);
            JSONArray results = object.getJSONArray("results");

            for (int i = 0; i < results.length(); ++i){
                popularMovie = new MovieEntry();
                popularMovie.setMovieId(results.getJSONObject(i).optString("id"));
                popularMovie.setMovieTitle(results.getJSONObject(i).optString("title"));
                popularMovie.setMovieThumbnailImage(IMAGE_BASE_URL.concat(IMAGE_SIZE).concat(results
                        .getJSONObject(i).optString("backdrop_path")));
                popularMovie.setMoviePosterImage(IMAGE_BASE_URL.concat(IMAGE_SIZE).concat(results
                        .getJSONObject(i).optString("poster_path")));
                popularMovie.setMovieRating(results.getJSONObject(i).optString("vote_average"));
                popularMovie.setMoviePlotSynopsis(results.getJSONObject(i).optString("overview"));
                popularMovie.setMovieReleaseDate(results.getJSONObject(i).optString("release_date"));

                if (movieType.contains("vote")){
                    popularMovie.setMovieType("vote");
                } else {
                    popularMovie.setMovieType("popularity");
                }
                popularMovie.setFavorite(0);
                popularMovies.add(popularMovie);
            }
        } catch (JSONException je){
            Logger.getLogger(TAG).log(Level.SEVERE, null, je);
        }

        return popularMovies;
    }

    private static List<MovieReviewEntry> createMovieReviews(String resultsJson){
        List<MovieReviewEntry> movieReviews = new LinkedList<>();
        MovieReviewEntry movieReview;
        try {
            JSONObject object = new JSONObject(resultsJson);
            String movieId = object.optString("id");
            JSONArray results = object.getJSONArray("results");

            for (int i = 0; i < results.length(); ++i){
                movieReview = new MovieReviewEntry();
                movieReview.setMovieId(movieId);
                movieReview.setAuthor(results.getJSONObject(i).optString("author"));
                movieReview.setContent(results.getJSONObject(i).optString("content"));
                movieReviews.add(movieReview);
            }
        } catch (JSONException je){
            Logger.getLogger(TAG).log(Level.SEVERE, null, je);
        }

        return movieReviews;
    }

    private static List<MovieTrailerEntry> createMovieTrailers(String resultsJson){
        List<MovieTrailerEntry> movieTrailers = new LinkedList<>();
        MovieTrailerEntry movieTrailer;
        try {
            JSONObject object = new JSONObject(resultsJson);
            String movieId = object.optString("id");
            JSONArray results = object.getJSONArray("results");

            for (int i = 0; i < results.length(); ++i){
                movieTrailer = new MovieTrailerEntry();
                movieTrailer.setMovieId(movieId);
                movieTrailer.setTrailerTitle(results.getJSONObject(i).optString("name"));
                movieTrailer.setTrailerUrl(createYouTubeURL(results.getJSONObject(i).optString("key")).toString());
                movieTrailers.add(movieTrailer);
            }
        } catch (JSONException je){
            Logger.getLogger(TAG).log(Level.SEVERE, null, je);
        }

        return movieTrailers;
    }

    private static String getMovieDetailsJSON(String sortType){
        String movieDetailsJSON = null;
        try {
            movieDetailsJSON = getJSON(createMovieDBURL(sortType));
            Logger.getLogger(TAG).log(Level.INFO, "JSON response: ", movieDetailsJSON.toString());
        } catch (IOException ioe){
            Logger.getLogger(TAG).log(Level.SEVERE, null, ioe);
        }

        return movieDetailsJSON;
    }

    private static String getMovieReviewsJSON(String popularMovieId){
        String movieReviewsJSON = null;
        try {
            movieReviewsJSON = getJSON(createMovieDBURL("reviews", popularMovieId));
            Logger.getLogger(TAG).log(Level.INFO, "JSON response for reviews: ", movieReviewsJSON.toString());
        } catch (IOException ioe){
            Logger.getLogger(TAG).log(Level.SEVERE, null, ioe);
        }

        return movieReviewsJSON;
    }

    private static String getMovieTrailersJSON(String popularMovieId){
        String movieTrailersJSON = null;
        Logger.getLogger(TAG).log(Level.INFO,"MovieId is: " + popularMovieId);
        try {
            movieTrailersJSON = getJSON(createMovieDBURL("trailers", popularMovieId));
            Logger.getLogger(TAG).log(Level.INFO, "JSON response for trailers: ", movieTrailersJSON.toString());
        } catch (IOException ioe){
            Logger.getLogger(TAG).log(Level.SEVERE, null, ioe);
        }

        return movieTrailersJSON;
    }
}
