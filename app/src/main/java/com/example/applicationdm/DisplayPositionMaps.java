package com.example.applicationdm;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.applicationdm.databinding.ActivityDisplayPositionMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.example.applicationdm.directionhelpers.FetchURL;
import com.example.applicationdm.directionhelpers.TaskLoadedCallback;

import java.util.ArrayList;
import java.util.Objects;

public class DisplayPositionMaps extends FragmentActivity implements GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    private static ArrayList<Coordinates> locationList;
    private static Coordinates myCoordinates = new Coordinates();

    private Polyline currentPolyline;

    private MarkerOptions markerDanger;
    private MarkerOptions markerMoi;
    // Le principal point d'entrée pour intragir avec le fournisseur de localisation fusionée
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationList = new ArrayList<>();
        com.example.applicationdm.databinding.ActivityDisplayPositionMapsBinding binding = ActivityDisplayPositionMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        //Initialisation de fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(DisplayPositionMaps.this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker for our position and an other for the person in danger
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        //Store his own location to compare with others
        vOnMapReadyStoreLocation();

        //Retrieve data : own location
        FirebaseDatabase.getInstance("https://appdm-fb9c5-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("MyLocalisation/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addValueEventListener(newValueListener);

        //Retrieve data : others locations
        FirebaseDatabase.getInstance("https://appdm-fb9c5-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("DangerLocalisation")
                .orderByKey()
                .addListenerForSingleValueEvent(queryValueListener);

    }

    /*-----
    Store our own location to compare later with others users
    -----*/
    private void vOnMapReadyStoreLocation() {
        if ((ActivityCompat.checkSelfPermission(DisplayPositionMaps.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(DisplayPositionMaps.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            //Initialisation de la localisation
            Location localisation = task.getResult();

            if (localisation != null) {
                //Si aucune erreur n'est détéctée
                Coordinates mCoordinates = new Coordinates(localisation.getLatitude(), localisation.getLongitude());

                FirebaseDatabase.getInstance("https://appdm-fb9c5-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("MyLocalisation")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .setValue(mCoordinates).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Coordonnées mises à jour !",
                                Toast.LENGTH_LONG).show();

                    } else {
                        //If sign in fails, display a message to the user.
                        Log.i(TAG, "Error");
                    }
                });
            }
        });
    }

    /*-----
    retrieve his own location data
    -----*/
    ValueEventListener newValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                myCoordinates = snapshot.getValue(Coordinates.class);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    /*-----
    retrieve all users location data
    -----*/
    ValueEventListener queryValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Coordinates coordinates = snapshot.getValue(Coordinates.class);

                    locationList.add(coordinates);
                    if (locationList == null) {
                        locationList = new ArrayList<>();
                    }
                    for (int i = 0; i < locationList.size(); i++) {
                        Coordinates getCoordinates = locationList.get(i);

                        //We compare to know if the other users are close or not enough
                        if ((Math.abs(getCoordinates.Latitude - myCoordinates.Latitude) <= 0.0089833458001069803820630534358898040659149376711452369942239328)
                                && (Math.abs(getCoordinates.Longitude - myCoordinates.Longitude) <= 0.0089833458001069803820630534358898040659149376711452369942239328)) {

                            LatLng dangerLocation = new LatLng(getCoordinates.Latitude, getCoordinates.Longitude);

                            markerDanger = new MarkerOptions().position(dangerLocation).alpha(0.7f).title("DANGER");
                            mMap.addMarker(markerDanger);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dangerLocation, 17F));
                            mMap.addMarker(markerDanger).setTag(0);
                        }
                        LatLng myLocation = new LatLng(myCoordinates.Latitude, myCoordinates.Longitude);

                        markerMoi = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(220)).position(myLocation).alpha(0.7f).title("Moi");
                        mMap.addMarker(markerMoi);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17F));
                        mMap.addMarker(markerMoi).setTag(0);
                    }
                    // Set a listener for marker click.
                    mMap.setOnMarkerClickListener(DisplayPositionMaps.this);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.i(TAG, "Error ");
        }
    };

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        new FetchURL(DisplayPositionMaps.this).execute(getUrl(markerDanger.getPosition(), markerMoi.getPosition(), "walking"), "walking");
        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }


    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}