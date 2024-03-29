package anotherappdev.countripedia;

import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    LinearLayout mapLayout, progress;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapLayout = view.findViewById(R.id.map_layout);
        progress = view.findViewById(R.id.prog_circle);
        try {
            ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info.isConnected()) {
                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(this);
                }
            } else {
                Toast.makeText(getContext(), "Network Error: Check your connection", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            Toast.makeText(getContext(), "Network Error: Check your connection", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if(getArguments() != null) {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

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
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
                    }
                    ((MoreInfoActivity) getActivity()).setActTitle("Maps- " + name);
                    break;

                case "CAPITAL":
                    String capName = getArguments().getString("CAPNAME");
                    String countryName = getArguments().getString("COUNTRYNAME");

                    ArrayList addresses;

                    try {
                        addresses = (ArrayList)geocoder.getFromLocationName(capName + "," + countryName, 1);
                        LatLng capLocation = new LatLng(((Address)addresses.get(0)).getLatitude(), ((Address)addresses.get(0)).getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(capLocation)
                                .title(capName + "," + countryName));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(capLocation, 12));
                    } catch (IOException e) {

                    }
                    ((MoreInfoActivity) getActivity()).setActTitle("Maps- " + capName + ", " + countryName);
                    break;

                case "REGION":
                    String rgnName = getArguments().getString("RGN");

                    ArrayList region;
                    try {
                        LatLng regionLocation;
                        if(!rgnName.equals("Polar")) {
                            region = (ArrayList) geocoder.getFromLocationName(rgnName, 1);
                            regionLocation = new LatLng(((Address) region.get(0)).getLatitude(), ((Address) region.get(0)).getLongitude());
                        } else {
                            regionLocation = new LatLng(-74.65, 4.48);
                        }
                        googleMap.addMarker(new MarkerOptions().position(regionLocation)
                            .title(rgnName));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(regionLocation, 3));
                    } catch (IOException e) {

                    } catch (IndexOutOfBoundsException e) {

                    }

                    ((MoreInfoActivity) getActivity()).setActTitle("Maps- " + rgnName);
                    break;
            }

            progress.setVisibility(View.GONE);
            mapLayout.setVisibility(View.VISIBLE);
        }
    }
}
