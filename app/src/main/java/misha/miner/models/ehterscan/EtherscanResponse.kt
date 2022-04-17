package misha.miner.models.ehterscan

import kotlinx.serialization.Serializable

@Serializable
data class EtherscanResponse<T>(
    val status: EtherscanResponseStatus,
    val message: String,
    val result: T,
)
