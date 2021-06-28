package misha.miner.common

import misha.miner.models.ethermine.EthermineData

class EthermineHelper(private val data: EthermineData) {

    private companion object {
        const val MINUTES_IN_MONTH = 43800
        const val MEGAHASH = 1000000
        const val WEI_IN_ETH = 1e18
    }

    fun getShares() =
        "${data.validShares}/${data.staleShares}/${data.invalidShares}"

    fun getReportedHashrate() =
        "${String.format("%.2f", data.reportedHashrate / MEGAHASH)} MH/s"

    fun getCurrentHashrate() =
        "${String.format("%.2f", data.currentHashrate / MEGAHASH)} MH/s"

    fun getAverageHashrate() =
        "${String.format("%.2f", data.averageHashrate / MEGAHASH)} MH/s"

    fun getUnpaidBalanceValue() =
        data.unpaid / WEI_IN_ETH

    fun getUnpaidBalanceLabel() =
        "${String.format("%.5f", getUnpaidBalanceValue())} ETH"

    fun getEstimatedEthForMonthValue() =
        data.coinsPerMin * MINUTES_IN_MONTH

    fun getEstimatedEthForMonthLabel() =
        "${String.format("%.5f", getEstimatedEthForMonthValue())} ETH"

    fun getWorkers() =
        data.activeWorkers
}