package com.noiselab.noisecomplain.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.noiselab.noisecomplain.model.ComplainForm;
import com.noiselab.noisecomplain.model.ComplainFormDao;

import java.lang.reflect.Field;

/**
 * Created by shawn on 27/3/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "db_mydb";
    private final static int DATABASE_VERSION = 1;

    private final static String CREATE_TABLE = "CREATE TABLE ";

    private static DBHelper helper;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (helper == null) {
            helper = new DBHelper(context);
        }
        return helper.getReadableDatabase();
    }

    private String createTableSQL(Class clazz) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(CREATE_TABLE + ComplainFormDao.TABLE_NAME + " (");
        Field[] fields = clazz.getFields();
        for (int i = 0; i < fields.length; i++) {
            sqlBuilder.append(fields[i].getName());
            if (i < fields.length - 1) {
                sqlBuilder.append(',');
            }
        }
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableSQL(ComplainForm.class));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
