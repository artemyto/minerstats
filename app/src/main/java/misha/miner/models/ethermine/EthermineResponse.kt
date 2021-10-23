package misha.miner.models.ethermine

data class EthermineResponse<T>(
    val status: EthermineResponseStatus,
    val data: T,
    val error: EthermineError
)
