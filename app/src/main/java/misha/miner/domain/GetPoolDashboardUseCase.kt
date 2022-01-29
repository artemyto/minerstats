package misha.miner.domain

import misha.miner.common.Constants
import misha.miner.models.ethermine.EthermineDashboard
import misha.miner.models.ethermine.EthermineResponseStatus
import misha.miner.services.api.RetrofitService
import javax.inject.Inject

class GetPoolDashboardUseCase @Inject constructor(
    private val api: RetrofitService
) {
    suspend fun execute(address: String) : Result<EthermineDashboard> {

        val endPoint = Constants.Ethermine.dashboardByAddress(address)
        return runCatching {
            val response = api.getPoolDashboard(endPoint)
            if (response.status != EthermineResponseStatus.Ok) throw Exception(response.error.error)
            response.data
        }
    }
}