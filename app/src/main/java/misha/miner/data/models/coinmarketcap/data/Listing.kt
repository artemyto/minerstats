package misha.miner.data.models.coinmarketcap.data

import kotlinx.serialization.Serializable
import misha.miner.data.models.coinmarketcap.Quote

@Serializable
data class Listing(
    val id: Int,
    val name: String,
    val symbol: String,
    val slug: String,
    val cmc_rank: String,
    val quote: Quote,
)
