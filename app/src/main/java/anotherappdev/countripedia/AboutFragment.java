package anotherappdev.countripedia;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Objects;

public class AboutFragment extends Fragment {
    ListView listView;
    TextView messageTV;
    View view;

    final String MESSAGE = "Countripedia is an app made to bring an online atlas into your hands. " +
            "Using Countripedia, you can easily get all the vital information regarding a country. " +
            "You can look up countries, see their nicknames and explore the spoken languages. " +
            "Learn about the population growth and  distribution through an interactive graph " +
            "or the exchange rate of a country's currency.";

    final String[] CREATORLIST = {"Aaditya Naik", "Aditya Chakraborti"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about_layout, container, false);
        text();
        listView = view.findViewById(R.id.creator_list);
        return view;
    }

    public void text() {
        messageTV = view.findViewById(R.id.message);
        messageTV.setText(MESSAGE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, CREATORLIST);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Visit- ");

                final ArrayAdapter<String> profileListArrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1);

                final HashMap<String, String> profileMap = new HashMap<>();

                if (i == 0) {
                    // Its-a me, Aaditya
                    profileMap.put("Github", "https://www.github.com/rincemust/");
                } else if (i == 1) {
                    // Its-a me, Aditya
                    profileMap.put("Something", "https://www.google.com");
                }

                profileListArrayAdapter.addAll(profileMap.keySet());

                builder.setAdapter(profileListArrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String str = profileListArrayAdapter.getItem(i);
                        Toast.makeText(getContext(), "Redirecting you to " + str, Toast.LENGTH_SHORT).show();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(profileMap.get(str)));
                        startActivity(browserIntent);
                    }
                });

                builder.show();
            }
        });
    }
}

