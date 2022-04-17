package misha.miner.models.storage

data class PCViewModel(
    var address: String,
    var port: String,
    var name: String,
    var password: String
) {

    fun toEntity() = PCEntity().apply {
        address = this@PCViewModel.address
        port = this@PCViewModel.port
        name = this@PCViewModel.name
        password = this@PCViewModel.password
    }
}
