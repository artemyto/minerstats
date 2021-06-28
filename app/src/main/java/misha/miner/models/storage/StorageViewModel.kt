package misha.miner.models.storage

data class StorageViewModel(
    var wallet: String = "",
    var address: String = "",
    var port: String = "",
    var name: String = "",
    var password: String = "",
    var command: String = "",
    val commandList: MutableList<String> = mutableListOf()
) {

    fun toEntity(): StorageEntity {
        val entity = StorageEntity()
        entity.wallet = wallet
        entity.address = address
        entity.name = name
        entity.port = port
        entity.password = password
        entity.command = command
        entity.commandList.addAll(commandList)
        return entity
    }
}