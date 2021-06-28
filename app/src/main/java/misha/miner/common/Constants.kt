package misha.miner.common

object Constants {

    object Ethermine {

        private const val base = "https://api.ethermine.org/"

        fun statsByAddress(address: String) = "${base}miner/${address}/currentStats"
    }

    const val exampleBaseUrl = "https://ya.ru"
}