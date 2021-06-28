package misha.miner.models.ethermine

import com.google.gson.annotations.SerializedName

enum class EthermineResponseStatus(val value: String) {
    @SerializedName("OK")
    Ok("OK"),
    @SerializedName("ERROR")
    Error("ERROR"),
}