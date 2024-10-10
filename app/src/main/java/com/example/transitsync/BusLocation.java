package com.example.transitsync;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.List;

public class BusLocation extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText fromInput;
    private EditText toInput;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_location);

        // Initialize the EditTexts and Button
        fromInput = findViewById(R.id.from_input);
        toInput = findViewById(R.id.to_input);
        searchButton = findViewById(R.id.search_button);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up the search button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fromLocation = fromInput.getText().toString();
                String toLocation = toInput.getText().toString();

                // Check if at least one location is entered
                if (TextUtils.isEmpty(fromLocation) && TextUtils.isEmpty(toLocation)) {
                    Toast.makeText(BusLocation.this, "Please enter at least one location", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Clear the map before adding new markers
                mMap.clear();

                // Add markers for the entered locations
                if (!fromLocation.isEmpty()) {
                    addMarker(fromLocation, "From Location");
                }
                if (!toLocation.isEmpty()) {
                    addMarker(toLocation, "To Location");
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Optionally, set the initial camera position
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2)); // Move to a default position
    }

    private void addMarker(String location, String title) {
        Geocoder geocoder = new Geocoder(this);
        try {
            // Get the list of addresses from the location string
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(title));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12)); // Adjust zoom level as needed
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Geocoder service not available", Toast.LENGTH_SHORT).show();
        }
    }
}
