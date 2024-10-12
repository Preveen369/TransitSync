package com.example.transitsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DetailFragment extends Fragment {

    private ImageView busImage;
    private TextView busRouteName, busNumber, busTimings, distanceCovered, nearbyStops;
    private Button viewBusLocationButton;
    private String[] locations;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        busImage = view.findViewById(R.id.bus_image);
        busRouteName = view.findViewById(R.id.bus_route_name);
        busNumber = view.findViewById(R.id.bus_number);
        busTimings = view.findViewById(R.id.bus_timings);
        distanceCovered = view.findViewById(R.id.distance_covered);
        nearbyStops = view.findViewById(R.id.nearby_stops);
        viewBusLocationButton = view.findViewById(R.id.view_bus_location);

        // Retrieve data from arguments
        if (getArguments() != null) {
            busImage.setImageResource(getArguments().getInt("imageResId"));
            busRouteName.setText(getArguments().getString("routeName"));
            busNumber.setText(getArguments().getString("busNumber"));
            busTimings.setText(getArguments().getString("busTimings"));
            distanceCovered.setText(getArguments().getString("distanceCovered"));
            nearbyStops.setText(getArguments().getString("nearbyStops"));

            String busRouteNameInput = busRouteName.getText().toString();
            // Split the bus route name into from and to locations
            locations = busRouteNameInput.split(" to ");
        }

        // Set functionality for the "View Bus Location" button
        viewBusLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve "from location" and "to location"
                String fromLocation = locations.length > 0 ? locations[0].trim() : ""; // Get the first part as fromLocation
                String toLocation = locations.length > 1 ? locations[1].trim() : "";   // Get the second part as toLocation

                // Start BusLocation activity and pass the "from" and "to" locations
                Intent intent = new Intent(getActivity(), BusLocation.class);
                intent.putExtra("fromLocation", fromLocation);
                intent.putExtra("toLocation", toLocation);
                startActivity(intent);

                Toast.makeText(getContext(), "Track the bus location", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
