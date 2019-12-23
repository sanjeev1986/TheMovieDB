package com.sample.themoviedb.storage.disk

import android.app.Application
import android.preference.PreferenceManager
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Shared preference abstraction
 */
class SharedPrefCache(private val application: Application) {
    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(application) }
    fun saveStringPreference(key: String, value: String = ""): Completable {
        return Completable.fromAction {
            prefs.edit().putString(key, value).apply()
        }
    }

    fun getStringPreference(key: String): Single<String> {
        return Single.fromCallable { prefs.getString(key, "") }
    }

    fun saveIntegerPreference(key: String, value: Int): Completable {
        return Completable.fromAction {
            prefs.edit().putInt(key, value).apply()
        }
    }

    fun getIntegerePreference(key: String): Single<Int> {
        return Single.fromCallable { prefs.getInt(key, 60) }
    }
}