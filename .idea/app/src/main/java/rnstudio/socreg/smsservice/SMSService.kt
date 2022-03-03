package rnstudio.socreg.smsservice

import io.reactivex.Observable
import io.reactivex.Observer
import org.json.JSONObject

abstract class SMSService(apiKey: String) {
    abstract fun getBalance(observer: Observer<Any?>)
    abstract fun getNumber(): String?
}