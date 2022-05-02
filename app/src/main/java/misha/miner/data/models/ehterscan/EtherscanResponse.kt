package misha.miner.data.models.ehterscan

import kotlinx.serialization.Serializable

@Serializable
data class EtherscanResponse<T>(
    val status: EtherscanResponseStatus,
    val message: String,
    val result: T,
)
