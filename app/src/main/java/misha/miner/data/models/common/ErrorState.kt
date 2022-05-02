package misha.miner.data.models.common

sealed class ErrorState {
    object None : ErrorState()
    class Error(var message: String) : ErrorState()
}
