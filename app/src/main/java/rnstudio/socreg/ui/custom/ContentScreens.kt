package rnstudio.socreg.ui.custom

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.EditText
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import rnstudio.socreg.Proxy
import rnstudio.socreg.R
import rnstudio.socreg.SMSServiceView
import rnstudio.socreg.repository.DataStorePreferenceRepository
import rnstudio.socreg.utils.App
import rnstudio.socreg.view_models.BDateViewModel
import rnstudio.socreg.view_models.ContentScreenViewModel
import rnstudio.socreg.view_models.MainScreenViewModel

@Composable
fun HomeScreen() {
    val coroutineScope = rememberCoroutineScope()
    val dataStorePreferenceRepository = App.getDataStore()
    val viewModel: BDateViewModel = viewModel(factory = DataStoreViewModelFactory(dataStorePreferenceRepository))
    val bDateFrom by viewModel.bDateFrom.observeAsState()
    val bDateTo by viewModel.bDateTo.observeAsState()
    viewModel.getBDates()

//    var datePicked1 : String? by remember {
//        mutableStateOf(bDateFrom)
//    }

    val updatedDate1 = { date : Long? ->
//        datePicked1 = date?.toString()?:""
        coroutineScope.launch {
            viewModel.saveBDateFrom(date?.toString()?:"")
        }
    }

//    var datePicked2 : String? by remember {
//        mutableStateOf(bDateTo)
//    }

    val updatedDate2 = { date : Long? ->
//        datePicked2 = date?.toString()?:""
        coroutineScope.launch {
            viewModel.saveBDateTo(date?.toString()?:"")
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.colorPrimaryLight))
            .padding(bottom = 85.dp, top = 10.dp, start = 10.dp, end = 10.dp)
    ) {
        item {
            SexSelectRow()
        }
        item{
            BDateSelect(bDateFrom, updatedDate1, bDateTo, updatedDate2)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

@Composable
fun SMSScreen() {
    val viewModel: ContentScreenViewModel = viewModel()
    val items by viewModel.smsServicesView.observeAsState(initial = emptyList())
    viewModel.getServices()
    ShowServices(items, viewModel)
}

@Composable
fun ShowServices(items: List<SMSServiceView>, viewModel: ContentScreenViewModel){
    val openPopup = remember { mutableStateOf(false)}
    var currentServiceId: Int = 0
    var currentApiKey = ""
    val context = LocalContext.current

    val textChangeCallback = { id: Int, apiKey: String, isChecked: Boolean ->
        items.forEach { item ->
            if(id == item.serviceId) {
                item.apiKey = apiKey
                item.isDefault = isChecked
            }
            if(isChecked && id != item.serviceId){
                item.isDefault = false
            }
        }
        viewModel.onServiceChecked(items)
    }

    val callback = { id: Int ->
        currentServiceId = id
        items.forEach { item ->
            if(id == item.serviceId) {
                currentApiKey = item.apiKey
                openPopup(id, item.apiKey, item.isDefault, textChangeCallback, context)
            }
        }
        openPopup.value = true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.colorPrimaryLight))
            .padding(bottom = 85.dp, top = 10.dp, start = 10.dp, end = 10.dp)
    ) {
        for(i in items.indices){
            item{
                items[i].smsServiceView(callback)
            }
        }
    }
}

fun openPopup(
    serviceId: Int,
    apiKey: String,
    isDefault: Boolean,
    textChangeCallback: (Int, String, Boolean) -> Unit,
    context: Context
){
    val inflater = LayoutInflater.from(context)
    val view = inflater.inflate(R.layout.sms_popup_view, null)
    val apiKeyEditText = view.findViewById<EditText>(R.id.apiKeyEditText)
    val checkBox = view.findViewById<CheckBox>(R.id.checkBox)
    apiKeyEditText.setText(apiKey)
    checkBox.isChecked = isDefault

    MaterialAlertDialogBuilder(context)
        .setView(view)
        .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
            textChangeCallback(serviceId, apiKeyEditText.text.toString(), checkBox.isChecked)
            dialogInterface.dismiss()
        })
        .create()
        .show()
}

