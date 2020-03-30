package com.sample.themoviedb.storage.disk

/**
 * Rx abstracted File IO. Not implemented.
 */
import android.content.Context
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.sample.themoviedb.storage.CacheResult
import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.reflect.KClass


class DiskCache(private val context: Context, private val gson: Gson) {
    companion object {
        private val DIR = "AppCache"
    }

    fun <T : Parcelable> saveFile(
        filename: KClass<T>,
        data: T,
        isCachedir: Boolean = true
    ): CacheResult<T, Throwable> {
        var bout: BufferedOutputStream? = null
        try {
            val fileDir =
                File(
                    ((if (isCachedir) File(context.cacheDir, DIR) else File(
                        context.filesDir,
                        DIR
                    )).also {
                        if (!it.exists()) {
                            it.mkdir()
                        }
                    }),
                    filename.java.simpleName
                )
            if (fileDir.exists()) {
                context.deleteFile(filename.simpleName)
            }
            val str = gson.toJson(data)
            var fileout: OutputStream = FileOutputStream(fileDir)
            fileout = GZIPOutputStream(fileout)
            bout = BufferedOutputStream(fileout)
            bout.write(str.toByteArray())
        } catch (e: JsonSyntaxException) {
            return CacheResult.CacheError(InvalidJsonInput(e))
        } catch (e: JsonParseException) {
            return CacheResult.CacheError(ParseFailure(e))
        } catch (e: Exception) {
            return CacheResult.CacheError(UnknownFileIOError(e))
        } finally {
            bout?.flush()
            bout?.close()
        }
        return CacheResult.CacheHit(data)
    }


    fun <T : Parcelable> readFile(
        cls: KClass<T>,
        isCachedir: Boolean = true
    ): CacheResult<T, Throwable> {

        var bin: BufferedReader? = null
        try {
            val fileDir = File(
                if (isCachedir) File(context.cacheDir, DIR) else File(context.filesDir, DIR),
                cls.simpleName
            )
            if (fileDir.exists()) {
                var fin: InputStream = FileInputStream(fileDir)
                fin = GZIPInputStream(fin)
                bin = BufferedReader(InputStreamReader(fin))
                val d = gson.fromJson(bin, cls.java)
                bin.close()
                return CacheResult.CacheHit(d)
            }
            return CacheResult.CacheMiss
        } catch (e: JsonSyntaxException) {
            return CacheResult.CacheError(InvalidJsonInput(e))

        } catch (e: JsonParseException) {
            return CacheResult.CacheError(ParseFailure(e))
        } catch (e: Exception) {
            return CacheResult.CacheError(UnknownFileIOError(e))
        } finally {
            bin?.close()
        }
    }

    fun <T> deleteFile(filename: Class<T>, isCachedir: Boolean = true): Boolean {
        val file =
            File(
                (if (isCachedir) File(context.cacheDir, DIR) else File(context.filesDir, DIR)),
                filename.simpleName
            )
        if (file.exists()) {
            file.delete()
            return true
        }
        return false
    }

    fun cleanDiskCache(isCachedir: Boolean = true): Boolean {
        val file = (if (isCachedir) File(context.cacheDir, DIR) else File(context.filesDir, DIR))
        if (file.exists()) {
            file.delete()
            return true
        }
        return false
    }


    data class UnknownFileIOError(val e: Throwable) : Throwable(e)
    data class InvalidJsonInput(val e: Throwable) :
        Throwable("The object is not a valid json model")

    data class ParseFailure(val e: Throwable) :
        Throwable("Json parsing exception occurred please check the class you are type casting to")
}