package anotherappdev.countripedia;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BoundsDataBaseHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "countries.sqlite";
    public static final String TABLE_BOUNDS_NAME = "COUNTRYBOUNDS";
    private Context myContext;
    public static final String DATABASE_PATH = "/data/data/com.halfwitdevs.countripedia/databases/";



    public BoundsDataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
    }

    private boolean checkDatabase() {
        try {
            String dbPath = DATABASE_PATH + DATABASE_NAME;
            File dbfile = new File(dbPath);
            return dbfile.exists();
        } catch (Exception e) {
            return false;
        }
    }

    private void copyDatabase() throws IOException {
        InputStream databaseInputStream = myContext.getAssets().open(DATABASE_NAME);
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream databaseOutputStream = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[2024];
        int mLength;
        while ((mLength = databaseInputStream.read(mBuffer)) > 0) {
            databaseOutputStream.write(mBuffer, 0, mLength);
        }
        databaseOutputStream.flush();
        databaseOutputStream.close();
        databaseInputStream.close();
    }

    public void createDatabase() {
        boolean dbExist = checkDatabase();

        if(dbExist) {
        } else {
            this.getReadableDatabase();
            try {
                this.close();
                copyDatabase();
            } catch (Exception e) {
            }
        }
    }

    public LatLngBounds getCountryBounds(String countryCode) throws Exception {
        SQLiteDatabase myDataBase = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BOUNDS_NAME + " WHERE CODE='" + countryCode + "';";

        Cursor cursor = myDataBase.rawQuery(query, null);

        cursor.moveToFirst();

        LatLng btmLft = new LatLng(cursor.getFloat(cursor.getColumnIndex("BOTTOM")), cursor.getFloat(cursor.getColumnIndex("LEFT")));
        LatLng topRgt = new LatLng(cursor.getFloat(cursor.getColumnIndex("TOP")), cursor.getFloat(cursor.getColumnIndex("RIGHT")));

        LatLngBounds bounds = new LatLngBounds(btmLft, topRgt);

        cursor.close();
        myDataBase.close();

        if(btmLft.latitude == 0 && btmLft.longitude == 0 && topRgt.longitude == 0 && topRgt.latitude == 0) {
            throw new Exception("EVERYTHING IS 0");
        }

        return bounds;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
