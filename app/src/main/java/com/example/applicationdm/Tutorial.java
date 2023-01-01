package com.example.applicationdm;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.slider.Slider;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Tutorial extends AppCompatActivity {

    private ArrayList<Fragment> fragments = new ArrayList<>();

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);


        viewPager = findViewById(R.id.viewpager);

        // setting up the adapter
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // add the fragments
        viewPagerAdapter.add(new Fragment1(), "");
        viewPagerAdapter.add(new Fragment2(), "");
        viewPagerAdapter.add(new Fragment3(), "");
        viewPagerAdapter.add(new Fragment4(), "");
        // Set the adapter
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager, true);
        // The Page (fragment) titles will be displayed in the
        // tabLayout hence we need to  set the page viewer
        // we use the setupWithViewPager().

    }


    // The Page (fragment) titles will be displayed in the
    // tabLayout hence we need to  set the page viewer
    // we use the setupWithViewPager().
}
