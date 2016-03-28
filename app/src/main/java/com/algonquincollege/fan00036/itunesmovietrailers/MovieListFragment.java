package com.algonquincollege.fan00036.itunesmovietrailers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.algonquincollege.fan00036.itunesmovietrailers.domain.MovieItem;
import com.algonquincollege.fan00036.itunesmovietrailers.persistence.MovieListDataSource;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.algonquincollege.fan00036.itunesmovietrailers.Constants.LOG_TAG;

/**
 * A list fragment representing a list of Feeds. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link LinkFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 */
public class MovieListFragment extends ListFragment {

    private MovieListArrayAdapter mMovieTrailers;

//    private MenuItem mIsSort;

    private MenuItem mIsSortTitle;
    private MenuItem mSortPudDate;
    private MenuItem mUnsorted;

    private MovieListDataSource mDataSource;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String movieItemLink);
    }

    /**
     * An implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: improve UI
        // Read: http://developer.android.com/reference/android/widget/ArrayAdapter.html
        //mMovieTrailers = new ArrayAdapter<>( getActivity(),
        //android.R.layout.simple_list_item_activated_1);
        // setListAdapter( mMovieTrailers );

        new GetiTunesMovieTrailersRssFeedTask().execute();

      //sort
        setHasOptionsMenu(true);

        mDataSource = new MovieListDataSource( this.getContext( ) );
        mDataSource.open( );
    }


    public  void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
//        mIsSort = menu.findItem(R.id.action_sort);
        mIsSortTitle = menu.findItem(R.id.action_sort_title);
        mSortPudDate = menu.findItem(R.id.action_sort_pub_date);
        mUnsorted = menu.findItem(R.id.action_unsorted);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.isCheckable()){
            item.setChecked(! item.isChecked());
            new GetiTunesMovieTrailersRssFeedTask().execute();
        }

        return  super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<MovieItem> adapter = (ArrayAdapter<MovieItem>) getListAdapter();
                //TODO: delete the selected MovieItem from the ArrayAdapter and database.
                // remove selectedItem
                MovieItem mSelectedItem = adapter.getItem(position);
                adapter.remove(mSelectedItem);
                mDataSource.deleteMovieItem(mSelectedItem);
                setListAdapter(adapter);
                Toast.makeText(getContext(),mSelectedItem.getTitle() + "is removed!", Toast.LENGTH_LONG).show();

                return true;
            }
        });

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.

        mCallbacks.onItemSelected( mMovieTrailers.getItem(position).getTheRealLink() ); //getLink() );
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    private String getiTunesMovieTrailersRssFeed() throws IOException {
        InputStream in = null;
        String rssFeed = null;
        try {
            //TODO
            // don't use the CA feed
            //URL url = new URL("http://trailers.apple.com/ca/home/rss/newtrailers.rss");
            URL url = new URL("http://trailers.apple.com/trailers/home/rss/newtrailers.rss");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            Log.e(LOG_TAG, "Response: " + new String(response) );
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }

    /*
     * My implementation of class GetiTunesMovieTrailerRssFeedTask is based on the work of
     * Henrique Rocha.
     *
     * Reference:
     *     https://www.androidpit.com/java-guide-2-program-your-own-rss-reader
     *
     * @author Gerald.Hurdle@AlgonquinCollege.com
     */
    private class GetiTunesMovieTrailersRssFeedTask extends AsyncTask<Void, Void, ArrayList<MovieItem>> {

        @Override
        protected ArrayList<MovieItem> doInBackground(Void... voids) {
            ArrayList<MovieItem> result = null;
            try {
                String feed = getiTunesMovieTrailersRssFeed();
                result = parse(feed);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        private ArrayList<MovieItem> parse(String rssFeed) throws XmlPullParserException, IOException {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(rssFeed));
            xpp.nextTag();
            return readRss(xpp);
        }

        private ArrayList<MovieItem> readRss(XmlPullParser parser)
                throws XmlPullParserException, IOException {
            ArrayList<MovieItem> movieItems = new ArrayList<>();
            parser.require(XmlPullParser.START_TAG, null, "rss");

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("channel")) {
                    movieItems.addAll(readChannel(parser));
                } else {
                    skip(parser);
                }
            }
            return movieItems;
        }

        private ArrayList<MovieItem> readChannel(XmlPullParser parser)
                throws IOException, XmlPullParserException {

            ArrayList<MovieItem> movieItems = new ArrayList<>();

            MyPreferences myPreferences=new MyPreferences(getContext());
            String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            if(todayDate.equals(myPreferences.getLastBuildDate()))
            {
                movieItems=new ArrayList<MovieItem>(mDataSource.getAllMovieItems());
            }
            else
            {
                mDataSource.removeAllMovieItems();
                parser.require(XmlPullParser.START_TAG, null, "channel");

                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name = parser.getName();
                    if (name.equals("item")) {
                        movieItems.add(readItem(parser));
                    } else {
                        skip(parser);
                    }
                }
                myPreferences.setLastBuildDate(todayDate);

            }


            return movieItems;
        }

        private MovieItem readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
            String title = null;
            String link = null;
            String description = null;
            String pubDate = null;
            parser.require(XmlPullParser.START_TAG, null, "item");

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                switch ( parser.getName() ) {
                    case "title" :
                        title = readTitle( parser );
                        break;
                    case "link" :
                        link = readLink( parser );
                        break;
                    case "description" :
                        description = readDescription( parser );
                        break;
                    case "pubDate" :
                        pubDate = readPubDate( parser );
                        break;

                    default:
                        skip( parser );
                }
            }

            Log.i(LOG_TAG, "MovieItem[ t: " + title
                    + " l: " + link
                    + " ]");
            if(title.contains("Trailer"))
            {
                title=title.replace("- Trailer","");
            return mDataSource.createMovieItem( title, link, description, pubDate );
            }
            return null;
        }



        // Processes title tags in the feed.
        private String readTitle(XmlPullParser parser)
                throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, "title");
            String title = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, "title");
            return title;
        }

        // Processes link tags in the feed.
        private String readLink(XmlPullParser parser)
                throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, "link");
            String link = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, "link");
            return link;
        }

        // Processes description tags in the feed.
        private String readDescription(XmlPullParser parser)
                throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, "description");
            String description = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, "description");
            return description;
        }

        // Processes pubDate tags in the feed.
        private String readPubDate(XmlPullParser parser)
                throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, "pubDate");
            String pubDate = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, "pubDate");
            return pubDate;
        }

        private String readText(XmlPullParser parser)
                throws IOException, XmlPullParserException {
            String result = "";
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.getText();
                parser.nextTag();
            }
            return result;
        }

        private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }

        @Override
        protected void onPostExecute( ArrayList<MovieItem> rssFeed ) {
            if (rssFeed != null) {
                //mMovieTrailers.addAll( rssFeed );
//                if (mIsSort.isChecked()){
//                    Collections.sort( rssFeed);
//                }
                if ( mIsSortTitle.isChecked() ) {
                    Collections.sort(rssFeed, new Comparator<MovieItem>() {
                        @Override
                        public int compare(MovieItem lhs, MovieItem rhs) {
                            return lhs.getTitle().compareTo(rhs.getTitle());
                        }
                    });
                }

                else if (mSortPudDate.isChecked()){
                    Collections.sort(rssFeed, new Comparator<MovieItem>() {
                        @Override
                        public int compare(MovieItem lhs, MovieItem rhs) {
                            return lhs.getPubDate().compareTo(rhs.getPubDate());
                        }
                    });
                }

                mMovieTrailers = new MovieListArrayAdapter(getActivity(), rssFeed);
                setListAdapter(mMovieTrailers);



            }
        }

    }

    @Override
    public void onResume() {
        mDataSource.open();
        super.onResume();

    }

    @Override
    public void onPause() {
        mDataSource.close();
        super.onPause();
    }
}
