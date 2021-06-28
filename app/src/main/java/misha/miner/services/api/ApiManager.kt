package misha.miner.services.api

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import misha.miner.App
import misha.miner.common.Constants
import misha.miner.models.BaseError
import misha.miner.models.ethermine.EthermineData
import java.io.IOException
import java.lang.Exception

object ApiManager {

    fun getPoolStats(
        address: String,
        completion: (EthermineData) -> Unit,
        onError: (BaseError) -> Unit
    ) {
        Log.d("mytag", "we are in apiManager")

        val service = RetrofitFactory.makeRetrofitService(App.getContext())

        val endPoint = Constants.Ethermine.statsByAddress(address)
        val apiCall = service::getPoolStats

        Log.d("mytag", "we want to call coroutine")

        CoroutineScope(Dispatchers.IO).launch {
            Log.d("mytag", "we are in routine")
            try {
                val response = apiCall(endPoint)
                withContext(Dispatchers.Main) {
                    Log.d("mytag", "we are returned to Main IO")
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
}