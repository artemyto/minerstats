package misha.miner.common.data

import misha.miner.common.data.AppSettingsPersistenceStorage.Companion.SAVED_SCAN_IP
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val appSettingsStorage: AppSettingsPersistenceStorage,
) {
    fun getSavedScanIp() = appSettingsStorage.readString(SAVED_SCAN_IP)

    fun setSavedScanIp(ip: String) = appSettingsStorage.writeString(SAVED_SCAN_IP, ip)
}
