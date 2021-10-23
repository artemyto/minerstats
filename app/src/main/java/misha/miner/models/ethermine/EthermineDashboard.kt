package misha.miner.models.ethermine

data class EthermineDashboard(
    val statistics: List<Statistics>,
    val workers: List<Worker>,
    val currentStatistics: Statistics,
    val settings: Settings,
) {
    data class Worker(
        val worker: String,
        val reportedHashrate: Double,
        val currentHashrate: Double,
        val validShares: Int,
        val invalidShares: Int,
        val staleShares: Int,
    )

    data class Statistics(
        val time: Long,
        val lastSeen: Long?,
        val reportedHashrate: Double,
        val currentHashrate: Double,
        val validShares: Int,
        val invalidShares: Int,
        val staleShares: Int,
        val activeWorkers: Int,
        val unpaid: Double?,
    )

    data class Settings(
        val email: String,
        val monitor: Int,
        val minPayout: Double,
        val suspended: Int,
    )
}
