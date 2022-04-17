package misha.miner.common

import misha.miner.common.util.getEthValue
import misha.miner.common.util.hashrateToMegahashLabel
import misha.miner.models.ethermine.EthermineCurrentStats

class EthermineHelper(private val currentStats: EthermineCurrentStats) {

    private companion object {
        const val MINUTES_IN_MONTH = 43800
    }

    fun getValidShares() = currentStats.validShares
    fun getStaleShares() = currentStats.staleShares
    fun getInvalidShares() = currentStats.invalidShares

    fun getReportedHashrate() =
        currentStats.reportedHashrate.hashrateToMegahashLabel()

    fun getCurrentHashrate() =
        currentStats.currentHashrate.hashrateToMegahashLabel()

    fun getAverageHashrate() =
        currentStats.averageHashrate.hashrateToMegahashLabel()

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
