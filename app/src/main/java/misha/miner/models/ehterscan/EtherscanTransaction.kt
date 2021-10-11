package misha.miner.models.ehterscan

import com.google.gson.annotations.SerializedName

data class EtherscanTransaction(
    val blockNumber: Int,
    val timeStamp: Long,
    val hash: String,
    val nonce: Int,
    val blockHash: String,
    val transactionIndex: Int,
    val from: String,
    val to: String,
    val value: Double,
    val gas: Int,
    val gasPrice: Double,
    val isError: Int,
    @SerializedName("txreceipt_status")
    val txReceiptStatus: Int,
    val cumulativeGasUsed: Int,
    val gasUsed: Int,
    val confirmations: Int,
)
