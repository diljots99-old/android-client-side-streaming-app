package com.android.moviestreamer.ui.people;

import android.os.Parcel;
import android.os.Parcelable;

public class people implements Parcelable {
    int id;
    String birthday,known_for_department,death_day,name,gender,imdb_id,homepage,place_of_birth,biography;
    double popularity;
    boolean adult;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getKnown_for_department() {
        return known_for_department;
    }

    public void setKnown_for_department(String known_for_department) {
        this.known_for_department = known_for_department;
    }

    public String getDeath_day() {
        return death_day;
    }

    public void setDeath_day(String death_day) {
        this.death_day = death_day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getPlace_of_birth() {
        return place_of_birth;
    }

    public void setPlace_of_birth(String place_of_birth) {
        this.place_of_birth = place_of_birth;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    protected people(Parcel in) {
        id = in.readInt();
        birthday = in.readString();
        known_for_department = in.readString();
        death_day = in.readString();
        name = in.readString();
        gender = in.readString();
        imdb_id = in.readString();
        homepage = in.readString();
        place_of_birth = in.readString();
        biography = in.readString();
        popularity = in.readDouble();
        adult = in.readByte() != 0;
    }

    public static final Creator<people> CREATOR = new Creator<people>() {
        @Override
        public people createFromParcel(Parcel in) {
            return new people(in);
        }

        @Override
        public people[] newArray(int size) {
            return new people[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(birthday);
        parcel.writeString(known_for_department);
        parcel.writeString(death_day);
        parcel.writeString(name);
        parcel.writeString(gender);
        parcel.writeString(imdb_id);
        parcel.writeString(homepage);
        parcel.writeString(place_of_birth);
        parcel.writeString(biography);
        parcel.writeDouble(popularity);
        parcel.writeByte((byte) (adult ? 1 : 0));
    }
}
