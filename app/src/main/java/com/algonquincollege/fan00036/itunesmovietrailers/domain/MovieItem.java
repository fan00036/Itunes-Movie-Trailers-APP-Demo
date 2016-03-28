package com.algonquincollege.fan00036.itunesmovietrailers.domain;

import android.util.Log;

import com.algonquincollege.fan00036.itunesmovietrailers.util.StringUtils;

import java.util.Date;

import static com.algonquincollege.fan00036.itunesmovietrailers.Constants.LOG_TAG;

/**
 * Represent an iTunes Movie Trailer item.
 *
 * A MovieItem is composed of the following instance variables:
 *
 * description (String)
 * link (String)
 * pubDate (java.util.Date)
 * title (String)
 *
 */
public class MovieItem extends Object implements Comparable<MovieItem>{
    private String description;
    private String link;
    private Date   pubDate;
    private String title;
    private long id;

    public MovieItem() {
        this( "UNKNOWN",
                "UNKNOWN",
                "UNKNOWN",
                new Date() );
    }

    public MovieItem(String title, String link, String description, String pubDate) {
        this( title, link, description, new Date(pubDate) );
    }

    public MovieItem(String title, String link, String description, Date pubDate) {
        super();

        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
    }

    // Getters and setters
    public String getDescription()                     { return description; }
    public void   setDescription( String description ) { this.description = description; }

    public String getLink()                            { return link; }
    public void   setLink( String link )               { this.link = link; }

    public Date   getPubDate()                         { return pubDate; }
    public void   setPubDate( Date pubDate )           { this.pubDate = pubDate; }

    public String getTitle()                           { return title; }
    public void   setTitle(String title)               { this.title = title; }

    /**
     * Return this movie item's <em>real</em> link.
     *
     * Apple obfuscates the link. That is, Apple buries the link within a bunch 'o Javascript.
     *
     * Use String manipulation techniques to return the real link.
     *
     * Limitations:
     *   Trailers - all trailers should work.
     *   all other types --- Clip, Featurette, TV Spot --- may work, but probably don't.
     *
     * @return String the real link to the movie trailer
     *
     * Reference:
     *   http://www.iskysoft.com/quicktime/download-apple-quicktime-trailers.html
     */
    public String getTheRealLink() {
        String theRealLink = new String( link );

        // replace the start of the link with the correct top-level URL
        theRealLink = theRealLink.replace( "http://trailers.apple.com/trailers/", "http://movietrailers.apple.com/movies/" );

        // get the movie's name
        String reverseLink = new StringBuilder(link).reverse().toString();
        int indexOf2ndSlash = StringUtils.nthIndexOf( reverseLink, '/', 2 );
        String reverseName = reverseLink.substring( 1, indexOf2ndSlash );
        String name = new StringBuilder(reverseName).reverse().toString();

        // re-build the real link by appending each piece.
        // Use the concat( ) method to concatenate 2 Strings.
        theRealLink = theRealLink.concat( name );

        // Handle the different types.
        if ( title.contains("- Trailer") ) {
            theRealLink = theRealLink.concat( "-tlr1" );
        } else if ( title.contains("- Clip") ) {
            theRealLink = theRealLink.concat( "-clip1" );
        } else if ( title.contains("- Featurette") ) {
            theRealLink = theRealLink.concat( "-fte1" );
        } else if ( title.contains("- TV Spot") ) {
            theRealLink = theRealLink.concat( "-tvspot" );
        } else {
            theRealLink = theRealLink.concat( "-tlr1" );
        }

        // the last piece: resolution + extension
        theRealLink = theRealLink.concat( "_i320.m4v" );

        // Studios.
        if ( theRealLink.contains("/lions_gate/") )
            theRealLink = theRealLink.replace( "/lions_gate/", "/lionsgate/" );
        if ( theRealLink.contains("/magnolia/") )
            theRealLink = theRealLink.replace( "/magnolia/", "/magnolia_pictures/" );
        if ( theRealLink.contains("/pnwpictures/") )
            theRealLink = theRealLink.replace( "/pnwpictures/", "/pnp/" );

        // Edge case: movie name
        if ( theRealLink.contains("thedivergentseriesallegiant") )
            theRealLink = theRealLink.replace( "thedivergentseriesallegiant", "allegiant" );

        // log theRealLink
        Log.i( LOG_TAG, "theRealLink: " + theRealLink );

        return theRealLink;
    }

    @Override
    public String toString() { return title; }


    public String getPosterLink() {
        return link + "images/poster.jpg";
    }

    ///compare this movie item's title with another's title.
    @Override
    public int compareTo(MovieItem another) {
        return this.title.compareTo(another.getTitle());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
