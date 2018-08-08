package me.susieson.popularmovies.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

@Entity
public class Movie implements Parcelable {

    @PrimaryKey
    @SerializedName("id")
    private final int id;
    @ColumnInfo(name = "title")
    @SerializedName("title")
    private final String title;
    @ColumnInfo(name = "original_title")
    @SerializedName("original_title")
    private final String originalTitle;
    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    private final String posterPath;
    @ColumnInfo(name = "overview")
    @SerializedName("overview")
    private final String overview;
    @ColumnInfo(name = "vote_average")
    @SerializedName("vote_average")
    private final double voteAverage;
    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    private final String releaseDate;
    @ColumnInfo(name = "is_favorited")
    private boolean isFavorited = false;

    public Movie(String title, String originalTitle, String posterPath, String overview,
            double voteAverage,
            String releaseDate, int id) {
        this.title = title;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.id = id;
    }

    private Movie(Parcel in) {
        title = in.readString();
        originalTitle = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
        id = in.readInt();
        isFavorited = in.readInt() == 1;
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

    public String getTitle() {
        return title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int getId() {
        return id;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(originalTitle);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeDouble(voteAverage);
        parcel.writeString(releaseDate);
        parcel.writeInt(id);
        parcel.writeInt(isFavorited ? 1 : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Movie) {
            Movie movie = (Movie) obj;
            return movie.getId() == getId();
        }
        return false;
    }
}
