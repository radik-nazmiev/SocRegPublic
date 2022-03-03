package rnstudio.socreg.DB

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import rnstudio.socreg.Proxy
import rnstudio.socreg.SMSServiceView

@Database(entities = [SMSServiceView::class, Proxy::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun smsServiceDao(): SMSServiceDao
    abstract fun proxyDao(): ProxyDao
}