package misha.miner

import android.app.Application
import com.mocklets.pluto.Pluto
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.realm.RealmConfiguration

@HiltAndroidApp
class App: Application() {

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

        Pluto.initialize(this)
    }
}