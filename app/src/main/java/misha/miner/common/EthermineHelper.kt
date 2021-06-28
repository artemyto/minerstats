package misha.miner.common

import misha.miner.models.ethermine.EthermineData

class EthermineHelper(private val data: EthermineData) {

    fun getEstimatedEthForMonth(): Double {
        return data.coinsPerMin * MINUTES_IN_MONTH
    }

    private companion object {
        const val MINUTES_IN_MONTH = 43800
    }
}