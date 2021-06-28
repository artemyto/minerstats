package misha.miner.services.api

import misha.miner.models.ethermine.EthermineResponse
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {

    @GET
    suspend fun getPoolStats(
        @Url
        url: String
    ): Response<EthermineResponse>
}