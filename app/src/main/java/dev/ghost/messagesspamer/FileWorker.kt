package dev.ghost.messagesspamer

import android.app.Fragment
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


object FileWorker {

    const val FILE_SELECT_SAME_CODE = 101
    const val FILE_SELECT_DIFFERENT_CODE = 102

    // Метод для открытия файла
    fun openFile(fileName: Uri, activity: FragmentActivity): String {
        return try {
            val inputStream: InputStream? = activity.getContentResolver().openInputStream(fileName)
            val r = BufferedReader(InputStreamReader(inputStream))
            val result = StringBuilder()
            var line: String?
            while (r.readLine().also { line = it } != null) {
                result.appendLine(line)
            }
            result.toString()
        } catch (e: java.lang.Exception) {
            ""
        }
    }

    fun getPath(context: Context, uri: Uri): String? {
        if ("content".equals(uri.getScheme(), ignoreCase = true)) {
            val projection = arrayOf("_data")
            var cursor: Cursor? = null
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null)
                val column_index: Int = cursor!!.getColumnIndexOrThrow("_data")
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) {
                // Eat it
            }
        } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
            return uri.getPath()
        }
        return null
    }
}