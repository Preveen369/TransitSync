package com.example.transitsync;

import android.annotation.SuppressLint;
import android.content.IntentSender;
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

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BusLocation extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText fromInput;
    private EditText toInput;
    private String fromLocation, toLocation;
    private Button trackButton;
    private LatLng fromLatLng, toLatLng;
    private List<LatLng> busStopLatLngs = new ArrayList<>(); // List to store bus stop locations
    private Timer busTimer;
    private LatLng busCurrentLocation;
    private Polyline busPolyline;
    private Marker busMarker;
    private boolean isTrackingBus = false;
    private int currentStopIndex = 0; // To keep track of the current bus stop index
    // Get bus stops from BusStopsManager
    private BusStopsManager busStopsManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_location);

        fromInput = findViewById(R.id.from_input);
        toInput = findViewById(R.id.to_input);
        trackButton = findViewById(R.id.track_button);

        fromLocation = getIntent().getStringExtra("fromLocation");
        toLocation = getIntent().getStringExtra("toLocation");
        busStopsManager = new BusStopsManager(); // Initialize BusStopsManager

        if (fromLocation != null) {
            fromInput.setText(fromLocation);
            makeEditTextNonEditable(fromInput);
        }
        if (toLocation != null) {
            toInput.setText(toLocation);
            makeEditTextNonEditable(toInput);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTrackingBus)
                    checkLocationSettingsAndTrackBus();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2));
    }

    private void makeEditTextNonEditable(EditText editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setClickable(false);
    }

    private void checkLocationSettingsAndTrackBus() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        client.checkLocationSettings(builder.build())
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    trackBusIfLocationsEntered();
                })
                .addOnFailureListener(this, e -> {
                    if (e instanceof ResolvableApiException) {
                        try {
                            ((ResolvableApiException) e).startResolutionForResult(BusLocation.this, 1001);
                        } catch (IntentSender.SendIntentException sendEx) {
                            sendEx.printStackTrace();
                        }
                    } else {
                        Toast.makeText(BusLocation.this, "Location services are not enabled", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void trackBusIfLocationsEntered() {
        String fromLocation = fromInput.getText().toString();
        String toLocation = toInput.getText().toString();

        if (TextUtils.isEmpty(fromLocation) || TextUtils.isEmpty(toLocation)) {
            Toast.makeText(BusLocation.this, "Please enter both locations", Toast.LENGTH_SHORT).show();
            return;
        }

        mMap.clear();

        fromLatLng = addMarker(fromLocation, "From Location");
        toLatLng = addMarker(toLocation, "To Location");

        if (fromLatLng != null && toLatLng != null) {
            addIntermediateBusStops(); // Add bus stops between the start and end locations
            drawRoute();
            if (!isTrackingBus) {
                startTrackingBus();
                isTrackingBus = true;
            }
        } else {
            Toast.makeText(BusLocation.this, "Unable to geocode one or both locations", Toast.LENGTH_SHORT).show();
        }
    }

    private LatLng addMarker(String location, String title) {
        Geocoder geocoder = new Geocoder(this);
        try {
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
        return null;
    }

    private void addIntermediateBusStops() {
        if (toLocation.isEmpty()) {
            return;
        }

        busStopLatLngs = busStopsManager.getBusStopLocations(toLocation);
        BitmapDescriptor busStopIcon = BitmapDescriptorFactory.fromResource(R.drawable.bus_stop);

        // Add markers for each bus stop
        for (LatLng busStop : busStopLatLngs) {
            mMap.addMarker(new MarkerOptions()
                    .position(busStop)
                    .title("Bus Stop" + (busStopLatLngs.indexOf(busStop) + 1))
                    .icon(busStopIcon));
        }
    }

    private void drawRoute() {
        if (mMap != null && fromLatLng != null && toLatLng != null) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(fromLatLng)
                    .addAll(busStopLatLngs) // Add all intermediate bus stops
                    .add(toLatLng)
                    .color(getResources().getColor(android.R.color.holo_red_dark))
                    .width(10);

            if (busPolyline != null) {
                busPolyline.remove();
            }
            busPolyline = mMap.addPolyline(polylineOptions);

            // Adjust camera to fit all points
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(fromLatLng);
            for (LatLng stop : busStopLatLngs) {
                builder.include(stop);
            }
            builder.include(toLatLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        }
    }

    @SuppressLint("DiscouragedApi")
    private void startTrackingBus() {
        busCurrentLocation = fromLatLng;
        currentStopIndex = 0; // Start from the first stop

        busTimer = new Timer();
        busTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (hasBusReachedDestination()) {
                        busCurrentLocation = toLatLng;
                        isTrackingBus = false;
                        if (busMarker != null) {
                            busMarker.remove();
                            busMarker = null;
                        }
                        BitmapDescriptor busIcon = BitmapDescriptorFactory.fromResource(R.drawable.bus_clipart);
                        busMarker = mMap.addMarker(new MarkerOptions().position(busCurrentLocation).title("Bus Location").icon(busIcon));
                        stopTrackingBus();
                        Toast.makeText(getApplicationContext(), "Bus has arrived at the destination", Toast.LENGTH_SHORT).show();
                    } else {
                        LatLng newLocation = calculateNextBusLocation(busCurrentLocation);
                        busCurrentLocation = newLocation;

                        if (busMarker != null) {
                            busMarker.setPosition(busCurrentLocation);
                        } else {
                            BitmapDescriptor busIcon = BitmapDescriptorFactory.fromResource(R.drawable.bus_clipart);
                            busMarker = mMap.addMarker(new MarkerOptions().position(busCurrentLocation).title("Bus Location").icon(busIcon));
                        }
                    }
                });
            }
        }, 50, 200);
        isTrackingBus = true; // Update the tracking status
    }

    private LatLng calculateNextBusLocation(LatLng currentLocation) {
        // Ensure the bus goes through each bus stop sequentially
        if (currentStopIndex < busStopLatLngs.size()) {
            LatLng nextStop = busStopLatLngs.get(currentStopIndex);
            double latDiff = nextStop.latitude - currentLocation.latitude;
            double lngDiff = nextStop.longitude - currentLocation.longitude;

            // Move the bus by a small fraction of the distance towards the next bus stop
            double newLat = currentLocation.latitude + latDiff * 0.05; // Adjust this value for speed
            double newLng = currentLocation.longitude + lngDiff * 0.05; // Adjust this value for speed

            // Check if the bus has reached the next stop
            if (Math.abs(newLat - nextStop.latitude) < 0.0001 && Math.abs(newLng - nextStop.longitude) < 0.0001) {
                currentStopIndex++; // Move to the next bus stop
            }

            return new LatLng(newLat, newLng);
        } else {
            // Once all bus stops are covered, move towards the final destination
            double latDiff = toLatLng.latitude - currentLocation.latitude;
            double lngDiff = toLatLng.longitude - currentLocation.longitude;

            double newLat = currentLocation.latitude + latDiff * 0.05; // Adjust this value for speed
            double newLng = currentLocation.longitude + lngDiff * 0.05; // Adjust this value for speed

            return new LatLng(newLat, newLng);
        }
    }

    private boolean hasBusReachedDestination() {
        final double REACH_THRESHOLD = 0.0001; // Adjust the threshold for reaching the destination (0.0001)

        // Check if all bus stops have been visited and the bus has reached the final destination
        return currentStopIndex >= busStopLatLngs.size() &&
                Math.abs(busCurrentLocation.latitude - toLatLng.latitude) < REACH_THRESHOLD &&
                Math.abs(busCurrentLocation.longitude - toLatLng.longitude) < REACH_THRESHOLD;
    }


    private void stopTrackingBus() {
        isTrackingBus = false; // Update the tracking status
        if (busTimer != null) {
            busTimer.cancel();
            busTimer = null;
        }
    }

    @Override
    public void onDestroy() {
        isTrackingBus = false;
        super.onDestroy();
        stopTrackingBus();
    }
}
