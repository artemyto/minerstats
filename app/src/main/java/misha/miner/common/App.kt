package misha.miner.common

import android.app.Application
import com.pluto.Pluto
import com.pluto.plugins.exceptions.PlutoExceptionsPlugin
import com.pluto.plugins.logger.PlutoLoggerPlugin
import com.pluto.plugins.network.PlutoNetworkPlugin
import com.pluto.plugins.preferences.PlutoSharePreferencesPlugin
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.realm.RealmConfiguration

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        Realm.setDefaultConfiguration(
            RealmConfiguration
                .Builder()
                .name("base.realm")
                .allowWritesOnUiThread(true)
                .build()
        )

        Pluto
            .Installer(this)
            .addPlugin(PlutoExceptionsPlugin("exceptions"))
            .addPlugin(PlutoLoggerPlugin("logger"))
            .addPlugin(PlutoNetworkPlugin("network"))
            .addPlugin(PlutoSharePreferencesPlugin("preferences"))
            .install()
    }
}
