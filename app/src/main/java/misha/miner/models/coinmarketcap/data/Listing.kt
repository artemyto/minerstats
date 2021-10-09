package misha.miner.models.coinmarketcap.data

import misha.miner.models.coinmarketcap.Quote

data class Listing(
    val id: Int,
    val name: String,
    val symbol: String,
    val cmc_rank: String,
    val quote: Quote
)
