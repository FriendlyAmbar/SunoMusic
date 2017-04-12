package com.ambar.musicplayerapp.fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ambar.musicplayerapp.R;
import com.ambar.musicplayerapp.Songs;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.ambar.musicplayerapp.MainActivity.TAG;

/**
 * Created by ambar on 12-04-2017.
 */

public class FragmentSongs extends android.support.v4.app.Fragment {

    private ListView listView;
    ArrayList<Songs> songList = new ArrayList<>();
    SongAdapter songAdapter = null;
    boolean playing = false;
    int current = -1;
    int seektime = 0;
    MediaPlayer player;
    ImageButton next, previous, stop;


    OnFragmentReady readyListener;

    public interface OnFragmentReady {
        void ready();
        void onClicked(int pos);
    }

    public void setOnFragmentReady(OnFragmentReady listener) {
        readyListener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        readyListener.ready();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragmentsongs,container,false);
        listView = (ListView)viewRoot.findViewById(R.id.listView);
        next = (ImageButton) viewRoot.findViewById(R.id.next);
        stop = (ImageButton) viewRoot.findViewById(R.id.stop);
        previous = (ImageButton) viewRoot.findViewById(R.id.previous);


        songList  = new ArrayList<>();
        getSongList();

        //Sort the song list alphabetically bt title
        Collections.sort(songList, new Comparator<Songs>() {
            @Override
            public int compare(Songs a, Songs b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });


        songAdapter = new SongAdapter(getContext(),songList);
        listView.setAdapter(songAdapter);


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    seektime = player.getCurrentPosition();
                    player.pause();
                    playing = false;
                    stop.setImageResource(R.drawable.play);
                } else if (current != -1) {
                    player.start();
                    playing = true;
                    stop.setImageResource(R.drawable.pause);
                } else if (songList.size() != 0) {
                    playSong(songList.get(0).getData(), 0, 0);
                    stop.setImageResource(R.drawable.pause);
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    player.stop();
                    playing = false;
                }

                if (current == songList.size()) {
                    //
                } else if (current != -1) {
                    playSong(songList.get(current + 1).getData(), current + 1, 0);
                } else {
                    playSong(songList.get(0).getData(), 0, 0);
                }
            }
        });

        previous.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    player.stop();
                    playing = false;
                }

                if (current == songList.size()) {

                } else if (current == -1 || current == 0) {

                } else if (current != -1) {
                    playSong(songList.get(current - 1).getData(), current - 1, 0);
                }

            }
        });

        return viewRoot;


    }

    public void getSongList() {

        ContentResolver musicResolver = getContext().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri,null,null,null,null);
        Cursor albumCursor = musicResolver.query(albumUri, null,null,null,null);


        if (musicCursor != null && musicCursor.moveToFirst()) {

            int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            int songImage = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int imagePath = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int artId = albumCursor.getColumnIndex(MediaStore.Audio.Albums._ID);

            while (musicCursor.moveToNext()) {
                String title = musicCursor.getString(titleColumn);
                Integer id = musicCursor.getInt(idColumn);
                String album = musicCursor.getString(albumColumn);
                String data = musicCursor.getString(dataColumn);
                String artist = musicCursor.getString(artistColumn);
                songList.add(new Songs(data, title, id, album, null, null, artist));
            }

            while (albumCursor.moveToNext()) {
                String songimage = albumCursor.getString(songImage);
                String imagepath = albumCursor.getString(imagePath);
                for (int i = 0; i < songList.size(); i++) {
                    if (songList.get(i).getAlbum().equals(imagepath))
                        songList.get(i).setArtPath(songimage);
                }
            }

        }
    }

    public void ArrayListReceive(ArrayList<Songs> arrayList) {
        Log.d(TAG, "ArrayListReceive: " + arrayList);
        songList = arrayList;
        songAdapter.notifyDataSetChanged();
    }



    public class SongAdapter extends BaseAdapter {


        //song list and layout
        private ArrayList<Songs> songs;
        private LayoutInflater songInf;

        //constructor
        public SongAdapter(Context c, ArrayList<Songs> theSongs) {
            songs = theSongs;
            songInf = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return songs.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final Songs s = songs.get(position);
            final int p = position;
            //map to song layout
            CardView songLay = (CardView) songInf.inflate(R.layout.songs, parent, false);
            //get title and artist views
            ImageView imageView = (ImageView) songLay.findViewById(R.id.songImage);
            TextView songView = (TextView) songLay.findViewById(R.id.song_title);
            TextView artistView = (TextView) songLay.findViewById(R.id.song_artist);
            //get song using position
            Songs currSong = songs.get(position);
            //get title and artist strings
            songView.setText(currSong.getTitle());
            artistView.setText(currSong.getArtist());

            try {
                Picasso.with(getContext()).load(new File(s.getArtPath())).fit().into(imageView);
            } catch (Exception e) {
                Log.d(TAG, "onBindViewHolder: " + e.toString());
            }

            songLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seektime = 0;
                    playSong(s.getData(), p, 0);

                }
            });

            //set position as tag
            songLay.setTag(position);
            return songLay;
        }

    }


    void playSong(String data, int p, int seek) {

        //   Toast.makeText(getApplicationContext(),s.getDATA().toString(),Toast.LENGTH_LONG).show();
        try {
            if (playing)
                player.release();

            player = MediaPlayer.create(this.getActivity(), Uri.fromFile(new File(data)));
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
}
