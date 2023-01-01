package com.example.applicationdm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;

public class PopUpNewActivity extends AppCompatActivity {

    private EditText mNomEditText;
    private EditText mTelephoneEditText;
    private ProgressBar mProgressBar;

    private ImageButton mAjouterButton;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String NAME = "name";
    private static final String TELEPHONE = "telephone";

    private static ArrayList<NewContact> peopleList;
    private NewContact nouveauContact = new NewContact();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_new);

        peopleList = new ArrayList<>();


        initActivity();

        vCreateOnClickAddContact();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

    }


    /*-----
    Click to add a new contact
     -----*/
    private void vCreateOnClickAddContact() {
        mTelephoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Check if the user wrote something
                mAjouterButton.setEnabled(!s.toString().isEmpty());
            }
        });

        mAjouterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vCreateSaveData();
                Intent mContactActivityIntent = new Intent(PopUpNewActivity.this, Contact.class);
                startActivity(mContactActivityIntent);
            }
        });
    }

    /*-----
    Save data locally in shared preferences
    -----*/
    private void vCreateSaveData() {
        String nom = mNomEditText.getText().toString().trim();
        String telephone = mTelephoneEditText.getText().toString().trim();

        //NewContact newContact = new NewContact(nom, telephone);
/*
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        peopleList.add(newContact);

        Gson gson = new Gson();
        String json = gson.toJson(peopleList);

        editor.putString(NAME, json);

        editor.apply();
*/
        //peopleList.add(new NewContact(nom, telephone));

        nouveauContact = new NewContact(nom, telephone);
        FirebaseDatabase.getInstance("https://appdm-fb9c5-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Contact")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .push().setValue(nouveauContact).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Contact ajouté dans la database !",
                            Toast.LENGTH_LONG).show();

                    overridePendingTransition(R.anim.fade_in, R.anim.move);
                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(PopUpNewActivity.this, "Impossible d'ajouter le contact à la database",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void initActivity() {
        mNomEditText = findViewById(R.id.newcontact_edittext_nom);
        mTelephoneEditText = findViewById(R.id.newcontact_edittext_phone);
        mAjouterButton = findViewById(R.id.newcontact_imagebutton_ajouter);
        mAjouterButton.setEnabled(false);
    }

}
