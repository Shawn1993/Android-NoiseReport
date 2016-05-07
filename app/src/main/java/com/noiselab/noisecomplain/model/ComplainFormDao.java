package com.noiselab.noisecomplain.model;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.noiselab.noisecomplain.utility.DBHelper;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shawn on 27/3/2016.
 */
public class ComplainFormDao extends Dao<ComplainForm> {

    public final static String TABLE_NAME = "tb_complain_form";

    public ComplainFormDao(Context context) {
        super(context);
    }

    @Override
    public List<ComplainForm> queryAll() {
        List<ComplainForm> list = new ArrayList<>();
        SQLiteDatabase db = DBHelper.getDatabase(mContext);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        Gson gson = new Gson();
        while (cursor.moveToNext()) {
            ComplainForm form = new ComplainForm();
            int length = cursor.getColumnCount();
            for (int i = 0; i < length; i++) {
                String name = cursor.getColumnName(i);
                String value = cursor.getString(i);
                Log.v(name + "", value + "");
                try {
                    if (value != null) {
                        Field field = ComplainForm.class.getField(name);
                        if (field.getType().equals(int.class)) {
                            field.set(form, Integer.valueOf(value));
                        } else if (field.getType().equals(long.class)) {
                            field.set(form, Long.valueOf(value));
                        } else if (field.getType().equals(float.class)) {
                            field.set(form, Float.valueOf(value));
                        } else if (field.getType().equals(double.class)) {
                            field.set(form, Double.valueOf(value));
                        } else if (field.getType().equals(boolean.class)) {
                            field.set(form, Boolean.valueOf(value));
                        } else if (field.getType().equals(byte.class)) {
                            field.set(form, Byte.valueOf(value));
                        } else if (field.getType().equals(short.class)) {
                            field.set(form, Short.valueOf(value));
                        } else if (field.getType().equals(String.class)) {
                            field.set(form, value);
                        } else if (field.getType().isArray()) {
                            field.set(form, gson.fromJson(value, field.getType()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            list.add(form);
        }
        db.close();

        return list;
    }

    @Override
    public ComplainForm queryById(String id) {
        SQLiteDatabase db = DBHelper.getDatabase(mContext);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " where formId=?", new String[]{id});

        ComplainForm form = new ComplainForm();
        Gson gson = new Gson();
        while (cursor.moveToNext()) {
            int length = cursor.getColumnCount();
            for (int i = 0; i < length; i++) {
                String name = cursor.getColumnName(i);
                String value = cursor.getString(i);
                try {
                    if (value != null) {
                        Field field = ComplainForm.class.getField(name);
                        if (field.getType().equals(int.class)) {
                            field.set(form, Integer.valueOf(value));
                        } else if (field.getType().equals(long.class)) {
                            field.set(form, Long.valueOf(value));
                        } else if (field.getType().equals(float.class)) {
                            field.set(form, Float.valueOf(value));
                        } else if (field.getType().equals(double.class)) {
                            field.set(form, Double.valueOf(value));
                        } else if (field.getType().equals(boolean.class)) {
                            field.set(form, Boolean.valueOf(value));
                        } else if (field.getType().equals(byte.class)) {
                            field.set(form, Byte.valueOf(value));
                        } else if (field.getType().equals(short.class)) {
                            field.set(form, Short.valueOf(value));
                        } else if (field.getType().equals(String.class)) {
                            field.set(form, value);
                        } else if (field.getType().isArray()) {
                            field.set(form, gson.fromJson(value, field.getType()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        db.close();
        return form;
    }

    @Override
    public long insert(ComplainForm form) {
        SQLiteDatabase db = DBHelper.getDatabase(mContext);
        ContentValues values = new ContentValues();
        Gson gson = new Gson();
        for (Field field : ComplainForm.class.getFields()) {
            try {
                Object valueObj = field.get(form);
                if (valueObj != null) {
                    String value;
                    if (field.getType().isArray()) {
                        value = gson.toJson(valueObj);
                    } else {
                        values.put(field.getName(), String.valueOf(valueObj));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        long row = db.insert(TABLE_NAME, null, values);
        db.close();
        return row;
    }

    @Override
    public long update(ComplainForm form) {
        SQLiteDatabase db = DBHelper.getDatabase(mContext);
        ContentValues values = new ContentValues();
        for (Field field : ComplainForm.class.getFields()) {
            try {
                Object value = field.get(form);
                if (value != null) {
                    values.put(field.getName(), String.valueOf(value));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        long row = db.update(TABLE_NAME, values, "formId", new String[]{form.formId});
        db.close();
        return row;
    }

    @Override
    public int deleteById(String id) {
        SQLiteDatabase db = DBHelper.getDatabase(mContext);
        int row = db.delete(TABLE_NAME, "formId", new String[]{id});
        db.close();
        return row;
    }
}
