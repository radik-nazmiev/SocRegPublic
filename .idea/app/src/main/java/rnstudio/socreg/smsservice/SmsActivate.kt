package rnstudio.socreg.smsservice

import android.os.Looper
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import rnstudio.socreg.utils.App
import java.util.*

class SmsActivate(apiKey: String) : SMSService(apiKey) {
    private val balanceUrl = "https://api.sms-activate.org/stubs/handler_api.php?api_key=$apiKey&action=getBalance"
    private val getNumberUrl = "https://api.sms-activate.org/stubs/handler_api.php?api_key=$apiKey&action=getNumber&service=vk"

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