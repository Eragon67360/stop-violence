package com.example.applicationdm;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class Contact extends AppCompatActivity {

    private static final boolean enableFast = false;

    //Variable declaration

    private ImageButton mBackImageButton;
    private FloatingActionButton mAjouterFloatingButton;
    private FloatingActionButton mEffacerContactsButton;
    private ListView mContactsListView;

    private ContactListAdapter adapter;
    private static ArrayList<NewContact> peopleList;
    //private ContactListAdapter adapter = new ContactListAdapter(Contact.this, R.layout.list_adapter, PopUpNewActivity.peopleList);

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String NAME = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        peopleList = new ArrayList<>();
        adapter = new ContactListAdapter(Contact.this, R.layout.list_adapter, peopleList);

        initActivity();
        vCreateOnClickAddContact();

        vCreateLoadData();
        vCreateOnClickDeleteAllContacts();
        vCreateOnClickBackButton();
        vCreateUpdateList();
    }


    /*-----
    Add a contact -> create a new contact
    -----*/
    private void vCreateOnClickAddContact() {
        mAjouterFloatingButton.setOnClickListener(view -> {
            Intent mNewContactActivityIntent = new Intent(Contact.this, PopUpNewActivity.class);
            startActivity(mNewContactActivityIntent);
        });
    }

    /*-----
    Return button -> go to the preview ativity
    -----*/
    private void vCreateOnClickBackButton() {
        mBackImageButton.setOnClickListener(v -> {
            Intent mBackToLocalisationActivityIntent = new Intent(Contact.this, Localisation.class);
            startActivity(mBackToLocalisationActivityIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.move);
        });
    }
    /*-----
    Click to delete all contacts ->AlertDialog box
     -----*/
    public void vCreateOnClickDeleteAllContacts() {
        mEffacerContactsButton.setOnClickListener(view -> {
            AlertDialog diaBox = vCreateAskOption();
            diaBox.show();
        });
    }

    private void vCreateLoadData() {
        if (enableFast) {
            vCreateSureLoadData();
        } else {
            vCreateTestLoadData();
        }
    }

    /*-----
    Load data locally from shared preferences
    -----*/
    private void vCreateSureLoadData() {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(NAME, null);

        Type type = new TypeToken<ArrayList<NewContact>>() {
        }.getType();

        peopleList = gson.fromJson(json, type);
        if (peopleList == null) {
            peopleList = new ArrayList<>();
        }

    }

    private void vCreateTestLoadData() {
        Query query = FirebaseDatabase.getInstance("https://appdm-fb9c5-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Contact/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .orderByKey();
        query.addListenerForSingleValueEvent(queryValueListener);

    }

    ValueEventListener queryValueListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            peopleList.clear();
            if (dataSnapshot.exists()) {
                Log.i(TAG, Objects.requireNonNull(dataSnapshot.getValue()).toString());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    NewContact newContact = snapshot.getValue(NewContact.class);

                    peopleList.add(newContact);
                    if (peopleList == null) {
                        peopleList = new ArrayList<>();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.i(TAG, "Error ");
        }
    };

    /*-----
    Clear contact list
    -----*/
    public void vCreateClearData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        sharedPreferences.edit().remove(NAME).apply();

        FirebaseDatabase.getInstance("https://appdm-fb9c5-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Contact")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .removeValue();

        peopleList.clear();
        vCreateUpdateList();
    }

    private void vCreateUpdateList() {

        mContactsListView.setAdapter(adapter);
    }

    /*-----
    Options for the AlertDialog box
    -----*/
    private AlertDialog vCreateAskOption() {
        return new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Effacer contacts")
                .setMessage("Attention : Cette action effacera tous les contacts ! Voulez-vous continuer?")
                .setIcon(R.drawable.logo04)

                .setPositiveButton("Effacer", (dialog, whichButton) -> {
                    vCreateClearData();
                    dialog.dismiss();
                })
                .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
                .create();
    }

    /*-----
    Initialize states
    -----*/
    private void initActivity() {
        mBackImageButton = findViewById(R.id.main_image_backbutton);
        mAjouterFloatingButton = findViewById(R.id.contact_button_ajouter);

        mEffacerContactsButton = findViewById(R.id.contact_button_supprimercontacts);

        mContactsListView = findViewById(R.id.contact_listview_person);

    }
}

