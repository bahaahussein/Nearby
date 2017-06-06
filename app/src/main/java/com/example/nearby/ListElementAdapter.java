package com.example.nearby;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nearby.data.PlacesContract;
import com.example.nearby.data.PlacesDb;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Professor on 4/13/2017.
 */
public class ListElementAdapter extends ArrayAdapter<Item> {

    private List<Item> items;

    public ListElementAdapter(Context context, List<Item> items){
        super(context, 0, items);
        this.items = items;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Item item = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

        ImageView photo = (ImageView)convertView.findViewById(R.id.place_photo);
        if(item.getPhoto().equals("null")){
            switch (item.getType()) {
                case "hospital": photo.setImageResource(R.mipmap.hospital_detail);break;
                case "cafe": photo.setImageResource(R.mipmap.cafe_detail);break;
                case "atm": photo.setImageResource(R.mipmap.atm_detail);break;
                case "bank": photo.setImageResource(R.mipmap.bank_detail);break;
                case "clothing_store": photo.setImageResource(R.mipmap.clothing_detail);break;
                case "car_repair": photo.setImageResource(R.mipmap.car_detail);break;
                case "post_office": photo.setImageResource(R.mipmap.post_detail);break;
                default: photo.setImageResource(R.mipmap.restaurant_detial);
            }
        }else{
            Picasso.with(getContext()).load(item.getPhoto()).into(photo);
        }

        TextView name = (TextView)convertView.findViewById(R.id.place_name);
        if(!item.getName().equals("null"))
            name.setText(item.getName());

        TextView address = (TextView)convertView.findViewById(R.id.place_address);
        if(!item.getAddress().equals("null"))
            address.setText(item.getAddress());

        TextView price = (TextView)convertView.findViewById(R.id.price_level);
        switch (item.getPrice()){
            case 0 : price.setText("price : free");break;
            case 1 : price.setText("price : inexpensive");break;
            case 2 : price.setText("price : moderate");break;
            case 3 : price.setText("price : expensive");break;
            case 4 : price.setText("price : very expensive");break;
        }

        TextView open = (TextView)convertView.findViewById(R.id.place_open);
        switch (item.isOpen()){
            case "true" : open.setText("open now");break;
            case "false" : open.setText("closed now");break;
        }

        RatingBar rating = (RatingBar) convertView.findViewById(R.id.place_rating);
        if(item.getRating()!=-1)
            rating.setRating((float) item.getRating());
        else
            rating.setVisibility(View.INVISIBLE);

        TextView type = (TextView)convertView.findViewById(R.id.place_type);
        ImageButton add_to_favorite = (ImageButton)convertView.findViewById(R.id.add_to_favorite);
        if(!PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getContext().getResources().getString(R.string.pref_key), false)){
            add_to_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlacesDb mDbHelper = new PlacesDb(getContext());
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(PlacesContract.FeedEntry._ID, item.getId());
                    values.put(PlacesContract.FeedEntry.COLUMN_LATITUDE, (float)item.getLatitude());
                    values.put(PlacesContract.FeedEntry.COLUMN_LONGITUDE, (float)item.getLongitude());
                    values.put(PlacesContract.FeedEntry.COLUMN_ADDRESS, item.getAddress());
                    values.put(PlacesContract.FeedEntry.COLUMN_NAME, item.getName());
                    values.put(PlacesContract.FeedEntry.COLUMN_PHOTO, item.getPhoto());
                    values.put(PlacesContract.FeedEntry.COLUMN_PRICE_LEVEL, item.getPrice());
                    values.put(PlacesContract.FeedEntry.COLUMN_RATING, (float)item.getRating());
                    values.put(PlacesContract.FeedEntry.COLUMN_TYPE, item.getType());
                    long newRowId = db.insert(PlacesContract.FeedEntry.TABLE_NAME, null, values);
                    Log.i(ListElementAdapter.class.getSimpleName(), "insert : "+ item.getPhoto());
                    mDbHelper.close();
                }
            });
        }else{
            add_to_favorite.setImageResource(R.mipmap.remove);
            type.setText(item.getType());
            add_to_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlacesDb mDbHelper = new PlacesDb(getContext());
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    db.delete(PlacesContract.FeedEntry.TABLE_NAME, PlacesContract.FeedEntry._ID+ " LIKE ?",
                            new String[]{item.getId()});
                    remove(item);
                    notifyDataSetChanged();
                    mDbHelper.close();
                }
            });
        }

        ImageButton navigate = (ImageButton)convertView.findViewById(R.id.navigate);
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getLatitude()!=-1 && item.getLongitude()!=-1){
                    Uri gmmIntentUri = Uri.parse("geo:"+item.getLatitude()+","+item.getLongitude());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(mapIntent);
                }
            }
        });
        return convertView;
    }
}
