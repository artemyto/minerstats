package misha.miner.models.coinmarketcap.data

import kotlinx.serialization.Serializable
import misha.miner.models.coinmarketcap.Quote

@Serializable
data class Listing(
    val id: Int,
    val name: String,
    val symbol: String,
    val cmc_rank: String,
    val quote: Quote,
)
