package com.example.db_androidstudio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "items.db";
    private static String DB_LOCATION;
    private static final int DB_VERSION = 1;

    private final Context myContext;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
        DB_LOCATION = context.getApplicationInfo().dataDir+"/databases/";

        copyDB();
    }

    private boolean checkDB() {
        File fileDB = new File(DB_LOCATION + DB_NAME);
        return fileDB.exists();
    }

    private void copyDB() {
        if (!checkDB()) {
            this.getReadableDatabase();
            try {
                copyDBFile();
            } catch (IOException e) {}
        }
    }

    private void copyDBFile() throws IOException {
        InputStream inputStream = myContext.getAssets().open(DB_NAME);
        OutputStream outputStream = new FileOutputStream(DB_LOCATION + DB_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
