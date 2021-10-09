package misha.miner.models.coinmarketcap.data

import misha.miner.models.coinmarketcap.Quote

data class Converted(
    val amount: Double,
    val quote: Quote
)
