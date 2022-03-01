package rnstudio.socreg.smsservice

import android.os.Looper
import io.reactivex.Observable
import org.json.JSONObject
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import java.util.*

class VakSms(apiKey: String) : SMSService(apiKey) {
    private val balanceUrl = "https://vak-sms.com/api/getBalance/?apiKey=$apiKey"

    override fun getBalance(observer: Observer<Any?>) {
        Rx2AndroidNetworking.get(balanceUrl)
            .build()
            .stringObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.from(Objects.requireNonNull(Looper.myLooper())))
            .map { s ->
                try {
                    val jsonObject = JSONObject(s)
                    return@map jsonObject.getString("balance")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            .subscribe(observer)
    }
}