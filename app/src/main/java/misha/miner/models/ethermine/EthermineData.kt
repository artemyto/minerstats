package misha.miner.models.ethermine

data class EthermineData(
    val reportedHashrate : Double,
    val currentHashrate: Double,
    val validShares: Int,
    val invalidShares: Int,
    val staleShares: Int,
    val averageHashrate: Double,
    val activeWorkers: Int,
    val unpaid: Double,
    val unconfirmed: Double,
    val coinsPerMin: Double,
    val usdPerMin: Double,
)
