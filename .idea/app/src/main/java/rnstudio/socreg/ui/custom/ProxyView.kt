package rnstudio.socreg.ui.custom

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rnstudio.socreg.Proxy
import rnstudio.socreg.R

@Composable
fun ProxyView(proxy: Proxy, itemClickCallBack : (proxy: Proxy) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
//                callback(serviceId)
            }
            .padding(10.dp)
            .clickable {
                itemClickCallBack(proxy)
            }
        //,
//            horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = proxy.host + ":" + proxy.port,
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
            when (proxy.isAviable) {
                null -> {
                    Column(
                        modifier = Modifier
                            .padding(start = 10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = colorResource(id = R.color.colorAccent),
                            strokeWidth = 2.dp
                        )
                    }
                }
                true -> {
                    Column(
                        modifier = Modifier
                            .padding(start = 10.dp)
                    ) {
                        Image(painterResource(R.drawable.ic_aviable), contentDescription = "")
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .padding(start = 10.dp)
                    ) {
                        Image(painterResource(R.drawable.ic_cross), contentDescription = "")
                    }
                }
            }
        }
//        if(isDefault) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) {
//                Text(
//                    text = "По умолчанию",
//                    color = Color.White,
//                    fontSize = 14.sp
//                )
//            }
//        }
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//        ) {
//            Text(
//                text = "Баланс: $balance",
//                color = Color.White,
//                fontSize = 14.sp
//            )
//        }
    }
    Divider(color = colorResource(id = R.color.colorAccent), thickness = 1.dp)
}

@Preview(showBackground = true)
@Composable
fun ProxyViewPreview() {
    val proxy = Proxy()
    proxy.host = "192.168.1.1"
    proxy.port = "80"
    proxy.userName = "admin"
    proxy.password = "admin"
    proxy.isAviable = true

    val itemClickCallBack = { _: Proxy ->
    }

    ProxyView(proxy, itemClickCallBack)
}