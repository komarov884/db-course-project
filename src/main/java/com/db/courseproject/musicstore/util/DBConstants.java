package com.db.courseproject.musicstore.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class with database constants.
 * <p>
 * Created on 6/11/2018.
 *
 * @author Vasilii Komarov
 */
@UtilityClass
public class DBConstants {
    public static final String SCHEMA = "mstore";

    public static final String ID = "id";

    public static final String ALBUM_PRODUCER_TABLE = "album_producer";
    public static final String ALBUM_PRODUCER_ALBUM_ID = "album_id";
    public static final String ALBUM_PRODUCER_PRODUCER_ID = "producer_id";

    public static final String ALBUMS_TABLE = "albums";
    public static final String ALBUMS_TITLE = "title";
    public static final String ALBUMS_ISSUE_YEAR = "issue_year";
    public static final String ALBUMS_PRICE = "price";
    public static final String ALBUMS_GENRE = "genre";
    public static final String ALBUMS_ARTIST_ID = "artist_id";
    public static final String ALBUMS_RECORD_LABEL_ID = "record_label_id";

    public static final String ARTISTS_TABLE = "artists";
    public static final String ARTISTS_FIRST_NAME = "first_name";
    public static final String ARTISTS_LAST_NAME = "last_name";
    public static final String ARTISTS_BIRTH_DATE = "birth_date";

    public static final String AUTHORS_TABLE = "authors";
    public static final String AUTHORS_FIRST_NAME = "first_name";
    public static final String AUTHORS_LAST_NAME = "last_name";
    public static final String AUTHORS_BIRTH_DATE = "birth_date";
    public static final String AUTHORS_AUTHOR_TYPE = "author_type";

    public static final String PRODUCERS_TABLE = "producers";
    public static final String PRODUCERS_FIRST_NAME = "first_name";
    public static final String PRODUCERS_LAST_NAME = "last_name";
    public static final String PRODUCERS_BIRTH_DATE = "birth_date";

    public static final String RECORD_LABELS_TABLE = "record_labels";
    public static final String RECORD_LABELS_NAME = "name";
    public static final String RECORD_LABELS_COUNTRY = "country";
    public static final String RECORD_LABELS_FOUNDATION_YEAR = "foundation_year";

    public static final String SONG_AUTHOR_TABLE = "song_author";
    public static final String SONG_AUTHOR_SONG_ID = "song_id";
    public static final String SONG_AUTHOR_AUTHOR_ID = "author_id";

    public static final String SONGS_TABLE = "songs";
    public static final String SONGS_ALBUM_ID = "album_id";
    public static final String SONGS_ORDER_NUMBER = "order_number";
    public static final String SONGS_TITLE = "title";
}
