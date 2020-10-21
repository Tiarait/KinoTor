package com.kinotor.tiar.kinotor.utils.voice;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.items.movie.Movie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tiar on 12.2018.
 */
public class VideoContentProvider extends ContentProvider {
  private static String TAG = "VideoContentProvider";
  public static String AUTHORITY = "com.kinotor.tiar.kinotor";

  // UriMatcher stuff
  private static final int SEARCH_SUGGEST = 1;
  private UriMatcher mUriMatcher;


  private VideoDatabase mVideoDatabase;

  private final String[] queryProjection =
          new String[] {
                  BaseColumns._ID,
                  VideoDatabase.KEY_NAME,
                  VideoDatabase.KEY_DESCRIPTION,
                  VideoDatabase.KEY_ICON,
                  VideoDatabase.KEY_DATA_TYPE,
                  VideoDatabase.KEY_IS_LIVE,
                  VideoDatabase.KEY_VIDEO_WIDTH,
                  VideoDatabase.KEY_VIDEO_HEIGHT,
                  VideoDatabase.KEY_AUDIO_CHANNEL_CONFIG,
                  VideoDatabase.KEY_PURCHASE_PRICE,
                  VideoDatabase.KEY_RENTAL_PRICE,
                  VideoDatabase.KEY_RATING_STYLE,
                  VideoDatabase.KEY_RATING_SCORE,
                  VideoDatabase.KEY_PRODUCTION_YEAR,
                  VideoDatabase.KEY_COLUMN_DURATION,
                  VideoDatabase.KEY_ACTION,
                  SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
          };

  /**
   * Builds up a UriMatcher for search suggestion and shortcut refresh queries.
   */
  private static UriMatcher buildUriMatcher() {
    UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI(
            AUTHORITY, "/search/" + SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
    uriMatcher.addURI(
            AUTHORITY,
            "/search/" + SearchManager.SUGGEST_URI_PATH_QUERY + "/*",
            SEARCH_SUGGEST);
    return uriMatcher;
  }

  @Override
  public boolean onCreate() {
    mVideoDatabase = new VideoDatabase();
    mUriMatcher = buildUriMatcher();
    Log.d(TAG, "onCreate");
    return true;
  }

  /**
   * Handles all the video searches and suggestion queries from the Search Manager.
   * When requesting a specific word, the uri alone is required.
   * When searching all of the video for matches, the selectionArgs argument must carry
   * the search query as the first element.
   * All other arguments are ignored.
   */
  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri,
                      @Nullable String[] projection,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs,
                      @Nullable String sortOrder) {
    // Use the UriMatcher to see what kind of query we have and format the db query accordingly
    Log.d(TAG, uri.toString());
    if (mUriMatcher.match(uri) == SEARCH_SUGGEST) {
      Log.d(TAG, "Search suggestions requested.");

      return search(uri.getLastPathSegment());

    } else {
      Log.d(TAG, "Unknown uri to query: " + uri);
      throw new IllegalArgumentException("Unknown Uri: " + uri);
    }
  }

  private Cursor search(String query) {
    MatrixCursor matrixCursor = new MatrixCursor(queryProjection);
    try {
      Statics.list = new ArrayList<>();
      SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getContext());
//            preference.getString("global_catalog", "filmix")
      List<Movie> results = mVideoDatabase.search(query, "myhit");
      if (!results.isEmpty()) {
        for (Movie movie : results) {
          matrixCursor.addRow(convertMovieIntoRow(movie));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return matrixCursor;
  }
  private Object[] convertMovieIntoRow(Movie movie) {
    return new Object[] {
            movie.getId(),
            movie.getTitle(),
            movie.getDescription(),
            movie.getCardImage(),
            movie.getContentType(),
            movie.isLive(),
            movie.getWidth(),
            movie.getHeight(),
            movie.getAudioChannelConfig(),
            movie.getPurchasePrice(),
            movie.getRentalPrice(),
            movie.getRatingStyle(),
            movie.getRatingScore(),
            movie.getProductionYear(),
            movie.getDuration(),
            "GLOBALSEARCH",
            movie.getId()
    };
  }


  /**
   * This method is required in order to query the supported types.
   * It's also useful in our own query() method to determine the type of Uri received.
   */
  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    return null;
  }

  // Other required implementations...

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
    throw new UnsupportedOperationException("Insert is not implemented.");
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
    throw new UnsupportedOperationException("Delete is not implemented.");
  }

  @Override
  public int update(
          @NonNull Uri uri,
          @Nullable ContentValues contentValues,
          @Nullable String s,
          @Nullable String[] strings) {
    throw new UnsupportedOperationException("Update is not implemented.");
  }
}