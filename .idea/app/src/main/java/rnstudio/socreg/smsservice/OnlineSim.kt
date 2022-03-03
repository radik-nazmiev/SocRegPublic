package rnstudio.socreg.smsservice

import android.os.Looper
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import rnstudio.socreg.utils.App
import java.util.*

class OnlineSim(apiKey: String) : SMSService(apiKey) {
    private val balanceUrl = "https://onlinesim.ru/api/getBalance.php?apikey=$apiKey"
    private val getNumberUrl = "https://onlinesim.ru/api/getNum.php?apikey=$apiKey&service=VKcom&number=true"

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
                // {"response":1,"tzid":55575095,"number":"+79639723239"}
                return jsonObject.getString("number")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return ""
    }
}