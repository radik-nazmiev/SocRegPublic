package rnstudio.socreg

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import rnstudio.socreg.smsservice.SMSService

@Entity(tableName = "sms_service")
class SMSServiceView(_name: String, _apiKey: String, _id: Int) {
    @PrimaryKey var id = 0
    @ColumnInfo(name = "name") var name = ""
    @ColumnInfo(name = "api_key") var apiKey = ""
    @ColumnInfo(name = "is_default") var isDefault = false
    @ColumnInfo(name = "service_id") var serviceId = _id
    @Ignore
    var bindedSmsService: SMSService? = null
    @Ignore var balance = ""

    constructor(): this("", "", 0){

    }

    init {
        name = _name
        apiKey = _apiKey
    }

    @Composable
    fun smsServiceView(callback: ((Int) -> Unit)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {
                    callback(serviceId)
                }
                .padding(10.dp)//,
//            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
            if(isDefault) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "По умолчанию",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Баланс: $balance",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
        Divider(color = colorResource(id = R.color.colorAccent), thickness = 1.dp)
    }

    @Preview(showBackground = true)
    @Composable
    fun SmsServiceViewPreview() {
        val callback = { id: Int ->

        }
        smsServiceView(callback)
    }
}