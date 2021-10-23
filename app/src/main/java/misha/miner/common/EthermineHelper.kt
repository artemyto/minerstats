package misha.miner.common

import misha.miner.common.util.getEthValue
import misha.miner.models.ethermine.EthermineCurrentStats

class EthermineHelper(private val currentStats: EthermineCurrentStats) {

    private companion object {
        const val MINUTES_IN_MONTH = 43800
        const val MEGAHASH = 1000000
    }

    fun getValidShares() = currentStats.validShares
    fun getStaleShares() = currentStats.staleShares
    fun getInvalidShares() = currentStats.invalidShares

    fun getReportedHashrate() =
        "${String.format("%.2f", currentStats.reportedHashrate / MEGAHASH)} MH/s"

    fun getCurrentHashrate() =
        "${String.format("%.2f", currentStats.currentHashrate / MEGAHASH)} MH/s"

    fun getAverageHashrate() =
        "${String.format("%.2f", currentStats.averageHashrate / MEGAHASH)} MH/s"

    fun getUnpaidBalanceValue() =
        currentStats.unpaid.getEthValue()

    fun getUnpaidBalanceLabel() =
        "${String.format("%.5f", getUnpaidBalanceValue())} ETH"

    fun getEstimatedEthForMonthValue() =
        currentStats.coinsPerMin * MINUTES_IN_MONTH

    fun getEstimatedEthForMonthLabel() =
        "${String.format("%.5f", getEstimatedEthForMonthValue())} ETH"

    fun getWorkers() =
        currentStats.activeWorkers
}