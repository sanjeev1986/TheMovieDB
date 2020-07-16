package com.sample.themoviedb.common

import androidx.lifecycle.ViewModelProvider
import com.sample.themoviedb.api.ApiManager
import com.sample.themoviedb.platform.PlatformManager
import com.sample.themoviedb.repositories.RepositoryManager
import com.sample.themoviedb.storage.StorageManager
import kotlin.reflect.KClass

/**
 * View model Abstract factory
 */
@Suppress("UNCHECKED_CAST")
class AppViewModerFactory(
    private val apiManager: ApiManager,
    private val platformManager: PlatformManager,
    private val storageManager: StorageManager,
    private val repositoryManager: RepositoryManager
) {

    companion object {
        private var testInstanceMap = mutableMapOf<KClass<*>, ViewModelProvider.Factory>()

        /**
         * Set this instance for Espresso testing
         */
        fun setInstance(cls: KClass<*>, mock: ViewModelProvider.Factory) {
            testInstanceMap[cls] = mock
        }
    }

}
