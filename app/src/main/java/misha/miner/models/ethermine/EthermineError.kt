package misha.miner.models.ethermine

import kotlinx.serialization.Serializable

@Serializable
data class EthermineError(
    val error: String
)
