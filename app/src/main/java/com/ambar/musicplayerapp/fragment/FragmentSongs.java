package com.ambar.musicplayerapp.fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ambar.musicplayerapp.R;
import com.ambar.musicplayerapp.Songs;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static com.ambar.musicplayerapp.MainActivity.TAG;

/**
 * Created by ambar on 12-04-2017.
 */

public class FragmentSongs extends android.support.v4.app.Fragment {

    public FragmentSongs() {

    }
    static ArrayList<Songs> songList = new ArrayList<>();
    SongAdapter songAdapter = null;
    LayoutInflater inflater;
    View viewRoot=null;
    RecyclerView recyclerView;



     OnFragmentReady readyListener;

     public  interface OnFragmentReady {
        void ready();
    }

    public void setOnFragmentReady(OnFragmentReady listener) {
        readyListener = listener;
    }


     OnSongClickListener SongClickListener;

     public interface OnSongClickListener {
       void onSongClick(int pos);
    }

     public void setOnSongClickListener (OnSongClickListener listener){
        SongClickListener = listener;
    }


    public static FragmentSongs newInstance() {

        FragmentSongs fragment = new FragmentSongs();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            Toast.makeText(getContext(), ""+songList.size(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        readyListener.ready();
    }



    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        viewRoot = inflater.inflate(R.layout.fragmentsongs,container,false);
        recyclerView = (RecyclerView) viewRoot.findViewById(R.id.recyclerView);


        this.inflater=inflater;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(songAdapter=new SongAdapter());
        recyclerView.setAdapter(songAdapter=new SongAdapter());
        return viewRoot;


    }





    public class SongHolder extends RecyclerView.ViewHolder{

        View v;
        public TextView title,artist;
        ImageView imageView;
        public SongHolder(View itemView) {
            super(itemView);
            v=itemView;
            imageView=(ImageView)itemView.findViewById(R.id.songImage);
            title=(TextView)itemView.findViewById(R.id.song_title);
            artist=(TextView)itemView.findViewById(R.id.song_artist);

        }
    }


    public class SongAdapter extends RecyclerView.Adapter<SongHolder>{


        @Override
        public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=inflater.inflate(R.layout.songs,parent,false);

            return new SongHolder(v);
        }

        @Override
        public void onBindViewHolder(final SongHolder holder, final int position) {
            final Songs s=songList.get(position);
            final int p=position;
            holder.artist.setText(s.getArtist());
            holder.title.setText(s.getTitle());
            try {
                Picasso.with(getContext()).load(new File(s.getArtPath())).fit().into(holder.imageView);
            }catch(Exception e){
                Log.d(TAG, "onBindViewHolder: "+e.toString());
                Picasso.with(getContext()).load(R.drawable.music4).fit().into(holder.imageView);
            }
            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SongClickListener.onSongClick(position);
//                    RemoteViews notificationView=new RemoteViews(getPackageName(),R.layout.notification);
                    //     notificationView.setImageViewBitmap(R.id.album_art, BitmapFactory.decodeFile(coverArt));
                }
            });

        }

        @Override
        public int getItemCount() {
            return songList.size();
        }
    }


    public void ArrayListReceive(ArrayList<Songs> arrayList) {
        Log.d(TAG, "ArrayListReceive: " + arrayList);
        songList = arrayList;
        songAdapter.notifyDataSetChanged();
    }


    @Override
    public void onStop() {
        super.onStop();
    }
}
