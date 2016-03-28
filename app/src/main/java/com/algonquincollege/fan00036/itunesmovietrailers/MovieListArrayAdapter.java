package com.algonquincollege.fan00036.itunesmovietrailers;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquincollege.fan00036.itunesmovietrailers.domain.MovieItem;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by helen on 2015-12-01.
 */
public class MovieListArrayAdapter extends ArrayAdapter<MovieItem> {

    private static  final SimpleDateFormat DATE_FORMAT;
    private ImageView posterView;
    static {
        DATE_FORMAT = new SimpleDateFormat("dd MM yyyy");
    }

    public MovieListArrayAdapter(Context context,ArrayList<MovieItem> movieList){
        super( context,0,movieList);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        final MovieItem movie = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item_view,parent,false);
        }

        posterView = (ImageView) convertView.findViewById(R.id.poster);
        TextView titleView = (TextView) convertView.findViewById(R.id.title);
        TextView puDateView = (TextView) convertView.findViewById(R.id.pub_date);
        titleView.setText(movie.getTitle());
        puDateView.setText(DATE_FORMAT.format(movie.getPubDate()));

        new GetPosterTask().execute(movie.getPosterLink());

        convertView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent playMovieIntent = new Intent(getContext(),LinkActivity.class);
                playMovieIntent.putExtra(LinkFragment.ARG_LINK, movie.getTheRealLink());
                getContext().startActivity(playMovieIntent);
            }
        });



        return convertView;


    }


    private class GetPosterTask extends AsyncTask<String,Void,Boolean> {
        private Drawable posterDrawable;

        protected Boolean doInBackground(String...urls){
            try {
                URL url = new URL(urls[0]);
                posterDrawable = Drawable.createFromStream(url.openStream(), "src");
            }catch (Exception e){
                return false;
            }
            return true;

        }

        protected void onPostExecute(Boolean isSuccess){
            if (isSuccess){
                posterView.setImageDrawable(posterDrawable);
            } else {
                Toast.makeText(getContext(), "Error: Could Not Download Poster", Toast.LENGTH_LONG).show();
            }


        }
    }

}