package misha.miner.data.models.ehterscan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
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
    @SerialName("txreceipt_status")
    val txReceiptStatus: Int,
    val cumulativeGasUsed: Int,
    val gasUsed: Int,
    val confirmations: Int,
)
