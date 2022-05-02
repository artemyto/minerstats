package misha.miner.data.models.ethermine

import kotlinx.serialization.Serializable

@Serializable
data class EthermineDashboard(
    val statistics: List<Statistics>,
    val workers: List<Worker>,
    val currentStatistics: Statistics,
    val settings: Settings,
) {
    @Serializable
    data class Worker(
        val worker: String,
        val reportedHashrate: Double,
        val currentHashrate: Double,
        val validShares: Int,
        val invalidShares: Int,
        val staleShares: Int,
    )

    @Serializable
    data class Statistics(
        val time: Long,
        val lastSeen: Long?,
        val reportedHashrate: Double,
        val currentHashrate: Double,
        val validShares: Int,
        val invalidShares: Int,
        val staleShares: Int,
        val activeWorkers: Int,
    )

    @Serializable
    data class Settings(
        val email: String,
        val monitor: Int,
        val minPayout: Double,
        val suspended: Int,
    )
}
