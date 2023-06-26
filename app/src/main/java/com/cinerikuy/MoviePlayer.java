package com.cinerikuy;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.cinerikuy.presenter.NavigationActivity;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class MoviePlayer extends Fragment{
    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    
    public static final String URL_VIDEO="https://www.shutterstock.com/shutterstock/videos/1104111131/preview/stock-footage-san-jose-del-cabo-bcs-mexico-a-large-body-of-water-next-to-a-sandy-beach.mp4";
    String URL = "https://firebasestorage.googleapis.com/v0/b/appwallpaper-75b5c.appspot.com/o/trailer%2FSpider-Man_SpiderVerso.mp4?alt=media&token=b03356fa-d69c-4b23-96c6-9b62c20fd77a";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_player, container, false);
        initExoPlayer(view);
        setFullScreen();
        hideActionBar();
        return view;
    }
    public void initExoPlayer(View view) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        playerView = view.findViewById(R.id.movie_exo_player);
        simpleExoPlayer = new SimpleExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(simpleExoPlayer);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(requireContext(),
                Util.getUserAgent(requireContext(), "appname"));
        //Obtenermos los valores
        Bundle args = getArguments();
        if (args != null) {
            String movieUrl = args.getString("movieUrl");
            MediaItem mediaItem = MediaItem.fromUri(movieUrl);
            ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            simpleExoPlayer.setMediaSource(mediaSource);
            simpleExoPlayer.prepare();
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }
    public void setFullScreen() {
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    public void hideActionBar() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
        ((NavigationActivity) requireActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}