package com.algonquincollege.fan00036.itunesmovietrailers;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.algonquincollege.fan00036.itunesmovietrailers.Constants.LOG_TAG;

/**
 * An activity representing a single movie's link detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of movies
 * in a {@link MovieListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link LinkFragment}.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 */
public class LinkActivity extends AppCompatActivity {
    // Class (static) variables

    // Full-qualified name of the movie file, stored on the device.
    private static final String MOVIE_FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
            + "/iTunesMovieTrailer.m4v";

    // Instance variables.
    private VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO replace
        //setContentView(R.layout.activity_link);
        setContentView(R.layout.activity_video_view);

        // Reference the <VideoView> object.
        // Displays the MediaController to stream the movie at its link.
        video = (VideoView) findViewById(R.id.video_view);

        // Get the bundle of extras that was sent to this activity
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ) {
            String movieLink = bundle.getString(LinkFragment.ARG_LINK);

            Log.i( LOG_TAG, "SAVING movie @link: " + movieLink );
            new SaveMovieLinkAsyncTask( getApplicationContext() )
                    //TODO: known test cases
                    //.execute( "http://bffmedia.com/bigbunny.mp4" );
                    //.execute("http://movietrailers.apple.com/ca_movies/mongrel/rams/rams-ca-tlr1_i320.m4v");
                    //.execute( "http://movietrailers.apple.com/movies/independent/criminalactivities/criminalactivities-clp_h480p.mov");
                    .execute( movieLink );

            Log.i( LOG_TAG, "PLAYING movie: " + MOVIE_FILE);
            new PlayMovieFileAsyncTask()
                    .execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, MovieListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Play the movie named: MOVIE_FILE
     *
     * AsyncTask's generic types
     * -------------------------
     * The three types used by an asynchronous task are the following:
     *
     * 1. Params, the type of the parameters sent to the task upon execution.
     * 2. Progress, the type of the progress units published during the background computation.
     * 3. Result, the type of the result of the background computation.
     *
     * The 4 steps
     * -----------
     * When an asynchronous task is executed, the task goes through 4 steps:
     *
     * 1. onPreExecute(), invoked on the UI thread before the task is executed. This step is
     *    normally used to setup the task, for instance by showing a progress bar in the UI.
     * 2. doInBackground(Params...), invoked on the background thread immediately after
     *    onPreExecute() finishes executing. This step is used to perform background computation that
     *    can take a long time. The parameters of the asynchronous task are passed to this step.
     *    The result of the computation must be returned by this step and will be passed back to the
     *    last step. This step can also use publishProgress(Progress...) to publish one or more units
     *    of progress. These values are published on the UI thread, in the
     *    onProgressUpdate(Progress...) step.
     * 3. onProgressUpdate(Progress...), invoked on the UI thread after a call to
     *    publishProgress(Progress...). The timing of the execution is undefined. This method is used
     *    to display any form of progress in the user interface while the background computation is
     *    still executing. For instance, it can be used to animate a progress bar or show logs in a
     *    text field.
     * 4. onPostExecute(Result), invoked on the UI thread after the background computation finishes.
     *    The result of the background computation is passed to this step as a parameter.
     *
     * Reference:
     *   http://www.oodlestechnologies.com/blogs/Working-with-MediaController,--VideoView-in-Android
     */
    private class PlayMovieFileAsyncTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog( LinkActivity.this );
            dialog.setMessage( "Loading, Please Wait..." );
            dialog.setCancelable( true );
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... params) {
            try {
                MediaController media = new MediaController( LinkActivity.this );
                video.setMediaController(media);
                media.setPrevNextListeners(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // next button clicked
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                media.show(10000);

                video.setVideoPath(MOVIE_FILE);
                video.requestFocus();
                video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer arg0) {
                        video.start();
                        dialog.dismiss();
                    }
                });
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                publishProgress();
            } catch ( Exception e ) {
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Open the movie @sURL, read the data, and save to MOVIE_FILE
     *
     * Reference:
     *   http://www.oodlestechnologies.com/blogs/Working-with-MediaController,--VideoView-in-Android
     */
    private class SaveMovieLinkAsyncTask extends AsyncTask<String, Integer, String> {
        private Context               context;
        private ProgressDialog        dialog;
        private PowerManager.WakeLock mWakeLock;

        public SaveMovieLinkAsyncTask( Context context ) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            dialog = new ProgressDialog( LinkActivity.this );
            dialog.setMessage( "Loading, Please Wait..." );
            dialog.setCancelable( true );
            dialog.show();
        }

        @Override
        protected String doInBackground( String... sUrl ) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                //TODO
                // Setting the UA for the win :)
                // To solve, I used 2 tools:
                // 1) Google's Developer Tools
                // 2) Wireshark
                connection.setRequestProperty( "User-Agent", "QuickTime/7.6.9" );
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                //output = new FileOutputStream( "/sdcard/iTunesMovieTrailer.m4v" );
                output = new FileOutputStream(MOVIE_FILE);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                Log.i( LOG_TAG, "TOTAL BYTES WRITTEN: " + "" + total );
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            dialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "Movie downloaded", Toast.LENGTH_SHORT).show();
        }
    }
}
