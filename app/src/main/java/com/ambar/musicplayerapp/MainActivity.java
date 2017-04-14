package com.ambar.musicplayerapp;


import android.media.MediaPlayer;
import android.net.Uri;

import android.support.design.widget.TabLayout;

import android.support.v7.app.AppCompatActivity;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ambar.musicplayerapp.fragment.FragmentArtist;
import com.ambar.musicplayerapp.fragment.FragmentSongs;
import com.ambar.musicplayerapp.fragment.FragmentAlbums;
import java.io.File;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements FragmentSongs.OnFragmentReady{



     SectionsPagerAdapter mSectionsPagerAdapter;
     ViewPager mViewPager;
    ListView listView;
    ImageButton stop;
    ArrayList<Songs> songList = new ArrayList<>();
    boolean playing = false;
    int current = -1;
    int seektime = 0;
    MediaPlayer player;
    FragmentSongs fragmentSongs=null;
    public static final String TAG = "SONG";
    ImageView currentSongImage;
    TextView currentSong, currentArtist;
    SeekBar seekBar;
    android.os.Handler mhandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = (ListView) findViewById(R.id.listView);
        currentSongImage = (ImageView)findViewById(R.id.currentSongImage);
        currentSong = (TextView)findViewById(R.id.currentSong);
        currentArtist = (TextView)findViewById(R.id.currentArtist);
        seekBar = (SeekBar)findViewById(R.id.seekbar);



        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mhandler = new android.os.Handler();



    }






    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            if (position == 0) {
                if(fragmentSongs==null) {
                    fragmentSongs = FragmentSongs.newInstance();
                }
                    fragmentSongs = new FragmentSongs();
                    fragmentSongs.setOnFragmentReady(new FragmentSongs.OnFragmentReady() {
                        @Override
                        public void ready() {
                            fragmentSongs.getSongList();
                        }
                    });

                    fragmentSongs.setOnSongClickListener(new FragmentSongs.OnSongClickListener() {
                        @Override
                        public void onSongClick(int pos) {
                            seektime=0;
                            current=pos;
                            playSong(songList.get(current).getData(), current, 0);
                        }
                    });

                return fragmentSongs;
            }
            if (position == 1) {
                fragment = new FragmentAlbums();
            }
            if (position == 2) {
                fragment  = new FragmentArtist();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SONGS";
                case 1:
                    return "ALBUMS";
                case 2:
                    return "ARTIST";
            }
            return null;
        }
    }

    void playSong(String data, int p, int seek) {

        //   Toast.makeText(getApplicationContext(),s.getDATA().toString(),Toast.LENGTH_LONG).show();
        try {
            if (playing)
                player.release();

            player = MediaPlayer.create(MainActivity.this, Uri.fromFile(new File(data)));

            playing = true;

            player.prepareAsync();
            player.seekTo(seek);

            player.start();
            current = p;
            stop.setImageResource(R.drawable.pause);
        } catch (IllegalStateException e) {
            player.seekTo(seek);

            player.start();
            current = p;
            stop.setImageResource(R.drawable.pause);
        }
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop.setImageResource(R.drawable.play);

            }
        });
    }




    @Override
    public void ready() {
        fragmentSongs.ArrayListReceive(songList);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
