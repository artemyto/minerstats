package misha.miner

import android.app.Application
import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        mInstance = this

        Realm.init(this)

        Realm.setDefaultConfiguration(
            RealmConfiguration
                .Builder()
                .name("base.realm")
                .allowWritesOnUiThread(true)
                .build()
        )
    }

    companion object {

        var mInstance: App? = null

        fun getContext(): Context? {
            return mInstance?.applicationContext
        }
    }
}