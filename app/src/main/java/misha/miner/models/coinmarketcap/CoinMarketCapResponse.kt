package misha.miner.models.coinmarketcap

data class CoinMarketCapResponse(
    val status: CoinMarketCapStatus,
    val data: CoinMarketCapData
)
