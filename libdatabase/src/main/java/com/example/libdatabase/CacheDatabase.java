package com.example.libdatabase;

import com.example.libcommon.AppGlobals;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Cache.class},version = 1,exportSchema = true)
public  abstract class CacheDatabase extends RoomDatabase {
    private static CacheDatabase catchDatabase;
    static {
        catchDatabase = Room.databaseBuilder(AppGlobals.getApplication(),CacheDatabase.class,"my_db")
                //是否允许在主线程进行查询
                .allowMainThreadQueries()
                .build();
    }

    public abstract CacheDao getCache();

    public static CacheDatabase get() {
        return catchDatabase;
    }

}
