package misha.miner.models.storage

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
        wallet = wallet,
        address = address,
        name = name,
        port = port,
        password = password,
        command = command,
        commandList = mutableListOf<String>().apply { addAll(commandList) }
    )
}