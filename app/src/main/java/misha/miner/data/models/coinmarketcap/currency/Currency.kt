package misha.miner.data.models.coinmarketcap.currency

import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val price: Double
)
