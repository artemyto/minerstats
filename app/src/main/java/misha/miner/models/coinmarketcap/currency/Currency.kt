package misha.miner.models.coinmarketcap.currency

import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val price: Double
)
