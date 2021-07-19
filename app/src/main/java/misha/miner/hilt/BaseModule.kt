package misha.miner.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import misha.miner.services.api.ApiManager
import misha.miner.services.api.ApiManagerImpl
import misha.miner.services.api.RetrofitService
import misha.miner.services.storage.StorageManager
import misha.miner.services.storage.StorageManagerImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object BaseModule {

    @Provides
    @Singleton
    fun provideStorageManager(): StorageManager = StorageManagerImpl()

    @Provides
    @Singleton
    fun provideApiManager(retrofitService: RetrofitService): ApiManager = ApiManagerImpl(retrofitService)
}