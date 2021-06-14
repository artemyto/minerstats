package misha.miner.models

import io.realm.RealmList
import io.realm.RealmObject

open class StorageEntity: RealmObject() {

    var wallet = ""
    var address = ""
    var port = ""
    var name = ""
    var password = ""
    var command = ""

    var commandList = RealmList<String>()

    fun toViewModel() = StorageViewModel(
        wallet,
        address,
        name,
        port,
        password,
        command,
        mutableListOf<String>().apply { addAll(commandList) }
    )
}