package misha.miner.models.coinmarketcap.data

import kotlinx.serialization.Serializable
import misha.miner.models.coinmarketcap.Quote

@Serializable
data class Converted(
    val amount: Double,
    val quote: Quote,
)
