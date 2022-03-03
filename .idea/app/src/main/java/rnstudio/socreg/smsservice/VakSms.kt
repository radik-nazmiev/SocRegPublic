package rnstudio.socreg.smsservice

import android.os.Looper
import io.reactivex.Observable
import org.json.JSONObject
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import org.json.JSONException
import rnstudio.socreg.utils.App
import java.util.*

class VakSms(apiKey: String) : SMSService(apiKey) {
    private val balanceUrl = "https://vak-sms.com/api/getBalance/?apiKey=$apiKey"
    private val getNumberUrl = "https://vak-sms.com/api/getNumber/?apiKey=$apiKey&service=mr"

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

    override fun getNumber(): String? {
        val request = Request.Builder()
            .url(getNumberUrl)
            .build()

        App.mClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful)
                return ""

            val body = response.body!!.string()
            val jsonObject = JSONObject(body)

            try {
                // {"tel": 79991112233, "idNum": "3adb61376b8f4adb90d6e758cf8084fd"}
                return jsonObject.getString("tel")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return ""
    }
}