package com.example.cs306cw1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Creates the database used for preferences
 */
class SqliteDatabase(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    /**
     * Initialise DB
     * @param db database object
     */
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TASKS_TABLE = "CREATE TABLE $TABLE_FILTER($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_FILTER_TITLE TEXT)"
        db.execSQL(CREATE_TASKS_TABLE)
    }

    /**
     * Updates database
     * @param db database object
     * @param oldVersion old
     * @param newVersion new
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FILTER")
        onCreate(db)
    }

    /**
     * Get Array of database values
     * @return MutableList<filter> list of filter types
     */
    fun listFilters(): MutableList<Filter>{
        val sql = "select * from $TABLE_FILTER"
        val db = this.readableDatabase
        val storeFilter = arrayListOf<Filter>()
        val cursor = db.rawQuery(sql, null)
        if (cursor.moveToFirst()){
            do{
                val id = Integer.parseInt(cursor.getString(0))
                val name = cursor.getString(1)
                storeFilter.add(Filter(id, name))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return storeFilter
    }

    /**
     * Adds filter to database
     * @param filtername add object to database
     */
    fun addFilter(filterName: String){
        val values = ContentValues()
        values.put(COLUMN_FILTER_TITLE, filterName)
        val db = this.writableDatabase
        db.insert(TABLE_FILTER, null, values)
    }

    /**
     * Removes filter from database
     * @param id id of value to be removed
     */
    fun deleteFilter(id: Int){
        val db = this.writableDatabase
        db.delete(TABLE_FILTER, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    companion object {
        private const val DATABASE_VERSION = 5
        private const val DATABASE_NAME = "filter"
        private const val TABLE_FILTER = "filter"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_FILTER_TITLE = "filtername"

    }
}