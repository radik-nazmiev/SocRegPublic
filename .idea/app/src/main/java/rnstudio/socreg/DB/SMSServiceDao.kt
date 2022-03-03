package rnstudio.socreg.DB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import rnstudio.socreg.SMSServiceView

@Dao
interface SMSServiceDao {
    @Query("select * from sms_service")
    fun getAll(): List<SMSServiceView>

    @Query("select * from sms_service where is_default = 1")
    fun getDefaultService(): SMSServiceView

    @Insert
    fun addServices(serviceViews: ArrayList<SMSServiceView>)

    @Update
    fun updateService(serviceView: SMSServiceView)

    @Update
    fun updateService(serviceViews: List<SMSServiceView>)
}