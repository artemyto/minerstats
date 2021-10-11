package misha.miner.models.ehterscan

data class EtherscanResponse<T>(
    val status: EtherscanResponseStatus,
    val message: String,
    val result: T
)
