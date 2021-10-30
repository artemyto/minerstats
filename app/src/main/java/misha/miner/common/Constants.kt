package misha.miner.common

object Constants {

    object Ethermine {

        private const val base = "https://api.ethermine.org/"

        fun statsByAddress(address: String) = "${base}miner/${address}/currentStats"

        fun dashboardByAddress(address: String) = "${base}miner/${address}/dashboard"
    }

    object Etherscan {

        const val api = "https://api.etherscan.io/api"
    }

    object CoinMarketCap {

        const val apiKeyName = "X-CMC_PRO_API_KEY"

        private const val base = "https://pro-api.coinmarketcap.com/v1/"

        const val priceConversion = "${base}tools/price-conversion"
        const val listings = "${base}cryptocurrency/listings/latest"
    }

    const val exampleBaseUrl = "https://ya.ru"
}