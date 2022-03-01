package rnstudio.socreg.smsservice

import android.os.Looper
import io.reactivex.Observable
import org.json.JSONObject
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import java.util.*

class SmsServiceOnline(apiKey: String) : SMSService(apiKey) {
    private val balanceUrl = "https://sms-service-online.com/stubs/handler_api?api_key=$apiKey&action=getBalance&lang=ru"

    override fun getBalance(observer: Observer<Any?>) {
        Rx2AndroidNetworking.get(balanceUrl)
            .build()
            .stringObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.from(Objects.requireNonNull(Looper.myLooper())))
//            .map { s ->
//                try {
//                    val jsonObject = JSONObject(s)
//                    s.drop(s.length)
//                    s + jsonObject.getString("balance")
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            }
            .subscribe(observer)
    }
}