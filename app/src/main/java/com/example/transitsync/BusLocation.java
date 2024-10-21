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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_location);

        fromInput = findViewById(R.id.from_input);
        toInput = findViewById(R.id.to_input);
        trackButton = findViewById(R.id.track_button);

        fromLocation = getIntent().getStringExtra("fromLocation");
        toLocation = getIntent().getStringExtra("toLocation");

        if (fromLocation != null) {
            fromInput.setText(fromLocation);
        }
        if (toLocation != null) {
            toInput.setText(toLocation);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        // Add some dummy bus stops for demonstration; replace these with real data in production
        busStopLatLngs.clear();

        if (toLocation.equalsIgnoreCase("Trichy") || toLocation.equalsIgnoreCase("Thiruchirapalli")) {
            // Add coordinates directly to the list for the towns bt. Madurai and Trichy
            busStopLatLngs.add(new LatLng(10.031664719570538, 78.33813006002595));  // Melur
            busStopLatLngs.add(new LatLng(10.220211301494112, 78.38177743194308));  // Kottampatti
            busStopLatLngs.add(new LatLng(10.378700894488235, 78.38810950899274)); // Thuvankurichi
            busStopLatLngs.add(new LatLng(10.603650499814558, 78.54616073952953));   // Viralimalai
        } else if (toLocation.equalsIgnoreCase("Kodaikanal")) {
            busStopLatLngs.add(new LatLng(9.964748020085343, 78.06678256687798));  // Paravai
            busStopLatLngs.add(new LatLng(9.979374514127093, 78.03270173627483));  // Samayanallur
            busStopLatLngs.add(new LatLng(10.087063460532013, 77.96044666514466));  // Vadipatti
            busStopLatLngs.add(new LatLng(10.165031260452862, 77.85358346242785)); // Nilakottai
            busStopLatLngs.add(new LatLng(10.163465638366741, 77.75693412105699)); // Batlagundu
        } else if (toLocation.equalsIgnoreCase("Rajapalayam")) {
            busStopLatLngs.add(new LatLng(9.90076119507132, 78.16571814531451));   // Viraganoor
            busStopLatLngs.add(new LatLng(9.887063285825137, 78.1498856128198));  // Anuppanadi
            busStopLatLngs.add(new LatLng(9.831035884460702, 78.1014054730237));  // Transport Nagar
            busStopLatLngs.add(new LatLng(9.835682362437767, 78.03432793438847));  // Kappalur
            busStopLatLngs.add(new LatLng(9.821622011441576, 77.98817285467143));  // Tirumangalam
            busStopLatLngs.add(new LatLng(9.721239633559946, 77.85552881558337));   // T. Kallupatti (Madurai District)
            busStopLatLngs.add(new LatLng(9.513383342178843, 77.63738133656273));   // Srivilliputhur
        } else if (toLocation.equalsIgnoreCase("Sattur")) {
            busStopLatLngs.add(new LatLng(9.90076119507132, 78.16571814531451));   // Viraganoor
            busStopLatLngs.add(new LatLng(9.887063285825137, 78.1498856128198));  // Anuppanadi
            busStopLatLngs.add(new LatLng(9.831035884460702, 78.1014054730237));  // Transport Nagar
            busStopLatLngs.add(new LatLng(9.835682362437767, 78.03432793438847));  // Kappalur
            busStopLatLngs.add(new LatLng(9.821622011441576, 77.98817285467143));  // Tirumangalam
            busStopLatLngs.add(new LatLng(9.710396741036678, 77.97281993278128));  // Kalligudi
            busStopLatLngs.add(new LatLng(9.5680222655915, 77.96093587665884));  // Virudhunagar
        } else if (toLocation.equalsIgnoreCase("Kovilpatti")) {
            busStopLatLngs.add(new LatLng(9.90076119507132, 78.16571814531451));   // Viraganoor
            busStopLatLngs.add(new LatLng(9.887063285825137, 78.1498856128198));  // Anuppanadi
            busStopLatLngs.add(new LatLng(9.831035884460702, 78.1014054730237));  // Transport Nagar
            busStopLatLngs.add(new LatLng(9.835682362437767, 78.03432793438847));  // Kappalur
            busStopLatLngs.add(new LatLng(9.821622011441576, 77.98817285467143));  // Tirumangalam
            busStopLatLngs.add(new LatLng(9.710396741036678, 77.97281993278128));  // Kalligudi
            busStopLatLngs.add(new LatLng(9.5680222655915, 77.96093587665884));  // Virudhunagar
            busStopLatLngs.add(new LatLng(9.357158613205344, 77.91566188340128));  // Sattur
        } else if (toLocation.equalsIgnoreCase("Thirunelveli") || toLocation.equalsIgnoreCase("Nellai")) {
            busStopLatLngs.add(new LatLng(9.90076119507132, 78.16571814531451));   // Viraganoor
            busStopLatLngs.add(new LatLng(9.887063285825137, 78.1498856128198));  // Anuppanadi
            busStopLatLngs.add(new LatLng(9.831035884460702, 78.1014054730237));  // Transport Nagar
            busStopLatLngs.add(new LatLng(9.835682362437767, 78.03432793438847));  // Kappalur
            busStopLatLngs.add(new LatLng(9.821622011441576, 77.98817285467143));  // Tirumangalam
            busStopLatLngs.add(new LatLng(9.710396741036678, 77.97281993278128));  // Kalligudi
            busStopLatLngs.add(new LatLng(9.5680222655915, 77.96093587665884));  // Virudhunagar
            busStopLatLngs.add(new LatLng(9.357158613205344, 77.91566188340128));  // Sattur
            busStopLatLngs.add(new LatLng(9.172864952371825, 77.87160849320773));  // Kovilpatti
        } else if (toLocation.equalsIgnoreCase("Tiruppur")) {
            busStopLatLngs.add(new LatLng(9.964748020085343, 78.06678256687798));  // Paravai
            busStopLatLngs.add(new LatLng(9.979374514127093, 78.03270173627483));  // Samayanallur
            busStopLatLngs.add(new LatLng(10.087063460532013, 77.96044666514466));  // Vadipatti
            busStopLatLngs.add(new LatLng(10.23437166847572, 77.89898239156584));   // kamalapuram
            busStopLatLngs.add(new LatLng(10.282098249228465, 77.8720418593486));   // sempatti
            busStopLatLngs.add(new LatLng(10.49035887214724, 77.75798744651391)); // Oddanchatram
            busStopLatLngs.add(new LatLng(10.735874784446818, 77.52539402190622));  // Dharapuram
        } else if (toLocation.equalsIgnoreCase("Nagercoil")) {
            busStopLatLngs.add(new LatLng(9.90076119507132, 78.16571814531451));   // Viraganoor
            busStopLatLngs.add(new LatLng(9.887063285825137, 78.1498856128198));  // Anuppanadi
            busStopLatLngs.add(new LatLng(9.831035884460702, 78.1014054730237));  // Transport Nagar
            busStopLatLngs.add(new LatLng(9.835682362437767, 78.03432793438847));  // Kappalur
            busStopLatLngs.add(new LatLng(9.821622011441576, 77.98817285467143));  // Tirumangalam
            busStopLatLngs.add(new LatLng(9.710396741036678, 77.97281993278128));  // Kalligudi
            busStopLatLngs.add(new LatLng(9.5680222655915, 77.96093587665884));  // Virudhunagar
            busStopLatLngs.add(new LatLng(9.357158613205344, 77.91566188340128));  // Sattur
            busStopLatLngs.add(new LatLng(9.172864952371825, 77.87160849320773));  // Kovilpatti
            busStopLatLngs.add(new LatLng(8.713979186801675, 77.77170293841294));  // Thirunelveli
        } else if (toLocation.equalsIgnoreCase("Srivilliputhur")) {
            busStopLatLngs.add(new LatLng(9.90076119507132, 78.16571814531451));   // Viraganoor
            busStopLatLngs.add(new LatLng(9.887063285825137, 78.1498856128198));  // Anuppanadi
            busStopLatLngs.add(new LatLng(9.831035884460702, 78.1014054730237));  // Transport Nagar
            busStopLatLngs.add(new LatLng(9.835682362437767, 78.03432793438847));  // Kappalur
            busStopLatLngs.add(new LatLng(9.821622011441576, 77.98817285467143));  // Tirumangalam
            busStopLatLngs.add(new LatLng(9.710396741036678, 77.97281993278128));  // Kalligudi
            busStopLatLngs.add(new LatLng(9.5680222655915, 77.96093587665884));  // Virudhunagar
            busStopLatLngs.add(new LatLng(9.357158613205344, 77.91566188340128));  // Sattur
            busStopLatLngs.add(new LatLng(9.172864952371825, 77.87160849320773));  // Kovilpatti
            busStopLatLngs.add(new LatLng(8.713979186801675, 77.77170293841294));  // Thirunelveli
        } else if (toLocation.equalsIgnoreCase("Dindigul")) {
            busStopLatLngs.add(new LatLng(9.964748020085343, 78.06678256687798));  // Paravai
            busStopLatLngs.add(new LatLng(9.979374514127093, 78.03270173627483));  // Samayanallur
            busStopLatLngs.add(new LatLng(10.087063460532013, 77.96044666514466));  // Vadipatti
            busStopLatLngs.add(new LatLng(10.27920895427043, 77.92676658530152));   // Chinnalapatti
        } else if (toLocation.equalsIgnoreCase("Virudunagar")) {
            busStopLatLngs.add(new LatLng(9.90076119507132, 78.16571814531451));   // Viraganoor
            busStopLatLngs.add(new LatLng(9.887063285825137, 78.1498856128198));  // Anuppanadi
            busStopLatLngs.add(new LatLng(9.831035884460702, 78.1014054730237));  // Transport Nagar
            busStopLatLngs.add(new LatLng(9.835682362437767, 78.03432793438847));  // Kappalur
            busStopLatLngs.add(new LatLng(9.821622011441576, 77.98817285467143));  // Tirumangalam
            busStopLatLngs.add(new LatLng(9.710396741036678, 77.97281993278128));  // Kalligudi
        } else if (toLocation.equalsIgnoreCase("Coimabatore") || toLocation.equalsIgnoreCase("Kovai")) {
            busStopLatLngs.add(new LatLng(9.964748020085343, 78.06678256687798));  // Paravai
            busStopLatLngs.add(new LatLng(9.979374514127093, 78.03270173627483));  // Samayanallur
            busStopLatLngs.add(new LatLng(10.087063460532013, 77.96044666514466));  // Vadipatti
            busStopLatLngs.add(new LatLng(10.23437166847572, 77.89898239156584));   // kamalapuram
            busStopLatLngs.add(new LatLng(10.282098249228465, 77.8720418593486));   // sempatti
            busStopLatLngs.add(new LatLng(10.49035887214724, 77.75798744651391)); // Oddanchatram
            busStopLatLngs.add(new LatLng(10.735874784446818, 77.52539402190622));  // Dharapuram
            busStopLatLngs.add(new LatLng(10.995784092791842, 77.28653504812287));  // Palladam
            busStopLatLngs.add(new LatLng(11.025197059672134, 77.1242809468234));  // Sulur
        } else if (toLocation.equalsIgnoreCase("Sivakasi")) {
            busStopLatLngs.add(new LatLng(9.90076119507132, 78.16571814531451));   // Viraganoor
            busStopLatLngs.add(new LatLng(9.887063285825137, 78.1498856128198));  // Anuppanadi
            busStopLatLngs.add(new LatLng(9.831035884460702, 78.1014054730237));  // Transport Nagar
            busStopLatLngs.add(new LatLng(9.835682362437767, 78.03432793438847));  // Kappalur
            busStopLatLngs.add(new LatLng(9.821622011441576, 77.98817285467143));  // Tirumangalam
            busStopLatLngs.add(new LatLng(9.710396741036678, 77.97281993278128));  // Kalligudi
            busStopLatLngs.add(new LatLng(9.5680222655915, 77.96093587665884));  // Virudhunagar
            busStopLatLngs.add(new LatLng(9.566308044437587, 77.86197152666158));  // Amathur
            busStopLatLngs.add(new LatLng(9.506332706972314, 77.83953267908497));  // Vadamalapuram
        } else if (toLocation.equalsIgnoreCase("Paramakudi")) {
            busStopLatLngs.add(new LatLng(9.90076119507132, 78.16571814531451));   // Viraganoor
            busStopLatLngs.add(new LatLng(9.826748978039104, 78.25556030472625));   // Thiruppuvanam
            busStopLatLngs.add(new LatLng(9.696051752332517, 78.45656999197135));  // Manamadurai
            busStopLatLngs.add(new LatLng(9.587626539096998, 78.45671603267999));   // Parthibanur (Ramnad District)

        } else if (toLocation.equalsIgnoreCase("Theni")) {
            busStopLatLngs.add(new LatLng(9.933763502951392, 78.04796367801438));   // Nagamalai Pudukkottai
            busStopLatLngs.add(new LatLng(9.942592923622342, 77.97190073402918));   // Checkanurani
            busStopLatLngs.add(new LatLng(9.964881555536346, 77.78863893040254));   // Usilampatti
            busStopLatLngs.add(new LatLng(10.000452078747106, 77.619004558733249)); // Andipatti

        } else if (toLocation.equalsIgnoreCase("Ramnad")) {
            busStopLatLngs.add(new LatLng(9.90076119507132, 78.16571814531451));   // Viraganoor
            busStopLatLngs.add(new LatLng(9.826748978039104, 78.25556030472625));   // Thiruppuvanam
            busStopLatLngs.add(new LatLng(9.696051752332517, 78.45656999197135));  // Manamadurai
            busStopLatLngs.add(new LatLng(9.587626539096998, 78.45671603267999));   // Parthibanur (Ramnad District)
            busStopLatLngs.add(new LatLng(9.550676744734862, 78.58448222061764));  // Paramakudi
        } else if (toLocation.equalsIgnoreCase("Batlagundu")) {
            busStopLatLngs.add(new LatLng(9.964748020085343, 78.06678256687798));  // Paravai
            busStopLatLngs.add(new LatLng(9.979374514127093, 78.03270173627483));  // Samayanallur
            busStopLatLngs.add(new LatLng(10.087063460532013, 77.96044666514466));  // Vadipatti
            busStopLatLngs.add(new LatLng(10.165031260452862, 77.85358346242785)); // Nilakottai
        } else if (toLocation.equalsIgnoreCase("Thanjavur") || toLocation.equalsIgnoreCase("Tanjore")) {
            busStopLatLngs.add(new LatLng(10.031664719570538, 78.33813006002595));  // Melur
            busStopLatLngs.add(new LatLng(10.107955051130826, 78.59805089773428)); // Thirupathur (Sivaganga District)
            busStopLatLngs.add(new LatLng(10.246892306718571, 78.749689507008));  // Thirumayam
            busStopLatLngs.add(new LatLng(10.246892306718571, 78.749689507008));  // Pudukkottai
            busStopLatLngs.add(new LatLng(10.573580057651116, 79.01347002966655));  // Gandharvakkottai

        } else if (toLocation.equalsIgnoreCase("Chennai") || toLocation.equalsIgnoreCase("Madras")) {
            busStopLatLngs.add(new LatLng(10.031664719570538, 78.33813006002595));  // Melur
            busStopLatLngs.add(new LatLng(10.220211301494112, 78.38177743194308));  // Kottampatti
            busStopLatLngs.add(new LatLng(10.378700894488235, 78.38810950899274)); // Thuvankurichi
            busStopLatLngs.add(new LatLng(10.603650499814558, 78.54616073952953));   // Viralimalai
            busStopLatLngs.add(new LatLng(10.790230343150702, 78.70857268026077));   // Trichy
            busStopLatLngs.add(new LatLng(11.234820548474966, 78.88332449361666));  // Perambalur
            busStopLatLngs.add(new LatLng(11.676804854874037, 79.2874148166318));  // Ulundurpet
            busStopLatLngs.add(new LatLng(11.93870680154976, 79.48692083285518));  // Villupuram
            busStopLatLngs.add(new LatLng(12.226304628893985, 79.65075010893437));  // Tindivanam
            busStopLatLngs.add(new LatLng(12.037193784042081, 79.54682919815248)); // Vikravandi
            busStopLatLngs.add(new LatLng(12.682133361388374, 79.98858662402355));  // Chengalpattu
            busStopLatLngs.add(new LatLng(12.96751767089572, 80.14915804345344));  // Pallavaram
            busStopLatLngs.add(new LatLng(12.925076103855268, 80.10126183222538));  // Tambaram

        } else if (toLocation.equalsIgnoreCase("Kanyakumari")) {
            busStopLatLngs.add(new LatLng(9.90076119507132, 78.16571814531451));   // Viraganoor
            busStopLatLngs.add(new LatLng(9.887063285825137, 78.1498856128198));  // Anuppanadi
            busStopLatLngs.add(new LatLng(9.831035884460702, 78.1014054730237));  // Transport Nagar
            busStopLatLngs.add(new LatLng(9.835682362437767, 78.03432793438847));  // Kappalur
            busStopLatLngs.add(new LatLng(9.821622011441576, 77.98817285467143));  // Tirumangalam
            busStopLatLngs.add(new LatLng(9.710396741036678, 77.97281993278128));  // Kalligudi
            busStopLatLngs.add(new LatLng(9.5680222655915, 77.96093587665884));  // Virudhunagar
            busStopLatLngs.add(new LatLng(9.357158613205344, 77.91566188340128));  // Sattur
            busStopLatLngs.add(new LatLng(9.172864952371825, 77.87160849320773));  // Kovilpatti
            busStopLatLngs.add(new LatLng(8.713979186801675, 77.77170293841294));  // Thirunelveli
        } else if (toLocation.equalsIgnoreCase("Rameswaram")) {
            busStopLatLngs.add(new LatLng(9.90076119507132, 78.16571814531451));  // Viraganoor
            busStopLatLngs.add(new LatLng(9.826748978039104, 78.25556030472625));   // Thiruppuvanam
            busStopLatLngs.add(new LatLng(9.696051752332517, 78.45656999197135));  // Manamadurai
            busStopLatLngs.add(new LatLng(9.587626539096998, 78.45671603267999));   // Parthibanur (Ramnad District)
            busStopLatLngs.add(new LatLng(9.550676744734862, 78.58448222061764));  // Paramakudi
            busStopLatLngs.add(new LatLng(9.363966940358457, 78.83814900811409));  // Ramanathapuram
            busStopLatLngs.add(new LatLng(9.27738003910911, 79.12902838403694));  // Mandapam
        } else if (toLocation.equalsIgnoreCase("Thoothukudi") || toLocation.equalsIgnoreCase("Tuticorin")) {
            busStopLatLngs.add(new LatLng(9.90076119507132, 78.16571814531451));   // Viraganoor
            busStopLatLngs.add(new LatLng(9.887063285825137, 78.1498856128198));  // Anuppanadi
            busStopLatLngs.add(new LatLng(9.831035884460702, 78.1014054730237));  // Transport Nagar
            busStopLatLngs.add(new LatLng(9.812291778204711, 78.09879105127754));   // Valayangulam (Madurai District)
            busStopLatLngs.add(new LatLng(9.395306084092253, 78.1083498077489));   // Pandalkudi
            busStopLatLngs.add(new LatLng(9.674641612130662, 78.1029713604619));   // Kariapatti
            busStopLatLngs.add(new LatLng(9.514050803610298, 78.10072539179968));   // Aruppukottai
            busStopLatLngs.add(new LatLng(9.147675066957605, 77.98951920702456));   // Ettayapuram

        } else if (toLocation.equalsIgnoreCase("Pollachi")) {
            busStopLatLngs.add(new LatLng(9.964748020085343, 78.06678256687798));  // Paravai
            busStopLatLngs.add(new LatLng(9.979374514127093, 78.03270173627483));  // Samayanallur
            busStopLatLngs.add(new LatLng(10.087063460532013, 77.96044666514466));  // Vadipatti
            busStopLatLngs.add(new LatLng(10.23437166847572, 77.89898239156584));   // kamalapuram
            busStopLatLngs.add(new LatLng(10.282098249228465, 77.8720418593486));   // sempatti
            busStopLatLngs.add(new LatLng(10.49035887214724, 77.75798744651391)); // Oddanchatram
            busStopLatLngs.add(new LatLng(10.449237836314458, 77.51599374553061));   // palani
            busStopLatLngs.add(new LatLng(10.582200898109013, 77.2568722018005)); // Udumalpet

        } else if (toLocation.equalsIgnoreCase("Palani")) {
            busStopLatLngs.add(new LatLng(9.964748020085343, 78.06678256687798));  // Paravai
            busStopLatLngs.add(new LatLng(9.979374514127093, 78.03270173627483));  // Samayanallur
            busStopLatLngs.add(new LatLng(10.087063460532013, 77.96044666514466));  // Vadipatti
            busStopLatLngs.add(new LatLng(10.27920895427043, 77.92676658530152));   // Chinnalapatti
            busStopLatLngs.add(new LatLng(10.362653599882954, 77.97068098728576));   // dindigul
            busStopLatLngs.add(new LatLng(10.49035887214724, 77.75798744651391)); // Oddanchatram
        } else {      // toLocation == OOTY
            busStopLatLngs.add(new LatLng(9.964748020085343, 78.06678256687798));  // Paravai
            busStopLatLngs.add(new LatLng(9.979374514127093, 78.03270173627483));  // Samayanallur
            busStopLatLngs.add(new LatLng(10.087063460532013, 77.96044666514466));  // Vadipatti
            busStopLatLngs.add(new LatLng(10.27920895427043, 77.92676658530152));   // Chinnalapatti
            busStopLatLngs.add(new LatLng(10.362653599882954, 77.97068098728576));   // dindigul
            busStopLatLngs.add(new LatLng(10.49035887214724, 77.75798744651391)); // Oddanchatram
            busStopLatLngs.add(new LatLng(10.735874784446818, 77.52539402190622));  // Dharapuram
            busStopLatLngs.add(new LatLng(10.995784092791842, 77.28653504812287));  // Palladam
            busStopLatLngs.add(new LatLng(11.018012669184357, 77.1843479471724));  // Karanampettai
            busStopLatLngs.add(new LatLng(11.104992051201972, 77.17920159557023));  // Karumathampatti
            busStopLatLngs.add(new LatLng(11.2319472447323, 77.10678046872361));   // Annur
            busStopLatLngs.add(new LatLng(11.302036615633272, 76.9373693375828));  // Mettupalayam
            busStopLatLngs.add(new LatLng(11.343387779123868, 76.79431110543081));  // Coonoor
            busStopLatLngs.add(new LatLng(11.38339003343969, 76.72904228827798));  // Ketti
        }

        // Add markers for intermediate bus stops
        for (int i = 0; i < busStopLatLngs.size(); i++) {
            LatLng stopLatLng = busStopLatLngs.get(i);
            BitmapDescriptor busStopIcon = BitmapDescriptorFactory.fromResource(R.drawable.bus_stop);
            mMap.addMarker(new MarkerOptions().position(stopLatLng).title("Bus Stop " + (i + 1)).icon(busStopIcon));
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
        final double REACH_THRESHOLD = 0.0001; // Adjust the threshold for reaching the destination

        // Check if all bus stops have been visited and the bus has reached the final destination
        return currentStopIndex >= busStopLatLngs.size() &&
                Math.abs(busCurrentLocation.latitude - toLatLng.latitude) < REACH_THRESHOLD &&
                Math.abs(busCurrentLocation.longitude - toLatLng.longitude) < REACH_THRESHOLD;
    }


    private void stopTrackingBus() {
        if (busTimer != null) {
            busTimer.cancel();
            busTimer = null;
            isTrackingBus = false; // Update the tracking status
        }
    }
}
