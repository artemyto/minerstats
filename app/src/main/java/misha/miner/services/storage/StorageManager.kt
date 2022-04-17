package misha.miner.services.storage

import misha.miner.models.storage.StorageViewModel

interface StorageManager {
    fun getStorage(): StorageViewModel
    fun update(storage: StorageViewModel)
    fun resetStorage()
}
