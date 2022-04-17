package misha.miner.models.ehterscan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EtherscanResponseStatus(val value: String) {
    @SerialName("1")
    OK("1"),
    @SerialName("0")
    Error("0"),
}
