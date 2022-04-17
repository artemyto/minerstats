package misha.miner.models.ethermine

import kotlinx.serialization.Serializable

@Serializable
data class EthermineCurrentStats(
    val reportedHashrate : Double,
    val currentHashrate: Double,
    val validShares: Int,
    val invalidShares: Int,
    val staleShares: Int,
    val averageHashrate: Double,
    val activeWorkers: Int,
    val unpaid: Double,
    val coinsPerMin: Double,
    val usdPerMin: Double,
)
