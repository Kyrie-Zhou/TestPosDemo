package com.example.mydemopos.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.mydemopos.util.MyDatabaseHelper;

public class TransactionContentProvider extends ContentProvider {
    //初始化一些常量
    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private MyDatabaseHelper myDatabaseHelper;
    private SQLiteDatabase db;
    private final String AUTHORITY = "com.example.mydemopos.provider.TransactionContentProvider";
    public static final int TRANSACTION_DIR = 0;//全表
    public static final int TRANSACTION_ITEM = 1;//某个id
    public TransactionContentProvider() {
        matcher.addURI(AUTHORITY,"transaction",TRANSACTION_DIR);
        matcher.addURI(AUTHORITY,"transaction/#",TRANSACTION_ITEM);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        long transactionId = db.insert("transaction", null, values);
        return uri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        myDatabaseHelper = new MyDatabaseHelper(getContext(),"Receipt",null);
        db = myDatabaseHelper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        Cursor cursor = null;
            switch (matcher.match(uri)) {
                case TRANSACTION_DIR:
                    cursor = db.query("transaction", projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case TRANSACTION_ITEM:
                    cursor = db.query("transaction", projection, "account=?", new String[]{"000001"}, null, null, sortOrder);
                    break;
                default:
                    break;
            }
            return cursor;
        }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}