package com.example.applicationdm;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Localisation extends AppCompatActivity {

    private static final boolean enableMessaging = true;
    //Initialisation des variables

    private ImageButton m112Button;
    private Button mDangerButton;
    private Button mCarteButton;
    private TextView mAdresseTextView;

    private WebView mLinkWebView;
    private static ArrayList<NewContact> peopleList;
    // Le principal point d'entrée pour intragir avec le fournisseur de localisation fusionée
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localisation);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        initActivity();
    }


    private void initActivity() {

        //Initialisation de fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Localisation.this);

        //Assignation des variables
        mDangerButton = findViewById(R.id.sender_button_danger);
        m112Button = findViewById(R.id.sender_button_112);
        mAdresseTextView = findViewById(R.id.sender_textview_adresse);
        mCarteButton = findViewById(R.id.sender_button_map);
        mLinkWebView = findViewById(R.id.webpage_webview_link);
        peopleList = new ArrayList<>();

        vCreateLoadData();
        vCreateOnClickLocalisation();
        vCreateOnClickCall();
        vCreateOnClickAfficherCarte();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.locmenu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.locmenu_informations:
                vCreateLink();
                break;
            case R.id.locmenu_contacts:
                vCreateContacts();
                break;
            case R.id.locmenu_deconnexion:
                vCreateDeconnexion();
                break;
            case R.id.locmenu_tuto:
                vCreateTuto();
                break;
            case R.id.locmenu_credits:
                vCreateCredits();
                break;
        }
        return true;
    }

    private void vCreateCredits() {
        Intent CreditsActivityIntent = new Intent(Localisation.this, Credits.class);
        startActivity(CreditsActivityIntent);
    }

    private void vCreateTuto() {
        //swipe de Ronan
        Intent TutorialActivityIntent = new Intent(Localisation.this, Tutorial.class);
        startActivity(TutorialActivityIntent);
    }

    private void vCreateOnClickAfficherCarte() {
        mCarteButton.setOnClickListener(view -> {
            Intent MapsActivityIntent = new Intent(Localisation.this, DisplayPositionMaps.class);
            startActivity(MapsActivityIntent);
        });
    }

    /*-----
    Service d'urgence général
    -----*/
    private void vCreateOnClickLocalisation() {

        mDangerButton.setOnClickListener(v -> vCreateUrgence());
    }

    /*-----
    Appeler le 112/ service européen d'urgence
    -----*/
    private void vCreateOnClickCall() {
        m112Button.setOnClickListener(v -> {
            Intent CallActivityIntent = new Intent(Intent.ACTION_CALL);
            CallActivityIntent.setData(Uri.parse("tel:" + "112"));
            startActivity(CallActivityIntent);
            Toast.makeText(getApplicationContext(), "Calling...",
                    Toast.LENGTH_LONG).show();
        });
    }

    /*-----
    Ouvrir le lien d'information
    -----*/
    @SuppressLint("SetJavaScriptEnabled")
    private void vCreateLink() {
        mLinkWebView = new WebView(Localisation.this);
        WebSettings myWebSettings = mLinkWebView.getSettings();
        myWebSettings.setJavaScriptEnabled(true);
        setContentView(mLinkWebView);
        mLinkWebView.loadUrl("https://ameli.fr/");
    }

    /*-----
    Se déconnecter de son compte
    -----*/
    private void vCreateDeconnexion() {
        FirebaseAuth.getInstance().signOut();
        Intent AccueilActivityIntent = new Intent(Localisation.this, Accueil.class);
        startActivity(AccueilActivityIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.move);
    }

    /*-----
    Ouvrir le gestionnaire de contacts
    -----*/
    private void vCreateContacts() {
        Intent ContactsActivityIntent = new Intent(Localisation.this, Contact.class);
        startActivity(ContactsActivityIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /*-----
    Service de géolocalisation
    -----*/
    private void vCreateUrgence() {
        if ((ActivityCompat.checkSelfPermission(Localisation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(Localisation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            //Initialisation de la localisation
            Location localisation = task.getResult();

            if (localisation != null) {
                //Si aucune erreur n'est détéctée
                try {
                    //Obtention des coordonnée (latitude, longitude)
                    Geocoder geocoder = new Geocoder(Localisation.this, Locale.getDefault());

                    //Initialisation de  la liste d'adresse
                    List<Address> addresses = geocoder.getFromLocation(localisation.getLatitude(), localisation.getLongitude(), 1);
                    vCreateStoreCoordinates(localisation);

                    //Paramétrer l'adresse
                    mAdresseTextView.setText(Html.fromHtml("<font color= '#6200EE'><b>Adresse actuelle trouvée : </b><br></font>" + addresses.get(0).getAddressLine(0)));
                    if (enableMessaging) {
                        vCreateSendMessage(addresses.get(0).getAddressLine(0));
                    }
                    //Si une erreur est détéctée
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*-----
    Enregistrer les coordonnées de géolocalisation dans la database
    -----*/
    private void vCreateStoreCoordinates(Location localisation) {
        Coordinates mCoordinates = new Coordinates(localisation.getLatitude(), localisation.getLongitude());

        FirebaseDatabase.getInstance("https://appdm-fb9c5-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("DangerLocalisation")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(mCoordinates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Coordonnées dans la database !",
                                Toast.LENGTH_LONG).show();

                    } else {
                        //If sign in fails, display a message to the user.

                        Toast.makeText(Localisation.this, "Impossible d'ajouter les coordonnées à la database",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*-----
    Envoi du message à tous les contacts -> si le numéro entré est correct
    -----*/
    private void vCreateSendMessage(String address) {

        NewContact[] arr = peopleList.toArray(new NewContact[peopleList.size()]);
        SmsManager smsManager = SmsManager.getDefault();
        for (NewContact s : arr) {
            smsManager.sendTextMessage(s.getTelephone(), null, "JE SUIS EN DANGER !!! MA LOCALISATION : ", null, null);
            smsManager.sendTextMessage(s.getTelephone(), null, address, null, null);
        }
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();
    }

    /*-----
    Load data locally from firebase
    -----*/

    private void vCreateLoadData() {
        Query query = FirebaseDatabase.getInstance("https://appdm-fb9c5-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Contact/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .orderByKey();
        query.addListenerForSingleValueEvent(queryValueListener);


    }

    ValueEventListener queryValueListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {
                Log.i(TAG, Objects.requireNonNull(dataSnapshot.getValue()).toString());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    NewContact newContact = snapshot.getValue(NewContact.class);

                    peopleList.add(newContact);
                    if (peopleList == null) {
                        peopleList = new ArrayList<>();
                    }
                }
                //adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


}