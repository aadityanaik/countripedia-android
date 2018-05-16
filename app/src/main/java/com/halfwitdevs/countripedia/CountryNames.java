package com.halfwitdevs.countripedia;

import android.os.Parcel;
import android.os.Parcelable;

public class CountryNames implements Parcelable {

    String name;
    String alpha2Code;
    String flag;

    public CountryNames(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);

        name = data[0];
        alpha2Code = data[1];
        flag = data[2];
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {name, alpha2Code, flag});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Object createFromParcel(Parcel source) {
            return new CountryNames(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new CountryNames[size];
        }
    };
}
