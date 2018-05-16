package com.halfwitdevs.countripedia;

import android.app.Activity;
import android.content.Context;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.ahmadrosid.svgloader.SvgLoader;

public class CountrySearchListAdapter extends ArrayAdapter<CountryNames> {
    ImageView countryFlagView;
    CountryNames country;
    boolean getFlags;
    CountrySearchActivity.ViewHolder viewHolder;

    public CountrySearchListAdapter(Context context, CountryNames[] value, boolean loadPics) {
        super(context, R.layout.country_list_row, value);
        getFlags = loadPics;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.country_list_row, parent, false);
            viewHolder = new CountrySearchActivity.ViewHolder();
            viewHolder.countryTextView = convertView.findViewById(R.id.country_name);
            viewHolder.flagIcon = convertView.findViewById(R.id.country_flag);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CountrySearchActivity.ViewHolder) convertView.getTag();
        }

        country = getItem(position);
        viewHolder.countryTextView.setText(country.name);
        new imageLoader().run();

        return convertView;
    }

    protected class imageLoader implements Runnable{
        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            if (getFlags) {
                // wifi is selected
                SvgLoader.pluck()
                        .with((Activity) getContext())
                        .setPlaceHolder(R.drawable.progress_animation, R.drawable.progress_animation)
                        .load(country.flag, viewHolder.flagIcon);
            } else {
                viewHolder.flagIcon.setImageResource(R.mipmap.flag);
            }
        }
    }
}
