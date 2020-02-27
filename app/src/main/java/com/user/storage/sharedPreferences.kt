package com.example.eoku.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class sharedPreferences @SuppressLint("CommitPrefEdits") constructor(context: Context) {

    var _token:String = "_TOKEN"

    private val sharedPrefFile:String = "shared_Preferences"

    var preferences: SharedPreferences
    var editor: SharedPreferences.Editor


    init {
        this.preferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        this.editor = this.preferences.edit()
    }

    fun remove(key:String){
        editor.remove(key)
        editor.apply()
        editor.commit()
    }

    fun saveString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
        editor.commit()
    }

    fun saveInt(key: String, value: Int) {
        editor.putInt(key, value)
        editor.apply()
        editor.commit()
    }

    fun getString(key:String): String? {
        return preferences.getString(key, "empty")
    }

    fun getInt(key:String):Int? {
        return preferences.getInt(key, 0)
    }

}