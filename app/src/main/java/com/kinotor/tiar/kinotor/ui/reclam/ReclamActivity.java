package com.kinotor.tiar.kinotor.ui.reclam;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.Statics;

public class ReclamActivity extends AppCompatActivity {
    private static String tagUrl = "";
    private boolean read = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_reclam);

        TextView source = findViewById(R.id.reklam_name);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("Source") != null) {
                if (bundle.getString("Source").toLowerCase().contains("filmix")) {
                    tagUrl = getString(R.string.ad_tag_url);
                    source.setText("Реклама от " + bundle.getString("Source"));
                } else {
                    tagUrl = getString(R.string.main_tag_url);
                    source.setText("Реклама от KinoTor");
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (read || tagUrl.equals(getString(R.string.ad_tag_url))) {
            finish();
        } else {
            read = true;
            Toast.makeText(this, "Нажмите назад для закрытия", Toast.LENGTH_SHORT).show();
        }
    }

    public static class VideoFragment extends Fragment implements AdEvent.AdEventListener, AdErrorEvent.AdErrorListener {

        // The video player.
        private SampleVideoPlayer mVideoPlayer;

        // The container for the ad's UI.
        private ViewGroup mAdUiContainer;

        // Factory class for creating SDK objects.
        private ImaSdkFactory mSdkFactory;

        // The AdsLoader instance exposes the requestAds method.
        private AdsLoader mAdsLoader;

        // AdsManager exposes methods to control ad playback and listen to ad events.
        private AdsManager mAdsManager;

        // Whether an ad is displayed.
        private boolean mIsAdDisplayed;

        // The play button to trigger the ad request.
//        private View mPlayButton;

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Create an AdsLoader.
            mSdkFactory = ImaSdkFactory.getInstance();
            mAdsLoader = mSdkFactory.createAdsLoader(this.getContext());
            // Add listeners for when ads are loaded and for errors.
            mAdsLoader.addAdErrorListener(this);
            mAdsLoader.addAdsLoadedListener(adsManagerLoadedEvent -> {
                // Ads were successfully loaded, so get the AdsManager instance. AdsManager has
                // events for ad playback and errors.
                mAdsManager = adsManagerLoadedEvent.getAdsManager();

                // Attach event and error event listeners.
                mAdsManager.addAdErrorListener(VideoFragment.this);
                mAdsManager.addAdEventListener(VideoFragment.this);
                mAdsManager.init();
            });

            mVideoPlayer.addVideoCompletedListener(() -> {
                // Handle completed event for playing post-rolls.
                if (mAdsLoader != null) {
                    mAdsLoader.contentComplete();
                } else complate();
            });


            requestAds(tagUrl);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_reclam_video, container, false);

            mAdUiContainer = rootView.findViewById(R.id.videoPlayerWithAdPlayback);
            mVideoPlayer = rootView.findViewById(R.id.sampleVideoPlayer);

            return rootView;
        }

        /**
         * Request video ads from the given VAST ad tag.
         * @param adTagUrl URL of the ad's VAST XML
         */
        private void requestAds(String adTagUrl) {
            AdDisplayContainer adDisplayContainer = mSdkFactory.createAdDisplayContainer();
            adDisplayContainer.setAdContainer(mAdUiContainer);

            // Create the ads request.
            AdsRequest request = mSdkFactory.createAdsRequest();
            request.setAdTagUrl(adTagUrl);
            request.setAdDisplayContainer(adDisplayContainer);
            request.setContentProgressProvider(() -> {
                if (mIsAdDisplayed || mVideoPlayer == null || mVideoPlayer.getDuration() <= 0) {
                    return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                }
                return new VideoProgressUpdate(mVideoPlayer.getCurrentPosition(),
                        mVideoPlayer.getDuration());
            });

            // Request the ad. After the ad is loaded, onAdsManagerLoaded() will be called.
            mAdsLoader.requestAds(request);
        }

        @Override
        public void onAdEvent(AdEvent adEvent) {
            Log.e("reklam", "Event: " + adEvent.getType());

            // These are the suggested event types to handle. For full list of all ad event
            // types, see the documentation for AdEvent.AdEventType.
            switch (adEvent.getType()) {
                case LOADED:
                    // AdEventType.LOADED will be fired when ads are ready to be played.
                    // AdsManager.start() begins ad playback. This method is ignored for VMAP or
                    // ad rules playlists, as the SDK will automatically start executing the
                    // playlist.
                    mAdsManager.start();
                    break;
                case CONTENT_PAUSE_REQUESTED:
                    // AdEventType.CONTENT_PAUSE_REQUESTED is fired immediately before a video
                    // ad is played.
                    mIsAdDisplayed = true;
                    mVideoPlayer.pause();
                    break;
                case CONTENT_RESUME_REQUESTED:
                    // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is completed
                    // and you should start playing your content.
                    mIsAdDisplayed = false;
                    mVideoPlayer.play();
                    break;
                case ALL_ADS_COMPLETED:
                    if (mAdsManager != null) {
                        mAdsManager.destroy();
                        mAdsManager = null;
                        complate();
                    }
                    break;
                default:
                    break;
            }
        }

        private void complate() {
            if (getActivity() != null) {
                Statics.adbWached = true;
                getActivity().finish();
            }
        }

        @Override
        public void onAdError(AdErrorEvent adErrorEvent) {
            Log.e("reklam", "Ad Error: " + adErrorEvent.getError().getMessage());
            complate();
        }

        @Override
        public void onResume() {
            if (mAdsManager != null && mIsAdDisplayed) {
                mAdsManager.resume();
            } else {
                mVideoPlayer.play();
            }
            super.onResume();
        }
        @Override
        public void onPause() {
            if (mAdsManager != null && mIsAdDisplayed) {
                mAdsManager.pause();
            } else {
                mVideoPlayer.pause();
            }
            super.onPause();
        }
    }
}
