package misha.miner.data.models.coinmarketcap.data

import kotlinx.serialization.Serializable
import misha.miner.data.models.coinmarketcap.Quote

@Serializable
data class Converted(
    val amount: Double,
    val quote: Quote,
)
