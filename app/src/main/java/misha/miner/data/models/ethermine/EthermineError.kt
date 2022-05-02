package misha.miner.data.models.ethermine

import kotlinx.serialization.Serializable

@Serializable
data class EthermineError(
    val error: String
)
