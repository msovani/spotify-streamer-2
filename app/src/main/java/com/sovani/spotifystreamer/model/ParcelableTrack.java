package com.sovani.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;



public class ParcelableTrack implements Parcelable{
    private final String albumName;
    private final String trackName;
    private final String url;
    private final String id;

    public String getAlbumName() {
        return albumName;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getUrl() {
        return url;
    }

    public ParcelableTrack(String vName, String vNumber, String imageurl, String idnumber)
    {
        this.albumName = vName;
        this.trackName = vNumber;
        this.url = imageurl;
        this.id = idnumber;
    }

    public String getId() {
        return id;
    }

    private ParcelableTrack(Parcel in){
        albumName = in.readString();
        trackName = in.readString();

        url = in.readString();
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return albumName + "--" + trackName + "--" + url; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(albumName);
        parcel.writeString(trackName);
        parcel.writeString(url);
        parcel.writeString(id);
    }

    public static final Parcelable.Creator<ParcelableTrack> CREATOR = new Parcelable.Creator<ParcelableTrack>() {
        @Override
        public ParcelableTrack createFromParcel(Parcel parcel) {
            return new ParcelableTrack(parcel);
        }

        @Override
        public ParcelableTrack[] newArray(int i) {
            return new ParcelableTrack[i];
        }

    };
}