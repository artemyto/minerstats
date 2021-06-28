package misha.miner.services.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import misha.miner.App
import misha.miner.BuildConfig
import misha.miner.common.Constants
import misha.miner.models.BaseError
import misha.miner.models.ehterscan.EtherscanResponseStatus
import misha.miner.models.ethermine.EthermineData
import java.io.IOException
import java.lang.Exception

object ApiManager {

    fun getPoolStats(
        address: String,
        completion: (EthermineData) -> Unit,
        onError: (BaseError) -> Unit
    ) {

        val service = RetrofitFactory.makeRetrofitService(App.getContext())

        val endPoint = Constants.Ethermine.statsByAddress(address)
        val apiCall = service::getPoolStats

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiCall(endPoint)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        when {
                            response.body() == null -> {
                                onError(BaseError("Пустой ответ"))
                            }
                            response.body()?.error != null -> {

                                onError(
                                    BaseError(
                                        response.body()?.error?.error ?: "Что-то пошло не так"
                                    )
                                )
                            }
                            else -> try {
                                completion(response.body()?.data!!)
                            } catch (e: NullPointerException) {
                                onError(BaseError("Пустой ответ"))
                            }
                        }
                    } else {
                        onError(BaseError(response.message() ?: "Что-то пошло не так"))
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    onError(BaseError(e.message ?: "Нет интернета"))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(BaseError(e.message ?: "Что-то пошло не так"))
                }
            }
        }
    }

    fun getWalletStats(
        address: String,
        completion: (String) -> Unit,
        onError: (BaseError) -> Unit
    ) {

        val service = RetrofitFactory.makeRetrofitService(App.getContext())

        val queries = mapOf(
            "module" to "account",
            "action" to "balance",
            "address" to address,
            "tag" to "latest",
            "apikey" to BuildConfig.ETHERSCAN_API_KEY
        )
        val endPoint = Constants.Etherscan.api
        val apiCall = service::getWalletStats

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiCall(endPoint, queries)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            if (body.status != EtherscanResponseStatus.OK) {
                                onError(BaseError(response.body()?.result ?: "Что-то пошло не так"))
                            } else {
                                completion(body.result)
                            }
                        } ?: run {
                            onError(BaseError("Пустой ответ"))
                        }
                    } else {
                        onError(BaseError(response.message() ?: "Что-то пошло не так"))
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    onError(BaseError(e.message ?: "Нет интернета"))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(BaseError(e.message ?: "Что-то пошло не так"))
                }
            }
        }
    }
}