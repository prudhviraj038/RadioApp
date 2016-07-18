package com.yellowsoft.radioapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    Context context;
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "downloads";

    // Contacts table name
    private static final String TABLE_CONTACTS = "songs";
    private static final String TABLE_PLAYLIST = "playlist";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_NAME_ar = "name_ar";
    private static final String KEY_PLAYLIST = "phone_number";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_YOU = "youtube";
    private static final String KEY_ITUN = "itunes";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_DATE = "date";
    private static final String KEY_DATE_ar = "date_ar";
    private static final String KEY_ARTID = "artistid";
    private static final String KEY_DES = "description";
    private static final String KEY_DES_ar = "description_ar";
    private static final String KEY_COUNT = "count";
    private static final String KEY_RADIO = "isradio";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                +KEY_ID + " INTEGER,"//0
                +KEY_NAME + " TEXT,"//1
                +KEY_NAME_ar + " TEXT,"//2
                +KEY_PLAYLIST + " TEXT,"//3
                +KEY_LOCATION + " TEXT,"//4
                +KEY_DATE + " TEXT,"//5
                +KEY_DATE_ar + " TEXT,"//6
                +KEY_IMAGE + " TEXT,"//7
                +KEY_YOU + " TEXT,"//8
                +KEY_ITUN + " TEXT,"//9
                +KEY_ARTID + " TEXT,"//10
                +KEY_COUNT + " TEXT,"//11
                +KEY_DES + " TEXT,"//12
                +KEY_DES_ar + " TEXT,"//13
                +KEY_RADIO + " TEXT"+")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        String CREATE_PLAYLIST_TABLE = "CREATE TABLE " + TABLE_PLAYLIST + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," +KEY_LOCATION + " TEXT" + ")";
        db.execSQL(CREATE_PLAYLIST_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    void addContact(SongModel song,ArtistModel artistModel,String playlist) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, song.getSongId()); // Contact Phone
        values.put(KEY_NAME, song.getSongName());
        values.put(KEY_NAME_ar, song.song_name_ar); // Contact Name// Contact Name
        values.put(KEY_PLAYLIST, playlist); // Contact Phone
        values.put(KEY_LOCATION, song.getSong()); // Contact Phone
        values.put(KEY_DATE, song.getArtist_Name());
        values.put(KEY_DATE_ar, song.artist_name_ar);
        values.put(KEY_IMAGE, song.getImage());
        values.put(KEY_YOU, song.getYoutube());
        values.put(KEY_ITUN, song.getItunes());
        values.put(KEY_ARTID, song.getArtist_id());
        values.put(KEY_COUNT, song.getViewcount());
        values.put(KEY_DES, song.getDescription());
        values.put(KEY_DES_ar, song.description_ar);
        values.put(KEY_RADIO, song.isradio);
         // Contact Phone
        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
        Log.e("contact", artistModel.getArtist_id());
        Log.e("song_id", song.getSongId());

    }

    void addPlaylist(ArtistModel song) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, song.getArtist_id()); // Contact Phone
        values.put(KEY_NAME, song.getName()); // Contact Name
        values.put(KEY_LOCATION, song.getImage()); // Contact Phone
        // Inserting Row
        db.insert(TABLE_PLAYLIST, null, values);
        db.close(); // Closing database connection
        Log.e("contact",song.getArtist_id());
    }

    void updateplaylist(ArtistModel song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, song.getName()); // Contact Name
        values.put(KEY_LOCATION, song.getImage()); // Contact Phone
        // Inserting Row
        db.update(TABLE_PLAYLIST, values, KEY_ID + " = ?",
                new String[]{song.getArtist_id()});
        db.close(); // Closing database connection
        Log.e("contact",song.getImage());
    }
    // Getting single contact
    int getContactcount(String id) {
        Log.e("key_id",id);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_ID,
                        KEY_NAME, KEY_PLAYLIST, KEY_LOCATION, KEY_DATE, KEY_IMAGE, KEY_YOU, KEY_ITUN, KEY_RADIO}, KEY_ID + "=?"+ " and "  +
                        KEY_PLAYLIST + "=?",
                new String[]{id,"-1"}, null, null, null, null);
        if (cursor != null)
        return cursor.getCount();
        else return 0;
    }
    ArtistModel getArtist(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PLAYLIST, new String[]{KEY_ID,
                        KEY_NAME, KEY_LOCATION}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ArtistModel contact = new ArtistModel(cursor.getString(1),cursor.getString(0)
                , cursor.getString(2),cursor.getString(3));
        // return contact
        return contact;
    }
    // Getting All Contacts
    public List<SongModel> getAllContacts() {
        List<SongModel> contactList = new ArrayList<SongModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SongModel contact = new SongModel(cursor.getString(3),cursor.getString(3)
                        , cursor.getString(3),cursor.getString(3),cursor.getString(3),cursor.getString(3),cursor.getString(3),cursor.getString(3),
                        "-1","-1","0","0","0","0",null);
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    public ArrayList<SongModel> getsongs_by_playlist(String id) {
        ArrayList<SongModel> contactList = new ArrayList<SongModel>();
        // Select All Query


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_ID,
                        KEY_NAME,KEY_NAME_ar, KEY_PLAYLIST, KEY_LOCATION, KEY_DATE,KEY_DATE_ar, KEY_IMAGE, KEY_YOU, KEY_ITUN,KEY_ARTID,KEY_COUNT,KEY_DES,KEY_DES_ar,KEY_RADIO}, KEY_PLAYLIST + "=?",
                new String[]{id}, null, null, null, null);


        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SongModel contact = new SongModel(cursor.getString(1)//name
                        ,cursor.getString(2)//namear
                        ,cursor.getString(0)//id
                        ,cursor.getString(7)//image
                        ,cursor.getString(12)//des
                        ,cursor.getString(13),//desar
                        cursor.getString(4),//url2
                        cursor.getString(11),//count
                        cursor.getString(5),//aname
                        cursor.getString(6),//anamear
                        cursor.getString(10),//aid
                        "",//you
                        "",
                        cursor.getString(14),context);//itun

                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }


    public ArrayList<ArtistModel> getAllPlaylist() {
        ArrayList<ArtistModel> contactList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PLAYLIST;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ArtistModel contact = new ArtistModel(cursor.getString(1),cursor.getString(0)
                        , cursor.getString(2),cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        contactList.add(new ArtistModel("add new playlist","-1","add_new_playlist","my test"));
        return contactList;
    }


    // Deleting single contact
    public void deleteContact(SongModel contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getSongId()) });
        db.close();
    }



    public void deletePlaylist(ArtistModel contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYLIST, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getArtist_id()) });
        db.close();
    }

    public void deletePlaylistsongs(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_PLAYLIST + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public int getPlaylistCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PLAYLIST;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }


}
