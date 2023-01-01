package com.example.applicationdm;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class NewAccount extends AppCompatActivity {

    private EditText mNomEditText;
    private EditText mPrenomEditText;
    private EditText mAgeEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mTelephoneEditText;

    private Button mInscriptionButton;
    private ImageButton mRetourButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        Window window = NewAccount.this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(NewAccount.this, R.color.gray));

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        initActivity();
        vCreateOnClickInscription();
        vCreateOnClickBackButton();

    }


    /*-----
    Au click, inscrire le nouvel utilisateur
    Enregistrer les données sur le Cloud
    -----*/
    private void vCreateOnClickInscription() {
        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mInscriptionButton.setEnabled(!toString().isEmpty());
            }

        });

        mInscriptionButton.setOnClickListener(v -> {

            String nom = mNomEditText.getText().toString().trim();
            String prenom = mPrenomEditText.getText().toString().trim();
            String age = mAgeEditText.getText().toString().trim();
            String email = mEmailEditText.getText().toString().trim();
            String motdepasse = mPasswordEditText.getText().toString().trim();
            String telephone = mTelephoneEditText.getText().toString().trim();

            Data mData = new Data(nom, prenom, age, email, motdepasse, telephone);

            mAuth.createUserWithEmailAndPassword(email, motdepasse)
                    .addOnCompleteListener(NewAccount.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance("https://appdm-fb9c5-default-rtdb.europe-west1.firebasedatabase.app/").getReference("UserData")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(mData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Bienvenue " + mData.getPrenom() + " !",
                                                Toast.LENGTH_LONG).show();
                                        Intent mBackToAccueilActivityIntent = new Intent(NewAccount.this, Accueil.class);
                                        startActivity(mBackToAccueilActivityIntent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.move);
                                    } else {
                                        // If sign in fails, display a message to the user.

                                        Toast.makeText(NewAccount.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            FirebaseDatabase.getInstance("https://appdm-fb9c5-default-rtdb.europe-west1.firebasedatabase.app/").getReference("UserID")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(NewAccount.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }

    /*-----
    Return button -> go to the preview activity
    -----*/
    private void vCreateOnClickBackButton() {
        mRetourButton.setOnClickListener(v -> {
            Intent mBackToAccueilActivityIntent = new Intent(NewAccount.this, Accueil.class);
            startActivity(mBackToAccueilActivityIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.move);
        });
    }

    /*-----
    Mettre à jour l'interface utilisateur
    -----*/
    public void vCreateUpdateUI(FirebaseUser account) {

        if (account != null) {
            Toast.makeText(this, "New account created successfully", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "You Didnt signed in", Toast.LENGTH_LONG).show();
        }

    }

    /*-----
    Initialiser les instances
    -----*/
    private void initActivity() {
        mNomEditText = findViewById(R.id.newaccount_edittext_name);
        mPrenomEditText = findViewById(R.id.newaccount_edittext_prenom);
        mAgeEditText = findViewById(R.id.newaccount_edittext_age);
        mEmailEditText = findViewById(R.id.newaccount_edittext_email);

        mPasswordEditText = findViewById(R.id.newaccount_edittext_password);
        mTelephoneEditText = findViewById(R.id.newaccount_edittext_phone);
        mInscriptionButton = findViewById(R.id.newaccount_button_enregistrer);

        mRetourButton = findViewById(R.id.newaccount_image_backbutton);

        mInscriptionButton.setEnabled(false);

    }

}