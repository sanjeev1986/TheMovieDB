package com.sanj.appstarterpack.storage.disk

/**
 * Rx abstracted File IO. Not implemented.
 */
import android.content.Context
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import io.reactivex.Maybe
import io.reactivex.Single
import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream


class DiskCache(private val context: Context) {

    private val gson = Gson()


    fun <T : Parcelable> saveFile(
        filename: Class<T>,
        data: T,
        isCachedir: Boolean = true
    ): Single<T> = Single.fromCallable {
        var bout: BufferedOutputStream? = null
        try {
            val fileDir =
                File((if (isCachedir) context.cacheDir else context.filesDir), filename.simpleName)
            if (fileDir.exists()) {
                context.deleteFile(filename.simpleName)
            }
            val str = gson.toJson(data)
            var fileout: OutputStream = FileOutputStream(fileDir)
            fileout = GZIPOutputStream(fileout)
            bout = BufferedOutputStream(fileout)
            bout.write(str.toByteArray())
        } catch (e: JsonSyntaxException) {
            throw InvalidJsonInput
        } catch (e: JsonParseException) {
            throw ParseFailure
        } catch (e: Exception) {
            throw UnknownFileIOError(e)
        } finally {
            bout?.flush()
            bout?.close()
        }
        return@fromCallable data
    }



    fun <T : Parcelable> readFile(cls: Class<T>, isCachedir: Boolean = true): Maybe<T> {

        return Maybe.create<T> { subscriber ->
            var bin: BufferedReader? = null
            try {
                val fileDir = File(
                    (if (isCachedir) context.cacheDir else context.filesDir),
                    cls.simpleName
                )
                if (fileDir.exists()) {
                    var fin: InputStream = FileInputStream(fileDir)
                    fin = GZIPInputStream(fin)
                    bin = BufferedReader(InputStreamReader(fin))
                    val d = gson.fromJson(bin, cls)
                    bin.close()
                    if (!subscriber.isDisposed) {
                        subscriber.onSuccess(d)
                    }

                }
                if (!subscriber.isDisposed) {
                    subscriber.onComplete()
                }
            } catch (e: JsonSyntaxException) {
                if (!subscriber.isDisposed) {
                    subscriber.onError(InvalidJsonInput)
                }
            } catch (e: JsonParseException) {
                if (!subscriber.isDisposed) {
                    subscriber.onError(ParseFailure)
                }
            } catch (e: Exception) {
                if (!subscriber.isDisposed) {
                    subscriber.onError(UnknownFileIOError(e))
                }
            } finally {
                bin?.close()
            }
        }
    }

    fun <T> deleteFile(filename: Class<T>, isCachedir: Boolean = true): Maybe<T> {

        return Maybe.create<T> {
            val file = File((if (isCachedir) context.cacheDir else context.filesDir), filename.simpleName)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    //suspend fun <T> saveFileAsync(filename: String, data: T) = saveFile(filename, data).await()

    //suspend fun <T> readFileAsync(filename: String, cls: Class<T>): T? = readFile(filename,cls).await()

    class UnknownFileIOError(throwable: Throwable) : Throwable(throwable)
    object InvalidJsonInput : Throwable("The object is not a valid json model")
    object ParseFailure :
        Throwable("Json parsing exception occurred please check the class you are type casting to")
}