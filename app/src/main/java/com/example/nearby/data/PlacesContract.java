package com.example.nearby.data;

import android.provider.BaseColumns;

/**
 * Created by Professor on 4/14/2017.
 */
public final class PlacesContract {
    private PlacesContract() {
    }

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_PRICE_LEVEL = "price";
        public static final String COLUMN_RATING = "rating";
        public static final String _ID = "id";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_TYPE = "type";
    }
}