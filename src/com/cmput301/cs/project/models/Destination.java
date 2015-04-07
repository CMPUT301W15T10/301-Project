package com.cmput301.cs.project.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;

public class Destination implements Parcelable {

    public static final class Builder {
        private String mName;
        private LatLng mLocation;
        private String mReason;

        public Builder() {
        }

        public Builder(String name, String reason) {
            mName = ClaimUtils.nonNullNonEmptyOrThrow(name, "name");
            mReason = reason;
        }

        private Builder(Destination destination) {
            mName = destination.getName();
            mLocation = destination.getLocation();
            mReason = destination.getReason();
        }

        public Builder name(String name) {
            mName = ClaimUtils.nonNullNonEmptyOrThrow(name, "name");
            return this;
        }

        public Builder location(LatLng location) {
            mLocation = ClaimUtils.nonNullOrThrow(location, "location");
            return this;
        }

        public Builder reason(String reason) {
            mReason = reason;
            return this;
        }

        public String getName() {
            return mName;
        }

        public LatLng getLocation() {
            return mLocation;
        }

        public String getReason() {
            return mReason;
        }

        public Destination build() {
            return new Destination(this);
        }
    }

    private final String mName;
    private final LatLng mLocation;
    private final String mReason;

    private Destination(Builder b) {
        mName = b.mName;
        mLocation = b.mLocation;
        mReason = b.mReason;
    }

    protected Destination(Parcel in) {
        mName = in.readString();
        mLocation = in.readParcelable(LatLng.class.getClassLoader());
        mReason = in.readString();
    }

    public Builder edit() {
        return new Builder(this);
    }

    public String getName() {
        return mName;
    }

    public LatLng getLocation() {
        return mLocation;
    }

    public String getReason() {
        return mReason;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // generated by http://www.parcelabler.com/
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeParcelable(mLocation, 0);
        dest.writeString(mReason);
    }

    // generated by http://www.parcelabler.com/
    public static final Parcelable.Creator<Destination> CREATOR = new Parcelable.Creator<Destination>() {
        @Override
        public Destination createFromParcel(Parcel in) {
            return new Destination(in);
        }

        @Override
        public Destination[] newArray(int size) {
            return new Destination[size];
        }
    };

    // Generated by IntelliJ
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Destination)) return false;

        final Destination that = (Destination) o;

        if (mName != null ? !mName.equals(that.mName) : that.mName != null) return false;
        if (mLocation != null ? !mLocation.equals(that.mLocation) : that.mLocation != null) return false;
        return !(mReason != null ? !mReason.equals(that.mReason) : that.mReason != null);

    }

    // Generated by IntelliJ
    @Override
    public int hashCode() {
        int result = mName != null ? mName.hashCode() : 0;
        result = 31 * result + (mLocation != null ? mLocation.hashCode() : 0);
        result = 31 * result + (mReason != null ? mReason.hashCode() : 0);
        return result;
    }
}