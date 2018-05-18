package com.halfwitdevs.countripedia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    MapView mapView;
    GoogleMap googleMap;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if(getArguments() != null) {
            switch(getArguments().getString("CATEGORY")) {
                case "COUNTRY":
                    float lat = getArguments().getFloat("LAT"), lng = getArguments().getFloat("LNG");
                    String name = getArguments().getString("LOC");

                    final LatLng location = new LatLng(lat, lng);
                    googleMap.addMarker(new MarkerOptions().position(location).title(name));
                    BoundsDataBaseHandler dataBaseHandler = new BoundsDataBaseHandler(getContext());
                    dataBaseHandler.createDatabase();
                    try {
                        final LatLngBounds bounds = dataBaseHandler.getCountryBounds(getArguments().getString("CODE"));

                        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                            @Override
                            public void onMapLoaded() {
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "SHITE", Toast.LENGTH_SHORT).show();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
                    }
            }
        }
    }
}
