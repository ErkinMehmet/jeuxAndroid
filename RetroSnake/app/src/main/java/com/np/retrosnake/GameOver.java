package com.np.retrosnake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class GameOver extends AppCompatActivity {

    TextView tvScore,tvPersonalBest;
    private AdView mAdView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        AdRequest adRequest=new AdRequest.Builder().build();
        InterstitialAd.load(this, getResources().getString(R.string.interstitial_adunit_id),
                adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        interstitialAd.show(GameOver.this);

                    }
                });
        mAdView=findViewById(R.id.adViewGameOverActivity);
        mAdView.loadAd(adRequest);
        int score=getIntent().getExtras().getInt("score");
        SharedPreferences pref=getSharedPreferences("MyPref",0);
        int scoreSP=pref.getInt("scoreSP",0);
        SharedPreferences.Editor editor =pref.edit();
        if (score>scoreSP) {
            scoreSP=score;
            editor.putInt("scoreSP",scoreSP);
            editor.commit();
        }
        tvScore=findViewById(R.id.tvPoints);
        tvPersonalBest=findViewById(R.id.tvPersonalBest);
        tvScore.setText(""+score);
        tvPersonalBest.setText(""+scoreSP);

    }

    public void restart(View view) {
        Intent intent=new Intent(GameOver.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void exit(View view) {
        finish();
    }
}
