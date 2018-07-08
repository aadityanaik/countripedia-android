package anotherappdev.countripedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ReferencesFragment extends Fragment {
    private RecyclerView recyclerView;                                  //Recycler View
    private CardViewRecyclerAdapter adapter;                            //Adapter loads only as many recycler view items as required and not all at once
    private RecyclerView.LayoutManager layoutManager;                   //Inflates a layout for the recycler view to follow
    private ArrayList<ReferenceObject> referenceObjects;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_references_layout, container, false);
        createList();
        buildRecyclerView(view);
        return view;
    }

    public void openLink(int position) {
        //referenceObjects.get(position);
        //Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(referenceObjects.get(position).getmText2()));
        Intent browseIntent = new Intent(getActivity(), WebViewActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("Link", referenceObjects.get(position).getmText2());
        browseIntent.putExtras(bundle);
        startActivity(browseIntent);
    }

    public void createList() {
        referenceObjects = new ArrayList<>();
        referenceObjects.add(new ReferenceObject("API for country information", "https://www.restcountries.eu"));
        referenceObjects.add(new ReferenceObject("API for currency conversion", "https://free.currencyconverterapi.com"));
        referenceObjects.add(new ReferenceObject("Wikipedia API for data on languages", "https://www.mediawiki.org/wiki/API:Main_page"));
        referenceObjects.add(new ReferenceObject("Gson library", "https://www.github.com/google/gson"));
        referenceObjects.add(new ReferenceObject("AndroidSVGLoader", "https://www.github.com/ar-android/AndroidSvgLoader"));
        referenceObjects.add(new ReferenceObject("Material Search View", "https://www.github.com/MiguelCatalan/MaterialSearchView"));
        referenceObjects.add(new ReferenceObject("Chart Illustrations", "https://www.github.com/PhilJay/MPAndroidChart"));
        referenceObjects.add(new ReferenceObject("Data for population graphs", "https://datahelpdesk.worldbank.org/knowledgebase/articles/889392-api-documentation"));
    }

    public void buildRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new CardViewRecyclerAdapter(referenceObjects);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CardViewRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                openLink(position);
            }
        });
    }
}
