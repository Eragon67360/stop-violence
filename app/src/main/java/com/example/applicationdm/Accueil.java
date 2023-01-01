package com.example.applicationdm;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Accueil extends AppCompatActivity {

    //Variables declaration
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private FloatingActionButton mConnectionButton;
    private Button mInscriptionButton;
    private Button mOubliButton;
    private FirebaseAuth mAuth;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://appdm-fb9c5-default-rtdb.europe-west1.firebasedatabase.app");
    DatabaseReference myRef = database.getReference("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = Accueil.this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(Accueil.this, R.color.black));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_accueil);

        initActivity();
        vCreateOnClickSeConnecter();
        vCreateOnClickSInscrire();
        vCreateOnClickOubli();

    }

    /*-----
    Bouton de connexion
    -----*/
    private void vCreateOnClickSeConnecter() {
        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Check if the user wrote something
                mConnectionButton.setEnabled(!s.toString().isEmpty());
            }
        });
        mConnectionButton.setOnClickListener(v -> mAuth.signInWithEmailAndPassword(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString())
                .addOnCompleteListener(Accueil.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        vCreateUpdateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(Accueil.this, "E-Mail ou Mot de Passe invalide",
                                Toast.LENGTH_SHORT).show();
                        vCreateShowError();
                        vCreateUpdateUI(null);
                    }
                }));
    }

    /*-----
    Bouton d'inscription
    -----*/
    private void vCreateOnClickSInscrire() {
        mInscriptionButton.setOnClickListener(v -> {
            Intent NewAccountActivityIntent = new Intent(Accueil.this, NewAccount.class);
            startActivity(NewAccountActivityIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    /*-----
    Bouton Mot de passe oublié
    -----*/
    private void vCreateOnClickOubli() {
        mOubliButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ForgotPasswordActivityIntent = new Intent(Accueil.this, ResetPassword.class);
                startActivity(ForgotPasswordActivityIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }
    /*-----
    Forcer des actions au démarragede l'activité
    -----*/
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent LocalisationActivityIntent = new Intent(Accueil.this, Localisation.class);
            startActivity(LocalisationActivityIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    /*-----
    Mettre l'interface à jour
    @param FirebaseUser -> référence utilisateur à rechercher sur le Cloud
    -----*/
    public void vCreateUpdateUI(FirebaseUser account) {
        if (account != null) {
            Toast.makeText(this, "Connexion réussie", Toast.LENGTH_LONG).show();
            Intent LocalisationActivityIntent = new Intent(Accueil.this, Localisation.class);
            startActivity(LocalisationActivityIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            Toast.makeText(this, "Connexion impossible", Toast.LENGTH_LONG).show();
        }

    }

    /*-----
    Montrer l'erreur
    -----*/
    public void vCreateShowError() {

        Animation shake = AnimationUtils.loadAnimation(Accueil.this, R.anim.shake);
        mPasswordEditText.startAnimation(shake);
        mPasswordEditText.setError("Mot de passe ou E-Mail invalide");
        mPasswordEditText.setText("");
    }

    /*-----
    Initialisation des activités
    -----*/
    private void initActivity() {
        mEmailEditText = findViewById(R.id.accueil_edittext_name);
        mPasswordEditText = findViewById(R.id.accueil_edittext_password);
        mConnectionButton = findViewById(R.id.accueil_button_connexion);
        mInscriptionButton = findViewById(R.id.accueil_button_s_inscrire);
        mOubliButton = findViewById(R.id.accueil_button_oubli);

        mConnectionButton.setEnabled(false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }
}