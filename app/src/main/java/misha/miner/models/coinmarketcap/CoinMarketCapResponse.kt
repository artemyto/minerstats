package misha.miner.models.coinmarketcap

data class CoinMarketCapResponse<T>(
    val status: CoinMarketCapStatus,
    val data: T
)
