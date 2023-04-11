package com.example.licentamobilebanking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.licentamobilebanking.classes.BankLocation;
import com.example.licentamobilebanking.classes.CustomInfoWindowAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsFragment extends Fragment {

    private GoogleMap mMap;

    private Random random;
    private List<BankLocation> bankList=new ArrayList<>();
    private static final int MAP_ZOOM=15;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            random = new Random();
            getBanksLocations();
            CustomInfoWindowAdapter infoWindowAdapter = new CustomInfoWindowAdapter(getLayoutInflater());
            mMap.setInfoWindowAdapter(infoWindowAdapter);
            for (int i = 0; i < bankList.size(); i++) {
                BankLocation location = bankList.get(i);
                if (i == 0) {
                    CameraPosition camPos = new CameraPosition(location.getCoordinates(),
                            MAP_ZOOM, 0, 0);
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
                }
                mMap.addMarker(new MarkerOptions()
                        .position(location.getCoordinates())
                        .title(location.getAddress())
                        .snippet(location.getCountry())
                        .icon(BitmapFromVector(getContext(), R.drawable.bankingmarker)));
            }

        }
    };



    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void getBanksLocations() {
        bankList.add(new BankLocation(new LatLng(44.447750,26.096797), "Romania", "Piata Romana 6 B"));
        bankList.add(new BankLocation(new LatLng(44.446200,26.087816), "Romania", "Strada Mihail Moxa B"));
        bankList.add(new BankLocation(new LatLng(46.773431, 23.630890), "Romania", "Strada T. Mihali CJ"));
        bankList.add(new BankLocation(new LatLng(51.627957, -0.753153), "UK", "Queen Alexandra Rd"));
    }
}