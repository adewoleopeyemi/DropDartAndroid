package com.example.dropdart.DaoClass;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.dropdart.EntityClass.UserModel;

import java.util.List;

@Dao
public interface Daoclass {

    @Insert
    void InsertAllModel(UserModel model);

    @Query("select * from user")
    List<UserModel> getAllName();
}
