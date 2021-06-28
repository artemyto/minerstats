package misha.miner.models.ethermine

data class EthermineResponse(
    val status: EthermineResponseStatus,
    val data: EthermineData,
    val error: EthermineError
)
