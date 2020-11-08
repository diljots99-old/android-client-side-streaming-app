package com.android.moviestreamer.ui.movies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.android.moviestreamer.DashboardActivity;
import com.android.moviestreamer.SplashScreenActivity;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Movie implements Parcelable {
     private static final String TAG  = "Movie";

    String original_title, homepage, backdrop_url, release_date, original_language, overview, poster_url, status, tagline, backdrop_path, title, revenue, poster_path, imdb_id, background_image_original, large_cover_image;
    int id, budget, vote_count, runtime;

    double vote_average,popularity;
    ArrayList<Integer> genre_ids;
    boolean adult, video, isPosterEmpty, isBackdropEmpty, streamable,torrent,isComplete=false;
    Uri poster_uri, backdrop_uri;
    List<HashMap> spoken_languages;
    List<String> genres;
    List<String> backdrop_urls = new ArrayList<>();
    List<HashMap> production_countries;
    HashMap belongs_to_collection;
    List<HashMap> production_companies;
    List<HashMap> torrents;
    String sources ="";


    Bitmap poster = null;



    public Movie(JSONObject movie ){

        try {
            this.id = (int) movie.get("id");
            this.imdb_id = (String) movie.get("imdb_id");
            this.original_language = (String) movie.get("original_language");
            this.original_title  = (String) movie.get("original_title");
            this.release_date = (String) movie.get("release_date");
            this.runtime = (int) movie.get("runtime");
            this.status = (String) movie.get("status");
            this.streamable = (boolean) movie.get("streamable");
            this.tagline = (String) movie.get("tagline");
            this.title  = (String) movie.get("title");
            this.adult = (boolean) movie.get("adult");
            this.torrent = (boolean) movie.get("torrent");
            this.overview = (String) movie.get("overview");
            this.vote_average = (double) movie.get("vote_average");
            this.vote_count = (int) movie.get("vote_count");
            this.popularity = (double) movie.get("popularity");
            this.genres = new ArrayList<>();
            this.isComplete =false;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public void loadMovieData(JSONObject movie){

        try {
            this.id = (int) movie.get("id");
            this.imdb_id = (String) movie.get("imdb_id");
            this.original_language = (String) movie.get("original_language");
            this.original_title  = (String) movie.get("original_title");
            this.release_date = (String) movie.get("release_date");
            this.runtime = (int) movie.get("runtime");
            this.status = (String) movie.get("status");
            this.streamable = (boolean) movie.get("streamable");
            this.tagline = (String) movie.get("tagline");
            this.title  = (String) movie.get("title");
            this.adult = (boolean) movie.get("adult");
            this.torrent = (boolean) movie.get("torrent");
            this.overview = (String) movie.get("overview");
            this.vote_average = (double) movie.get("vote_average");
            this.vote_count = (int) movie.get("vote_count");
            this.popularity = (double) movie.get("popularity");
            this.genres = new ArrayList<>();

            JSONArray genresJson = (JSONArray) movie.get("genres");
            JSONArray backdrop_urls_JSON = (JSONArray) movie.get("backdrop_urls");
            JSONObject sourcesJSON = (JSONObject) movie.get("sources");



            for (int index = 0;index<genresJson.length();index++) {
                JSONObject jsonObject = (JSONObject) genresJson.get(index);
                genres.add(jsonObject.getString("name"));
            }

            for (int index = 0;index<backdrop_urls_JSON.length();index++) {
                backdrop_urls.add(backdrop_urls_JSON.get(index).toString());
            }


                sources = sourcesJSON.toString();
            this.isComplete =true;




        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public Bitmap getPoster() {
        return poster;
    }


    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    public String getBackground_image_original() {
        return background_image_original;
    }

    public void setBackground_image_original(String background_image_original) {
        this.background_image_original = background_image_original;
    }

    public String getLarge_cover_image() {
        return large_cover_image;
    }

    public void setLarge_cover_image(String large_cover_image) {
        this.large_cover_image = large_cover_image;
    }

    public boolean isTorrent() {
        return torrent;
    }

    public void setTorrent(boolean torrent) {
        this.torrent = torrent;
    }

    protected Movie(Parcel in) {
        original_title = in.readString();
        homepage = in.readString();
        backdrop_url = in.readString();
        release_date = in.readString();
        original_language = in.readString();
        overview = in.readString();
        poster_url = in.readString();
        status = in.readString();
        tagline = in.readString();
        backdrop_path = in.readString();
        title = in.readString();
        poster_path = in.readString();
        imdb_id = in.readString();
        id = in.readInt();
        budget = in.readInt();
        vote_count = in.readInt();
        runtime = in.readInt();
        revenue = in.readString();
        popularity = in.readDouble();
        vote_average = in.readDouble();
        adult = in.readByte() != 0;
        video = in.readByte() != 0;
        isPosterEmpty = in.readByte() != 0;
        isBackdropEmpty = in.readByte() != 0;
        poster_uri = in.readParcelable(Uri.class.getClassLoader());
        backdrop_uri = in.readParcelable(Uri.class.getClassLoader());
        torrents = in.readArrayList(HashMap.class.getClassLoader());
        spoken_languages = in.readArrayList(HashMap.class.getClassLoader());
        genres = in.readArrayList(String.class.getClassLoader());
        production_countries = in.readArrayList(HashMap.class.getClassLoader());
        production_companies = in.readArrayList(HashMap.class.getClassLoader());
        belongs_to_collection = in.readHashMap(HashMap.class.getClassLoader());
        streamable = in.readByte() != 0;
        background_image_original = in.readString();
        large_cover_image = in.readString();
        torrent = in.readByte() != 0;
        poster = in.readParcelable(Bitmap.class.getClassLoader());
        backdrop_urls = in.readArrayList(String.class.getClassLoader());
        sources = in.readString();
        isComplete =in.readByte() != 0;

    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getBackdrop_url() {
        return backdrop_url;
    }

    public void setBackdrop_url(String backdrop_url) {
        this.backdrop_url = backdrop_url;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public ArrayList<Integer> getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(ArrayList<Integer> genre_ids) {
        this.genre_ids = genre_ids;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public boolean isPosterEmpty() {
        return isPosterEmpty;
    }

    public void setPosterEmpty(boolean posterEmpty) {
        isPosterEmpty = posterEmpty;
    }

    public boolean isBackdropEmpty() {
        return isBackdropEmpty;
    }

    public void setBackdropEmpty(boolean backdropEmpty) {
        isBackdropEmpty = backdropEmpty;
    }

    public Uri getPoster_uri() {
        return poster_uri;
    }

    public void setPoster_uri(Uri poster_uri) {
        this.poster_uri = poster_uri;
    }

    public Uri getBackdrop_uri() {
        return backdrop_uri;
    }

    public void setBackdrop_uri(Uri backdrop_uri) {
        this.backdrop_uri = backdrop_uri;
    }


    public HashMap getBelongs_to_collection() {
        return belongs_to_collection;
    }

    public void setBelongs_to_collection(HashMap belongs_to_collection) {
        this.belongs_to_collection = belongs_to_collection;
    }

    public List<HashMap> getSpoken_languages() {
        return spoken_languages;
    }

    public void setSpoken_languages(List<HashMap> spoken_languages) {
        this.spoken_languages = spoken_languages;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<HashMap> getProduction_countries() {
        return production_countries;
    }

    public void setProduction_countries(List<HashMap> production_countries) {
        this.production_countries = production_countries;
    }

    public List<HashMap> getProduction_companies() {
        return production_companies;
    }

    public void setProduction_companies(List<HashMap> production_companies) {
        this.production_companies = production_companies;
    }

    public List<HashMap> getTorrents() {
        return torrents;
    }

    public void setTorrents(List<HashMap> torrents) {
        this.torrents = torrents;
    }

    public boolean isStreamable() {
        return streamable;
    }

    public void setStreamable(boolean streamable) {
        this.streamable = streamable;
    }

    public List<String> getBackdrop_urls() {
        if (backdrop_urls.size() < 1) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 3000);
        }
        return backdrop_urls;
    }

    public void setBackdrop_urls(List<String> backdrop_urls) {
        this.backdrop_urls = backdrop_urls;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(original_title);
        dest.writeString(homepage);
        dest.writeString(backdrop_url);
        dest.writeString(release_date);
        dest.writeString(original_language);
        dest.writeString(overview);
        dest.writeString(poster_url);
        dest.writeString(status);
        dest.writeString(tagline);
        dest.writeString(backdrop_path);
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeString(imdb_id);
        dest.writeInt(id);
        dest.writeInt(budget);
        dest.writeInt(vote_count);
        dest.writeInt(runtime);
        dest.writeString(revenue);
        dest.writeDouble(popularity);
        dest.writeDouble(vote_average);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeByte((byte) (video ? 1 : 0));
        dest.writeByte((byte) (isPosterEmpty ? 1 : 0));
        dest.writeByte((byte) (isBackdropEmpty ? 1 : 0));
        dest.writeParcelable(poster_uri, flags);
        dest.writeParcelable(backdrop_uri, flags);
        dest.writeList(torrents);
        dest.writeList(spoken_languages);
        dest.writeList(genres);
        dest.writeList(production_countries);
        dest.writeList(production_companies);
        dest.writeMap(belongs_to_collection);
        dest.writeByte((byte) (streamable ? 1 : 0));
        dest.writeString(background_image_original);
        dest.writeString(large_cover_image);
        dest.writeByte((byte) (torrent ? 1 : 0));
        dest.writeParcelable( poster,flags);
        dest.writeList(backdrop_urls);
        dest.writeString(sources);
        dest.writeByte((byte) (isComplete ? 1 : 0));
    }
}
