package misha.miner.models.coinmarketcap.data

import kotlinx.serialization.Serializable

@Serializable
data class Metadata(
    val id: Int,
    val name: String,
    val symbol: String,
    val slug: String,
    val logo: String,
)
