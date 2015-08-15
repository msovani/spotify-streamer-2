package com.sovani.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This a wrapper class around data elements needed to show Artist information around Artist object.
 */
public class ParcelableArtist implements Parcelable{
    private final String artistName;

    private final String url;
    private final String id;

    public String getArtistName() {
        return artistName;
    }



    public String getUrl() {
        return url;
    }


    public ParcelableArtist(String vName,  String imageurl, String idnumber)
    {
        this.artistName = vName;
        this.url = imageurl;
        this.id = idnumber;
    }

    public String getId() {
        return id;
    }

    private ParcelableArtist(Parcel in){
        artistName = in.readString();
        url = in.readString();
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return artistName + "--" + url; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(artistName);
        parcel.writeString(url);
        parcel.writeString(id);
    }

    public static final Parcelable.Creator<ParcelableArtist> CREATOR = new Parcelable.Creator<ParcelableArtist>() {
        @Override
        public ParcelableArtist createFromParcel(Parcel parcel) {
            return new ParcelableArtist(parcel);
        }

        @Override
        public ParcelableArtist[] newArray(int i) {
            return new ParcelableArtist[i];
        }

    };
}
