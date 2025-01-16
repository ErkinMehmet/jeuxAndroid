package com.np.retrosnake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView; // use alt + enter to import that thing lol

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Set the layout for this activity
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        mAdView=findViewById(R.id.adViewMainActivity);
        AdRequest adRequest=new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
    public void play(View view) {
        Intent intent=new Intent(MainActivity.this,GameActivity.class);
        startActivity(intent);
        finish();
    }
}
