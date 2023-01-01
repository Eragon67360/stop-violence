package com.example.applicationdm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;

import android.os.Bundle;
import android.text.Html;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
/*----------------------------------------
*
*Permet d'afficher les crédits : remerciements, créateurs, etc...
*
---------------------------------------- */
public class Credits extends AppCompatActivity {

    private Animation scrollAnim;
    private ConstraintLayout mConstraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        initActivity();

        scrollAnim = AnimationUtils.loadAnimation(Credits.this, R.anim.bottom_large);

        mConstraintLayout.setAnimation(scrollAnim);

    }

    private void initActivity() {
        mConstraintLayout = findViewById(R.id.credits_constraint_layout);
    }
}