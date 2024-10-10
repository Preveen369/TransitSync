package com.example.transitsync;

import android.annotation.SuppressLint;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BusLocation extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText fromInput;
    private EditText toInput;
    private Button searchButton;
    private LatLng fromLatLng, toLatLng; // Variables to store the LatLng of the two locations
    private Timer busTimer; // Timer to track the bus
    private LatLng busCurrentLocation; // Variable to store the bus's current location
    private Polyline busPolyline; // Polyline for bus route
    private Marker busMarker; // Marker for the bus location

    private boolean isTrackingBus = false; // Flag to track if the bus is already being tracked

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

                // Check if both locations are entered
                if (TextUtils.isEmpty(fromLocation) || TextUtils.isEmpty(toLocation)) {
                    Toast.makeText(BusLocation.this, "Please enter both locations", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Clear the map before adding new markers
                mMap.clear();

                // Add markers and set the LatLng values for the entered locations
                fromLatLng = addMarker(fromLocation, "From Location");
                toLatLng = addMarker(toLocation, "To Location");

                // Draw the route if both locations are valid
                if (fromLatLng != null && toLatLng != null) {
                    drawRoute();
                    if (!isTrackingBus) { // Start tracking the bus only if it's not already tracking
                        startTrackingBus(); // Start tracking the bus after drawing the route
                    }
                } else {
                    Toast.makeText(BusLocation.this, "Unable to geocode one or both locations", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Enable Zoom Controls
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Optionally, set the initial camera position
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2)); // Move to a default position
    }

    private LatLng addMarker(String location, String title) {
        Geocoder geocoder = new Geocoder(this);
        try {
            // Get the list of addresses from the location string
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(title));
                return latLng;
            } else {
                Toast.makeText(this, "Location not found: " + location, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Geocoder service not available", Toast.LENGTH_SHORT).show();
        }
        return null; // Return null if location couldn't be geocoded
    }

    private void drawRoute() {
        if (mMap != null && fromLatLng != null && toLatLng != null) {
            // Draw a polyline between the two geocoded points
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(fromLatLng) // Starting location
                    .add(toLatLng) // Destination location
                    .color(getResources().getColor(android.R.color.holo_blue_dark))
                    .width(10);

            // Clear existing polyline if it exists
            if (busPolyline != null) {
                busPolyline.remove();
            }
            busPolyline = mMap.addPolyline(polylineOptions);

            // Optionally, adjust the camera to show the entire route
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                    new LatLngBounds.Builder()
                            .include(fromLatLng)
                            .include(toLatLng)
                            .build(), 100));
        }
    }

    @SuppressLint("DiscouragedApi")
    private void startTrackingBus() {
        busCurrentLocation = fromLatLng; // Start the bus at the "From" location

        // Create a timer to simulate bus movement
        busTimer = new Timer();
        busTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update bus location (for simulation purposes, move it towards 'toLatLng')
                        double latIncrement = (toLatLng.latitude - busCurrentLocation.latitude) / 100; // Adjust denominator for speed
                        double lngIncrement = (toLatLng.longitude - busCurrentLocation.longitude) / 100; // Adjust denominator for speed

                        busCurrentLocation = new LatLng(busCurrentLocation.latitude + latIncrement,
                                busCurrentLocation.longitude + lngIncrement);

                        // Remove the previous bus marker
                        if (busMarker != null) {
                            busMarker.remove();
                        }

                        // Draw the new bus marker
                        busMarker = mMap.addMarker(new MarkerOptions().position(busCurrentLocation).title("Bus Location"));
                        // Move camera to the bus location
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(busCurrentLocation));

                        // Check if the bus has reached the destination
                        if (hasBusReachedDestination()) {
                            stopTrackingBus(); // Call to stop the tracking
                        }
                    }
                });
            }
        }, 0, 150); // Update every second
        isTrackingBus = true; // Set the tracking flag to true
    }

    private boolean hasBusReachedDestination() {
        // Check if the bus is close enough to the destination (using a simple distance check)
        double threshold = 0.0001; // Define a small threshold for latitude/longitude
        return Math.abs(busCurrentLocation.latitude - toLatLng.latitude) < threshold &&
                Math.abs(busCurrentLocation.longitude - toLatLng.longitude) < threshold;
    }

    private void stopTrackingBus() {
        // Cancel the bus tracking timer if it is running
        if (busTimer != null) {
            busTimer.cancel();
            busTimer = null; // Set to null to prevent reuse
        }

        // Notify the user that the bus has arrived
        Toast.makeText(this, "Bus has arrived at the destination", Toast.LENGTH_SHORT).show();

        // Remove the bus marker if it exists
        if (busMarker != null) {
            busMarker.remove();
            busMarker = null;
        }

        isTrackingBus = false; // Reset the tracking flag
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the bus tracking timer if the activity is destroyed
        stopTrackingBus(); // Ensure that tracking stops when the activity is destroyed
    }
}
