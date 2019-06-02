package io.openweather.data.features.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import io.openweather.domain.entities.Optional;
import io.openweather.domain.entities.Weather;
import io.openweather.domain.features.weather.WeatherDao;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

public class WeatherDaoImpl extends SQLiteOpenHelper implements WeatherDao {

    private static final String TABLE_NAME = "weather";
    private static final String ID = "id";
    private static final String PLACE = "place";
    private static final String DESCRIPTION = "description";
    private static final String UPDATED_DATE = "updated_date";
    private static final String GROUP_ID = "group_id";
    private static final String TEMP = "temperature";
    private static final String PRESSURE = "pressure";
    private static final String HUMIDITY = "humidity";

    public WeatherDaoImpl(@Nullable Context context) {
        super(context, "weather", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +
                "(" + ID + " integer PRIMARY KEY ," +
                PLACE + " text," +
                DESCRIPTION + " text," +
                UPDATED_DATE + " numeric," +
                GROUP_ID + " integer," +
                TEMP + " integer," +
                PRESSURE + " integer," +
                HUMIDITY + " integer);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void insert(Weather weather) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        cv.put(ID, 1); //allow only the one weather record in db
        cv.put(PLACE, weather.getPlace());
        cv.put(DESCRIPTION, weather.getDescription());
        cv.put(UPDATED_DATE, weather.getUpdatedDate());
        cv.put(GROUP_ID, weather.getGroupId());
        cv.put(TEMP, weather.getTemp());
        cv.put(PRESSURE, weather.getPressure());
        cv.put(HUMIDITY, weather.getHumidity());

        db.insertWithOnConflict(TABLE_NAME, null, cv, CONFLICT_REPLACE);
    }

    @Override
    public Optional<Weather> get() {
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null)) {
            if (c.moveToFirst()) {
                return Optional.of(new Weather(
                        c.getString(c.getColumnIndex(PLACE)),
                        c.getString(c.getColumnIndex(DESCRIPTION)),
                        c.getLong(c.getColumnIndex(UPDATED_DATE)),
                        c.getInt(c.getColumnIndex(GROUP_ID)),
                        c.getInt(c.getColumnIndex(TEMP)),
                        c.getInt(c.getColumnIndex(PRESSURE)),
                        c.getInt(c.getColumnIndex(HUMIDITY))
                ));
            } else {
                return Optional.empty();
            }
        }
    }
}
