package com.example.libdatabase;

import java.util.Date;

import androidx.room.TypeConverter;

public class DateConverter {
    @TypeConverter
    public static Long date2Long(Date date){
        return date.getTime();
    }

    @TypeConverter
    public static Date long2Date(long data){
        return new Date(data);
    }
}
