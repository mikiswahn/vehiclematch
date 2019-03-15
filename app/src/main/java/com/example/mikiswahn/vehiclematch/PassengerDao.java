package com.example.mikiswahn.vehiclematch;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PassengerDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(Passenger passenger);

    @Delete
    void delete(Passenger passenger);

    @Query("DELETE FROM passenger")
    void deleteAll();

    @Query("SELECT * FROM passenger")
    List<Passenger> getAll();

    /*
    @Query("SELECT * FROM passenger WHERE pid IN (:pids)")
    List<Passenger> loadAllByIds(int[] pids);

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " + "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);
    */
}
