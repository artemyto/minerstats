package misha.miner.models.ethermine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EthermineResponseStatus(val value: String) {
    @SerialName("OK")
    Ok("OK"),
    @SerialName("ERROR")
    Error("ERROR"),
}