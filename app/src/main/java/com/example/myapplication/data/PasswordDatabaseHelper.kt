package com.example.myapplication.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.myapplication.utils.EncryptionHelper

class PasswordDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "password_manager_app.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "password"
        private const val COLUMN_ID = "id"
        private const val COLUMN_ACCOUNT_TYPE = "accountType"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ACCOUNT_TYPE TEXT,
                $COLUMN_USERNAME TEXT,
                $COLUMN_PASSWORD TEXT
            )
        """.trimIndent()

        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertPassword(passwordEntity: PasswordEntity): Long {
        val db = writableDatabase
        val encryptedPassword = EncryptionHelper.encrypt(passwordEntity.encryptedPassword)
        val values = ContentValues().apply {
            put(COLUMN_ACCOUNT_TYPE, passwordEntity.accountType)
            put(COLUMN_USERNAME, passwordEntity.username)
            put(COLUMN_PASSWORD, encryptedPassword)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllPasswords(): List<PasswordEntity> {
        val passwords = mutableListOf<PasswordEntity>()
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                passwords.add(
                    PasswordEntity(
                        id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                        accountType = getString(getColumnIndexOrThrow(COLUMN_ACCOUNT_TYPE)),
                        username = getString(getColumnIndexOrThrow(COLUMN_USERNAME)),
                        encryptedPassword = getString(getColumnIndexOrThrow(COLUMN_PASSWORD))
                    )
                )
            }
        }
        cursor.close()
        return passwords
    }

    fun getPasswordById(id: Int): PasswordEntity? {
        val db = readableDatabase
        var password: PasswordEntity? = null

        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_ACCOUNT_TYPE, COLUMN_USERNAME, COLUMN_PASSWORD),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            password = PasswordEntity(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                accountType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_TYPE)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                encryptedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            )
        }

        cursor.close()
        return password
    }

    fun updatePassword(passwordEntity: PasswordEntity): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ACCOUNT_TYPE, passwordEntity.accountType)
            put(COLUMN_USERNAME, passwordEntity.username)
            put(COLUMN_PASSWORD, passwordEntity.encryptedPassword)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(passwordEntity.id.toString()))
    }

    fun deletePassword(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun logAllPasswords() {
        val passwords = getAllPasswords()
        passwords.forEach {
            Log.d("PasswordEntry", "ID: ${it.id}, Account: ${it.accountType}, User: ${it.username}, EncryptedPassword: ${it.encryptedPassword}")
        }
    }

}
