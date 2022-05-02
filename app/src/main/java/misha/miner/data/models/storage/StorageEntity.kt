package misha.miner.data.models.storage

import io.realm.RealmList
import io.realm.RealmObject

open class StorageEntity : RealmObject() {

    var wallet = ""

    var pcList = RealmList<PCEntity>()

    var commandList = RealmList<String>()

    fun toViewModel() = StorageViewModel(
        wallet = wallet,
        pcList = pcList.map { it.toViewModel() }.toMutableList(),
        commandList = mutableListOf<String>().apply { addAll(commandList) }
    )
}
