package petelap.radarblast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class AdsManager {
    private InterstitialAd mInterstitialAd;
    private boolean loadComplete;

    public AdsManager() {
        String appID = BuildConfig.appID;
        String unitID = BuildConfig.unitID;

        // Initialize MobileAds
        MobileAds.initialize(Constants.CONTEXT, appID);

        // Initialize InterstitialAd
        mInterstitialAd = new InterstitialAd(Constants.CONTEXT);
        mInterstitialAd.setAdUnitId(unitID);

        // Load Ad
        loadComplete = false;
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        Constants.INIT_TIME = System.currentTimeMillis();

        // Set AdListeners
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                loadComplete = true;
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                loadComplete = false;
                Constants.INIT_TIME = System.currentTimeMillis();
                Constants.GAME_STATUS = "GAMELOOP";
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    public void showAd() {
        if (loadComplete) {
            mInterstitialAd.show();
        } else {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

}