@Preview(showBackground = true)
@Composable
fun SMSScreenPreview() {
    SMSScreen()
}

@Composable
fun MoviesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.colorPrimaryLight))
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Movies View",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MoviesScreenPreview() {
    MoviesScreen()
}


@Composable
fun ProxyScreen() {
    val context = LocalContext.current
    val viewModel: ContentScreenViewModel = viewModel()
    val items by viewModel.proxiesList.observeAsState(initial = emptyList())
    viewModel.getProxies()

    val proxySaveCallBack = { id: Long?, proxyHost: String, proxyPort: String, proxyUserName: String, proxyPassword: String ->
        val proxy = Proxy()
        if(id == null){
            proxy.host = proxyHost
            proxy.port = proxyPort
            proxy.userName = proxyUserName
            proxy.password = proxyPassword
            viewModel.insertProxy(proxy)
        }
        else{
            proxy.id = id
            proxy.host = proxyHost
            proxy.port = proxyPort
            proxy.userName = proxyUserName
            proxy.password = proxyPassword
            viewModel.updateProxy(proxy)
        }
    }

    val itemClickCallBack = { proxy: Proxy ->
        openProxyPopup(
            proxyHost = proxy.host.toString(),
            proxyPort = proxy.port.toString(),
            proxyUserName = proxy.userName.toString(),
            proxyPassword = proxy.password.toString(),
            proxySaveCallBack = proxySaveCallBack,
            context = context,
            id = proxy.id
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.colorPrimaryLight))
                .padding(10.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { openProxyPopup(null,"", "", "", "", context, proxySaveCallBack) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = Color.White
                ),
                modifier = Modifier.background(Color.Transparent),
                elevation = null
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    "",
                    modifier = Modifier.background(Color.Transparent)
                )
            }
        }
        Row(
            modifier = Modifier
                .background(colorResource(id = R.color.colorPrimaryLight))
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(bottom = 85.dp, top = 10.dp, start = 10.dp, end = 10.dp)
            ) {
                for(i in items.indices){
                    item {
                        ProxyView(items[i], itemClickCallBack)
                    }
                }
            }
        }
    }
}

fun openProxyPopup(
    id: Long?,
    proxyHost: String,
    proxyPort: String,
    proxyUserName: String,
    proxyPassword: String,
    context: Context,
    proxySaveCallBack: (id: Long?, proxyHost: String, proxyPort: String, proxyUserName: String, proxyPassword: String) -> Unit
){
    val inflater = LayoutInflater.from(context)
    val view = inflater.inflate(R.layout.proxy_popup_view, null)
    val proxyIPEditText = view.findViewById<EditText>(R.id.proxyIPEditText)
    proxyIPEditText.setText(proxyHost)
    val proxyPortEditText = view.findViewById<EditText>(R.id.proxyPortEditText)
    proxyPortEditText.setText(proxyPort)
    val proxyUserNameEditText = view.findViewById<EditText>(R.id.proxyUserNameEditText)
    proxyUserNameEditText.setText(proxyUserName)
    val proxyPasswordEditText = view.findViewById<EditText>(R.id.proxyPasswordEditText)
    proxyPasswordEditText.setText(proxyPassword)

    MaterialAlertDialogBuilder(context)
        .setView(view)
        .setPositiveButton(R.string.save, DialogInterface.OnClickListener { dialogInterface, i ->
            proxySaveCallBack(
                id,
                proxyIPEditText.text.toString(),
                proxyPortEditText.text.toString(),
                proxyUserNameEditText.text.toString(),
                proxyPasswordEditText.text.toString()
            )
            dialogInterface.dismiss()
        })
        .create()
        .show()
}

@Preview(showBackground = true)
@Composable
fun ProxyScreenPreview() {
    ProxyScreen()
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.colorPrimaryLight))
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Profile View",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}