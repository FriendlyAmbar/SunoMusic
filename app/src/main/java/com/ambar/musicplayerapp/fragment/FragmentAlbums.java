package com.ambar.musicplayerapp.fragment;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ambar.musicplayerapp.R;
import com.ambar.musicplayerapp.Songs;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.ambar.musicplayerapp.MainActivity.TAG;

/**
 * Created by ambar on 12-04-2017.
 */

public class FragmentAlbums extends android.support.v4.app.Fragment {

    RecyclerView recyclerView;
    ArrayList<Songs> songList;
    SongAdapter songAdapter;
    ListView listView;
    int i=0,j=0;

    public FragmentAlbums() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View viewAlbum = inflater.inflate(R.layout.fragmentalbum,container,false);
        listView = (ListView) viewAlbum.findViewById(R.id.albumListView);

        songList = new ArrayList<>();
        getAlbumList();

        Collections.sort(songList, new Comparator<Songs>() {
            @Override
            public int compare(Songs a, Songs b) {
                return a.getAlbum().compareTo(b.getAlbum());
            }
        });

       songAdapter = new SongAdapter(getContext(), songList);
        listView.setAdapter(songAdapter);


        return viewAlbum;
    }



    public void getAlbumList() {

        ContentResolver musicResolver = getContext().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        Cursor albumCursor = musicResolver.query(albumUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToNext()) {
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            int songImage = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int imagePath = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int artId = albumCursor.getColumnIndex(MediaStore.Audio.Albums._ID);

            while (musicCursor.moveToNext()) {
                String album = musicCursor.getString(albumColumn);
                String artist = musicCursor.getString(artistColumn);
                songList.add(new Songs(null,null,null,album,null,null,artist));
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

        for (i = 0; i < songList.size(); i++) {
            for (j = i + 1; j < songList.size(); j++) {
                if (songList.get(i).getAlbum().equals(songList.get(j).getAlbum())) {
                    songList.remove(j);
                }
            }

        }
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
            CardView songLay = (CardView) songInf.inflate(R.layout.albums, parent, false);
            //get title and artist views
            ImageView imageview = (ImageView) songLay.findViewById(R.id.albumImageView);
            TextView songView = (TextView) songLay.findViewById(R.id.song_album);
            //get song using position
            final Songs currSong = songs.get(position);
            //get title and artist strings
            if (currSong.getAlbum()==null) {
                songView.setText(currSong.getTitle());
            }
            else {
                songView.setText(currSong.getAlbum());
            }


            try {
                Picasso.with(getContext()).load(new File(s.getArtPath())).fit().into(imageview);
            } catch (Exception e) {
                Log.d(TAG, "onBindViewHolder: " + e.toString());
            }


            songLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //seektime = 0;
                    //playSong(s.getData(), p, 0);
                    Toast.makeText(getContext(), "Album" +songs.get(position).getAlbum(),Toast.LENGTH_SHORT).show();

                }
            });

            songLay.setTag(position);
            return songLay;
        }

    }
}
