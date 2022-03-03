package rnstudio.socreg.smsservice

import android.os.Looper
import io.reactivex.Observable
import org.json.JSONObject
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import org.json.JSONException
import rnstudio.socreg.utils.App
import java.util.*

class SmsServiceOnline(apiKey: String) : SMSService(apiKey) {
    private val balanceUrl = "https://sms-service-online.com/stubs/handler_api?api_key=$apiKey&action=getBalance&lang=ru"
    private val getNumberUrl = "https://sms-service-online.com/stubs/handler_api?api_key=$apiKey&action=getNumber&service=vk&operator=any&country=0&lang=ru"

    override fun getBalance(observer: Observer<Any?>) {
        Rx2AndroidNetworking.get(balanceUrl)
            .build()
            .stringObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.from(Objects.requireNonNull(Looper.myLooper())))
            .subscribe(observer)
    }

    override fun getNumber(): String? {
        val request = Request.Builder()
            .url(getNumberUrl)
            .build()

        App.mClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful)
                return ""

            val body = response.body!!.string()
            try{
                val params = body.split(":")
                return params[2]
            }
            catch (ex: Exception){
                ex.printStackTrace()
            }
        }
        return ""
    }
}