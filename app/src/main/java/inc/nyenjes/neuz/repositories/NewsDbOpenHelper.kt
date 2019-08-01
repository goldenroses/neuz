package inc.nyenjes.neuz.repositories

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class NewsDbOpenHelper(context: Context): ManagedSQLiteOpenHelper(context, "neuz_00.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {

        db?.createTable("favorites", true,
            "source" to BLOB,
            "author" to TEXT,
            "title" to TEXT,
            "description" to TEXT,
            "url" to TEXT,
            "urlToImage" to TEXT,
            "publishedAt" to TEXT,
            "content" to TEXT
        )

        db?.createTable("history", true,
            "source" to TEXT,
            "author" to TEXT,
            "title" to TEXT,
            "description" to TEXT,
            "url" to TEXT,
            "urlToImage" to TEXT,
            "publishedAt" to TEXT,
            "content" to TEXT
            )
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}