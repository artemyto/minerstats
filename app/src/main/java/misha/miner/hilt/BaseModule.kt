package misha.miner.hilt

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import misha.miner.common.data.AppSettingsPersistenceStorage
import misha.miner.services.storage.StorageManager
import misha.miner.services.storage.StorageManagerImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object BaseModule {

    private const val APP_SETTINGS_PREFERENCES_NAME = "app_settings_preferences_name"

    @Provides
    @Singleton
    fun provideStorageManager(): StorageManager = StorageManagerImpl()

    @Provides
    @Singleton
    fun provideAppSettingsPersistenceStorage(
        @ApplicationContext context: Context,
    ): AppSettingsPersistenceStorage {
        val preferences =
            context.getSharedPreferences(APP_SETTINGS_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return AppSettingsPersistenceStorage(preferences)
    }
}
