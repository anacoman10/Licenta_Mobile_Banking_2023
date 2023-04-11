package com.example.licentamobilebanking.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.licentamobilebanking.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mInfoWindowView;

    public CustomInfoWindowAdapter(LayoutInflater inflater) {
        mInfoWindowView = inflater.inflate(R.layout.pop_layout, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // Return null so that getInfoContents() is called.
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Populate the views in your custom info window layout.
        TextView titleTextView = mInfoWindowView.findViewById(R.id.adresaBanca);
        TextView snippetTextView = mInfoWindowView.findViewById(R.id.adresa);

        titleTextView.setText(marker.getTitle());
        snippetTextView.setText(marker.getSnippet());
        // Set the desired width and height of the pop-up view
        int width = 600; // Change this to your desired width
        int height = 300; // Change this to your desired height
        mInfoWindowView.setLayoutParams(new ViewGroup.LayoutParams(width, height));

        return mInfoWindowView;
    }
}
