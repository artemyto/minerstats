package misha.miner.common

object Constants {

    object Ethermine {

        private const val base = "https://api.ethermine.org/"

        fun statsByAddress(address: String) = "${base}miner/${address}/currentStats"
    }

    object Etherscan {

        const val api = "https://api.etherscan.io/api"
    }

    const val exampleBaseUrl = "https://ya.ru"
}