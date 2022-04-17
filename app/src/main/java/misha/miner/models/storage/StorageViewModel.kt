package misha.miner.models.storage

data class StorageViewModel(
    var wallet: String,
    val pcList: MutableList<PCViewModel>,
    val commandList: MutableList<String> = mutableListOf()
) {

    fun toEntity() = StorageEntity().apply {
        wallet = this@StorageViewModel.wallet
        this@StorageViewModel.pcList.forEach { pcList.add(it.toEntity()) }
        commandList.addAll(this@StorageViewModel.commandList)
    }
}
