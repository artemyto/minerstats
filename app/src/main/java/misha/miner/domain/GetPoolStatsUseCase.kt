package misha.miner.domain

import com.pluto.plugins.logger.PlutoLog
import misha.miner.models.ethermine.EthermineCurrentStats
import misha.miner.models.ethermine.EthermineResponseStatus
import misha.miner.services.api.RetrofitService
import javax.inject.Inject

class GetPoolStatsUseCase @Inject constructor(
    private val api: RetrofitService
) {
    suspend fun execute(address: String): Result<EthermineCurrentStats> {

        return runCatching {
            val response = api.getPoolStats(address)
            if (response.status != EthermineResponseStatus.Ok) throw Exception(response.error?.error)
            response.data!!
        }.onFailure {
            PlutoLog.d("api", it.message.toString())
        }
    }
}
