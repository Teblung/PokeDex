package com.teblung.pokedex.model.local.sql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.teblung.pokedex.model.remote.response.PokemonResponse

class PokemonDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "pokemon_database"

        private const val TABLE_POKEMON = "pokemon"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_URL = "url"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_POKEMON (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_URL TEXT
            )
        """.trimIndent()

        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // You can handle database upgrades here if needed
    }

    fun insertPokemon(name: String, url: String) {
        val db = writableDatabase

        // Check if the data already exists in the database
        val selection = "$COLUMN_NAME = ?"
        val selectionArgs = arrayOf(name)
        val cursor = db.query(TABLE_POKEMON, null, selection, selectionArgs, null, null, null)

        if (cursor != null && cursor.count > 0) {
            // Data already exists, do not insert again
            cursor.close()
            return
        }

        cursor?.close()

        // Data does not exist, perform the insertion
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_URL, url)

        db.insert(TABLE_POKEMON, null, values)
    }

    fun getAllPokemon(): List<PokemonResponse.Result> {
        val pokemonList = mutableListOf<PokemonResponse.Result>()
        val db = readableDatabase
        val cursor = db.query(TABLE_POKEMON, null, null, null, null, null, null)

        cursor?.use {
            val columnNames = cursor.columnNames.joinToString(", ")
            Log.d("CursorColumnNames", "Columns: $columnNames")

            while (it.moveToNext()) {
                val nameIndex = it.getColumnIndex(COLUMN_NAME)
                val urlIndex = it.getColumnIndex(COLUMN_URL)

                if (nameIndex >= 0 && urlIndex >= 0) {
                    val name = it.getString(nameIndex)
                    val url = it.getString(urlIndex)
                    val pokemon = PokemonResponse.Result(name, url)
                    pokemonList.add(pokemon)
                } else {
                    Log.d("CursorError", "Column indexes are invalid: nameIndex=$nameIndex, urlIndex=$urlIndex")
                }
            }
        }
        return pokemonList
    }
}
