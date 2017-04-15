package com.ambar.musicplayerapp;


import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;

import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;

import android.support.v7.app.AppCompatActivity;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ambar.musicplayerapp.fragment.FragmentArtist;
import com.ambar.musicplayerapp.fragment.FragmentSongs;
import com.ambar.musicplayerapp.fragment.FragmentAlbums;
import com.squareup.picasso.Picasso;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity{



     SectionsPagerAdapter mSectionsPagerAdapter;
     ViewPager mViewPager;
    ListView listView;
    ImageButton next, previous, stop;
    ArrayList<Songs> songList = null;
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
    AsyncTask<Void,Void,ArrayList<Songs>> songFetch=null;
    Runnable runnable;
    Cursor albumCursor, musicCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentSongImage = (ImageView)findViewById(R.id.currentSongImage);
        currentSong = (TextView)findViewById(R.id.currentSong);
        currentArtist = (TextView)findViewById(R.id.currentArtist);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        next = (ImageButton) findViewById(R.id.next);
        stop = (ImageButton) findViewById(R.id.stop);
        previous = (ImageButton)findViewById(R.id.previous);



        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mhandler = new android.os.Handler();




        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (playing) {

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (player ==null) {
                    seektime=seekBar.getProgress();
                    player.seekTo(seekBar.getProgress());
                }
            }
        });



        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
         musicCursor = musicResolver.query(musicUri,null,null,null,null);
         albumCursor = musicResolver.query(albumUri, null,null,null,null);

        songFetch=new AsyncTask<Void, Void, ArrayList<Songs>>() {

            @Override
            protected ArrayList<Songs> doInBackground(Void... params) {

                int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

                int songImage = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
                int imagePath = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
                int artId = albumCursor.getColumnIndex(MediaStore.Audio.Albums._ID);
                ArrayList<Songs> s=new ArrayList<>();

                while (musicCursor.moveToNext()) {
                    String title = musicCursor.getString(titleColumn);
                    Integer id = musicCursor.getInt(idColumn);
                    String album = musicCursor.getString(albumColumn);
                    String data = musicCursor.getString(dataColumn);
                    String artist = musicCursor.getString(artistColumn);
                    s.add(new Songs(data, title, id, album, null, null, artist));
                }

                while (albumCursor.moveToNext()) {
                    String songimage = albumCursor.getString(songImage);
                    String imagepath = albumCursor.getString(imagePath);
                    for (int i = 0; i < s.size(); i++) {
                        if (s.get(i).getAlbum().equals(imagepath))
                            s.get(i).setArtPath(songimage);
                    }
                }
                return s;
            }

            @Override
            protected void onPostExecute(ArrayList<Songs> songs) {
                super.onPostExecute(songs);
                songList=new ArrayList<>();
                songList=songs;
            }
        };
        songFetch.execute();

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    seektime = player.getCurrentPosition();
                    player.pause();
                    playing = false;
                    stop.setImageResource(R.drawable.play);
                }
                else if (songList.size()!=0){
                    if (current!=-1){
                        // Toast.makeText(MainActivity.this,"YO",Toast.LENGTH_LONG).show();
                        player.start();
                        playing=true;
                        playCycle();
                        updatePlayerDetail(current);
                        // playSong(listSong.get(current).getDATA(),current,seektime);
                        stop.setImageResource(R.drawable.pause);
                    }else {
                        current=0;
                        updatePlayerDetail(current);
                        playSong(songList.get(0).getData(),0,0);
                        stop.setImageResource(R.drawable.pause);
                    }
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    player.stop();
                    playing = false;
                    seekBar.setProgress(0);
                }

                if (current==songList.size()){
                    //Do nothing
                }
                else if (current != -1) {
                    current++;
                    updatePlayerDetail(current);
                    playSong(songList.get(current).getData(), current, 0);
                }
                else {
                    updatePlayerDetail(0);
                    playSong(songList.get(0).getData(), 0, 0);
                }
            }
        });

        previous.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seektime=0;
                if (playing) {
                    player.stop();
                    playing = false;
                    seekBar.setProgress(0);
                }

                if (current == -1 || current == 0) {
                    //Do nothing
                } else  {
                    current--;
                    updatePlayerDetail(current);
                    playSong(songList.get(current).getData(), current, 0);
                }

            }
        });

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
                            fragmentSongs.ArrayListReceive(songList);
                        }
                    });

                    fragmentSongs.setOnSongClickListener(new FragmentSongs.OnSongClickListener() {
                        @Override
                        public void onSongClick(int pos) {
                            seektime=0;
                            current=pos;
                            updatePlayerDetail(current);
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


    //Play Song on the start of the app.
    void playSong(String data, int p, int seek) {

        //   Toast.makeText(getApplicationContext(),s.getDATA().toString(),Toast.LENGTH_LONG).show();
        try {
            if (playing)
                player.release();

            player = MediaPlayer.create(MainActivity.this, Uri.fromFile(new File(data)));

            playing = true;

            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    seekBar.setMax(mp.getDuration());
                    seekBar.setProgress(0);
                    playCycle();
                }
            });
            player.prepareAsync();
            player.seekTo(seek);

            if(player.getDuration()!=-1){
                seekBar.setProgress(seek);
            }else {
                seekBar.setProgress(0);

            }

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


    //Update the player details on the bottom cardview.
    public void updatePlayerDetail (int pos){
        Songs songs = songList.get(pos);
        currentSong.setText(songs.getTitle());
        currentArtist.setText(songs.getArtist());

        try {
            Picasso.with(MainActivity.this).load(new File(songs.getArtPath())).fit().into(currentSongImage);
        }catch (Exception e) {
            Picasso.with(MainActivity.this).load(R.drawable.music4).fit().into(currentSongImage);
        }
    }




    void playCycle(){
        seekBar.setProgress(player.getCurrentPosition());
        if(playing){
            runnable=new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
            mhandler.postDelayed(runnable,1000);
        }

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
