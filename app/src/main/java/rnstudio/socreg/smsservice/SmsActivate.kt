package rnstudio.socreg.smsservice

import android.os.Looper
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SmsActivate(_apiKey: String) : SMSService(_apiKey) {
    private val balanceUrl = "https://api.sms-activate.org/stubs/handler_api.php?api_key=$_apiKey&action=getBalance"

    override fun getBalance(observer: Observer<Any?>) {
        Rx2AndroidNetworking.get(balanceUrl)
            .build()
            .stringObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.from(Objects.requireNonNull(Looper.myLooper())))
            .map { s ->
                try {
                    return@map s.replace("ACCESS_BALANCE", "")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            .subscribe(observer)
    }
}