package com.halfwitdevs.countripedia;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BookmarkFragment extends Fragment{
    View view;
    private ListView bookmarkList;
    private ArrayAdapter adapterBookmarkList;
    private android.support.v7.widget.Toolbar toolbar;
    BookmarkDatabaseAdapter bookmarkDatabaseAdapter;
    ArrayList<String> bookmarks;
    ArrayList<String[]> bookmarksCountryAndCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        bookmarkDatabaseAdapter = new BookmarkDatabaseAdapter(getActivity(), null, null, 1);
        bookmarks(view);
        bookmarksCountryAndCode = bookmarkDatabaseAdapter.dataBaseToNameCode();
        if (bookmarks.size() > 0 && !bookmarks.get(0).equals("No bookmarks yet!")) {
            bookmarkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!bookmarks.get(position).equals("No bookmarks yet!")) {
                        String[] nameCode = bookmarksCountryAndCode.get(position);
                        Intent intent = new Intent(getActivity(), CountryInfoDisplayActivity.class);
                        intent.putExtra("COUNTRYCODE", nameCode[1]);
                        intent.putExtra("COUNTRYNAME", nameCode[0]);
                        startActivity(intent);
                    }
                }
            });
            bookmarkList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    if (!bookmarks.get(position).equals("No bookmarks yet!")) {
                        final String[] nameCode = bookmarksCountryAndCode.get(position);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Delete " + nameCode[0] + "?").setMessage("Are you sure you want to delete " + nameCode[0] + " from your bookmarks?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bookmarkDatabaseAdapter.deleteCountry(nameCode[0]);
                                bookmarks.remove(position);
                                Toast.makeText(getActivity(), nameCode[0] + " deleted", Toast.LENGTH_SHORT).show();
                                getFragmentManager().beginTransaction().detach(BookmarkFragment.this)
                                        .attach(BookmarkFragment.this).commit();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        builder.create().show();
                    }
                    return true;
                }
            });
        }

        return view;
    }

    public void bookmarks(View view) {

        bookmarks = bookmarkDatabaseAdapter.dataBaseToString();
        if (bookmarks.size() > 0) {
            adapterBookmarkList = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, bookmarks);
        } else {
            bookmarks.add("No bookmarks yet!");
            adapterBookmarkList = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, bookmarks);
        }
        bookmarkList = view.findViewById(R.id.bookmarkList);
        bookmarkList.setAdapter(adapterBookmarkList);
    }
}
