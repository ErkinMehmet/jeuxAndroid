package com.np.brickbreaker;

import android.app.Application;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;


public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "MyApp";
    private AppOpenAd appOpenAd = null;
    private InterstitialAd mInterstitialAd;
    private Activity currentActivity;
    private boolean isShowingAd = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Mobile Ads SDK
        MobileAds.initialize(this, initializationStatus -> Log.d(TAG, "Mobile Ads SDK initialized"));

        // Register activity lifecycle callbacks
        registerActivityLifecycleCallbacks(this);

        // Load the initial App Open Ad
        loadAppOpenAd();
        loadInterstitialAd();
    }
    private boolean continueLoading = true;
    private void loadInterstitialAd() {
        if (!continueLoading) {
            //Log.d(TAG, "Not continuing loading hte interstitial ad.");
            return;  // Skip loading if an ad is already being loaded
        }
        continueLoading=false;
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-9425877128344635/3775646405", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // Called when the interstitial ad is loaded
                        Log.d(TAG, "Interstitial ad loaded");
                        mInterstitialAd = interstitialAd;
                        continueLoading = true;//resets the value
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull com.google.android.gms.ads.LoadAdError loadAdError) {
                        // Called when the interstitial ad fails to load
                        Log.e(TAG, "Failed to load interstitial ad: " + loadAdError.getMessage());
                    }
                });
    }

    public void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when the ad is dismissed
                    Log.d(TAG, "Interstitial ad dismissed");
                    mInterstitialAd = null; // Reset the interstitial ad object
                    loadInterstitialAd(); // Load the next ad
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                    // Called when the ad fails to show
                    Log.e(TAG, "Interstitial ad failed to show: " + adError.getMessage());
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when the ad is shown
                    Log.d(TAG, "Interstitial ad shown");
                }
            });

            mInterstitialAd.show(currentActivity); // Show the ad
        } else {
            //Log.d(TAG, "Interstitial ad is not ready to be shown");
            //loadInterstitialAd(); // Load a new ad if one isn't available
        }
    }
    /**
     * Loads an App Open Ad.
     */

    private void loadAppOpenAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        AppOpenAd.load(
                this,
                "ca-app-pub-9425877128344635/6945030370", // Replace with your actual Ad Unit ID
                adRequest,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        Log.d(TAG, "App Open Ad loaded");
                        appOpenAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull com.google.android.gms.ads.LoadAdError loadAdError) {
                        Log.e(TAG, "Failed to load App Open Ad: " + loadAdError.getMessage());
                    }
                });
    }

    /**
     * Shows the App Open Ad if available.
     */
    public void showAppOpenAdIfAvailable() {
        if (appOpenAd != null && !isShowingAd) {
            isShowingAd = true;

            // Set up the callback for when the ad is shown and dismissed
            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when the ad is dismissed
                    Log.d(TAG, "Ad dismissed");
                    isShowingAd = false;
                    appOpenAd = null;

                    // Preload the next ad
                    loadAppOpenAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                    // Called when the ad fails to show
                    Log.e(TAG, "Ad failed to show: " + adError.getMessage());
                    isShowingAd = false;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when the ad is shown
                    Log.d(TAG, "Ad shown");
                }
            });

            // Show the ad
            appOpenAd.show(currentActivity);
        } else {
            //Log.d(TAG, "Ad is not available to show");
            //loadAppOpenAd(); // Load a new ad if one isn't available
        }
    }

    // Manage the current activity
    @Override
    public void onActivityCreated(@NonNull Activity activity, @NonNull Bundle savedInstanceState) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;

        // Optional: Show ad when activity resumes
        showAppOpenAdIfAvailable();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}
}