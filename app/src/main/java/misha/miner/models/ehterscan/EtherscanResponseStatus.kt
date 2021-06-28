package misha.miner.models.ehterscan

import com.google.gson.annotations.SerializedName

enum class EtherscanResponseStatus(val value: String) {
    @SerializedName("1")
    OK("1"),
    @SerializedName("0")
    Error("0"),
}