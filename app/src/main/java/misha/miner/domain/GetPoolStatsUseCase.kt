package misha.miner.domain

import misha.miner.common.Constants
import misha.miner.models.ethermine.EthermineCurrentStats
import misha.miner.models.ethermine.EthermineResponseStatus
import misha.miner.services.api.RetrofitService
import javax.inject.Inject

class GetPoolStatsUseCase @Inject constructor(
    private val api: RetrofitService
) {
    suspend fun execute(address: String) : Result<EthermineCurrentStats> {

        val endPoint = Constants.Ethermine.statsByAddress(address)
        return runCatching {
            val response = api.getPoolStats(endPoint)
            if (response.status != EthermineResponseStatus.Ok) throw Exception(response.error.error)
            response.data
        }
    }
}