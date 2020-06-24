package coe.thbs.com.videostreaming;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private ImageButton ibPlay, ibPause;
    private ImageView ibFullScreen;
    private TextView tvDownload;
    private ProgressiveMediaSource progressiveMediaSource;
    //    private String videoURL ="http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4?source=post_page---------------------------";
    private long currentPosition;
    private String videoURL = "https://developer.android.com/images/pip.mp4";
    private long playbackPosition;
    private int currentWindow;
    private Dialog mFullScreenDialog;
    private FrameLayout main_media_frame, mFullScreenButton;
    private boolean mExoPlayerFullscreen = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPlayer();
        setListeners();
        initFullscreenDialog();
    }

    private void setListeners() {
//    ibPlay.setOnClickListener(this);
//    ibPause.setOnClickListener(this);
        ibFullScreen.setOnClickListener(this);
    }


    private void initView() {
        playerView = findViewById(R.id.exoplayerView);
        ibPause = findViewById(R.id.exo_pause);
//        ibPlay = findViewById(R.id.exo_play);
        ibFullScreen = findViewById(R.id.exo_fullscreen_icon);
        tvDownload = findViewById(R.id.tv_download);
        main_media_frame =findViewById(R.id.main_media_frame);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPlayer();
    }

    @Override
    protected void onPause() {
        releasePlayer();
        super.onPause();
    }

    @Override
    protected void onStop() {
        releasePlayer();
        super.onStop();
    }

    private void initPlayer(){
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultRenderersFactory(this),
                new DefaultTrackSelector());
        playerView.setPlayer(player);
    }

    private void startPlayer() {
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "player"));
        progressiveMediaSource = new ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(Uri.parse(videoURL));

        boolean isRunning = currentPosition != 0;
        player.prepare(progressiveMediaSource, isRunning, false);
        player.setPlayWhenReady(true);
        if (isRunning) {
            player.seekTo(currentPosition);
        }
    }

    private void releasePlayer() {
        if (player == null) {
            return;
        }
        currentPosition = player.getCurrentPosition();
        player.release();
        player = null;

    }

    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }
    private void closeFullscreenDialog() {

        ((ViewGroup) playerView.getParent()).removeView(playerView);
        main_media_frame.addView(playerView);
         mExoPlayerFullscreen = false;
        ibFullScreen.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_fullscreen_expand));
        mFullScreenDialog.dismiss();
    }


    private void openFullscreenDialog() {

        ((ViewGroup) playerView.getParent()).removeView(playerView);
        mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ibFullScreen.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()){
          case R.id.exo_fullscreen_icon:
              if (!mExoPlayerFullscreen)
                  openFullscreenDialog();
              else
                  closeFullscreenDialog();

              break;
     }
    }
}

