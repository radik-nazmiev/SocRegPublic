package rnstudio.socreg.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import rnstudio.socreg.Proxy
import rnstudio.socreg.SMSServiceView
import rnstudio.socreg.smsservice.OnlineSim
import rnstudio.socreg.smsservice.SmsActivate
import rnstudio.socreg.smsservice.SmsServiceOnline
import rnstudio.socreg.smsservice.VakSms
import rnstudio.socreg.utils.App

class ContentScreenViewModel: ViewModel() {
    private val _name = MutableLiveData("")
    private val db = App.instance.database
    val smsServicesView: MutableLiveData<List<SMSServiceView>> = MutableLiveData()
    val proxiesList: MutableLiveData<List<Proxy>> = MutableLiveData()

    fun getServices(){
        if(smsServicesView.value == null || smsServicesView.value?.isEmpty() == true) {
            viewModelScope.launch {
                val services = withContext(Dispatchers.IO) { db.smsServiceDao().getAll() }
                services.forEach {
                    when (it.name) {
                        "vak-sms.com" -> it.bindedSmsService = VakSms(it.apiKey)
                        "sms-service-online.com" -> it.bindedSmsService = SmsServiceOnline(it.apiKey)
                        "onlinesim.ru" -> it.bindedSmsService = OnlineSim(it.apiKey)
                        "sms-activate.org" -> it.bindedSmsService = SmsActivate(it.apiKey)
                    }
                }
                services.forEach {
                    val service = it
                    if (service.bindedSmsService != null && service.apiKey.isNotEmpty()) {
                        service.bindedSmsService!!
                            .getBalance(object : Observer<Any?> {
                                override fun onError(error: Throwable) {
                                    val s = error.message
                                }

                                override fun onComplete() {
                                    smsServicesView.value = emptyList()
                                    smsServicesView.value = services
                                }
                                override fun onSubscribe(d: Disposable) {}
                                override fun onNext(result: Any) {
                                    //do anything with response
                                    try {
                                        service.balance = result.toString()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            })
                    }
                }
                smsServicesView.postValue(services)
            }
        }
    }

    fun onServiceChecked(serviceViews: List<SMSServiceView>) {
        Thread{
            db.smsServiceDao().updateService(serviceViews)
        }.start()
    }

    fun getProxies(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val allProxy = db.proxyDao().getAll()
                    withContext(Dispatchers.Main) {
                        proxiesList.value = allProxy
                    }
                    checkProxy()
                }
                catch (ex: Exception){
                    ex.printStackTrace()
                }
            }
        }
    }

    private suspend fun checkProxy(){
        val list: MutableList<Proxy> = mutableListOf()
        proxiesList.value?.let { list.addAll(it) }

        list.forEach {
            Thread.sleep(1000)
            try {
                it.check()
            }
            catch (ex: IOException){
                ex.printStackTrace()
                it.isAviable = false
            }
        }
        withContext(Dispatchers.Main){
            proxiesList.value = emptyList()
            proxiesList.value = list
        }
    }

    fun updateProxy(proxy: Proxy){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                db.proxyDao().updateProxy(proxy)
                getProxies()
            }
        }
    }

    fun insertProxy(proxy: Proxy){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    db.proxyDao().addProxy(proxy)
                    getProxies()
                }
                catch (ex: Exception){
                    ex.printStackTrace()
                }
            }
        }
    }
}