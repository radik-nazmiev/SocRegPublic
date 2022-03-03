package rnstudio.socreg.DB

import androidx.room.*
import rnstudio.socreg.Proxy
import rnstudio.socreg.SMSServiceView

@Dao
interface ProxyDao {
    @Query("select * from proxy")
    fun getAll(): List<Proxy>

    @Insert
    fun addProxy(proxyList: ArrayList<Proxy>)

    @Insert()
    fun addProxy(proxy: Proxy)

    @Update
    fun updateProxy(proxy: Proxy)

    @Update
    fun updateProxy(proxyList: List<Proxy>)
}