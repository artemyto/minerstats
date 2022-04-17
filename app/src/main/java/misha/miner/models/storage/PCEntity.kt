package misha.miner.models.storage

import io.realm.RealmObject

open class PCEntity : RealmObject() {

    var address = ""
    var port = ""
    var name = ""
    var password = ""

    fun toViewModel() = PCViewModel(
        address = address,
        port = port,
        name = name,
        password = password
    )
}
