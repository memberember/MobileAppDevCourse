package com.example.myapplication

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        // удалять конфигурацию если структура БД изменилась
        val realmConfig = RealmConfiguration.Builder()
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfig)

        getSharedPreferences("name", 0).edit().putString("zzz", "xx").apply()

        getSharedPreferences("name", 0).getString("zzz", "xx")

    }
}