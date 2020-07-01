package com.example.libdatabase;

import java.io.Serializable;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "cache")
public class Cache implements Serializable {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    public String key;
    public byte[] data;

//    @TypeConverters(value = {DateConverter.class})
//    public Date date;
}
