package misha.miner.services.storage

import io.realm.Realm
import io.realm.kotlin.where
import misha.miner.data.models.storage.StorageEntity
import misha.miner.data.models.storage.StorageViewModel

class StorageManagerImpl : StorageManager {

    override fun getStorage(): StorageViewModel {
        val realm = Realm.getDefaultInstance()
        val storedStorage: StorageViewModel? =
            realm.where<StorageEntity>().findFirst()?.toViewModel()
        realm.close()

        return storedStorage ?: initStorageEntity().toViewModel()
    }

    override fun update(storage: StorageViewModel) {

        val realm = Realm.getDefaultInstance()
        val entity = storage.toEntity()
        realm.executeTransaction {
            val oldEntity = realm.where<StorageEntity>().findFirst()
            oldEntity?.deleteFromRealm()
            realm.copyToRealm(entity)
        }
        realm.close()

        getStorage()
    }

    override fun resetStorage() {

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val storage = realm.where<StorageEntity>().findFirst()
            storage?.deleteFromRealm()
        }
        realm.close()

        initStorageEntity()
    }

    private fun initStorageEntity(): StorageEntity {

        val storage = StorageEntity()

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val oldEntity = realm.where<StorageEntity>().findFirst()
            oldEntity?.deleteFromRealm()
            realm.copyToRealm(storage)
        }
        realm.close()
        return storage
    }
}
