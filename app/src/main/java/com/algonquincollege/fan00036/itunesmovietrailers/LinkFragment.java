package com.algonquincollege.fan00036.itunesmovietrailers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single movie's link detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link LinkActivity}
 * on handsets.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 */
public class LinkFragment extends Fragment {
    /**
     * The fragment argument representing the movie link that this fragment
     * represents.
     */
    public static final String ARG_LINK = "link";

    /**
     * The content this fragment is presenting.
     */
    private String mMovieLink;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LinkFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_LINK)) {
            // Load the content specified by the fragment
            // arguments.
            mMovieLink = getArguments().getString( ARG_LINK );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_link, container, false);

        // Show the content as text in a TextView.
        if (mMovieLink != null) {
            // TODO #8
            ((TextView) rootView.findViewById(R.id.movie_link)).setText(mMovieLink);
        }

        return rootView;
    }
}
