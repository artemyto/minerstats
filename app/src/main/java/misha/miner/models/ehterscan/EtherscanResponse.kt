package misha.miner.models.ehterscan

data class EtherscanResponse(
    val status: EtherscanResponseStatus,
    val message: String,
    val result: String
)
