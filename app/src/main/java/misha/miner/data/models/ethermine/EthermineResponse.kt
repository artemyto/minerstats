package misha.miner.data.models.ethermine

import kotlinx.serialization.Serializable

@Serializable
data class EthermineResponse<T>(
    val status: EthermineResponseStatus,
    val data: T? = null,
    val error: EthermineError? = null,
)
