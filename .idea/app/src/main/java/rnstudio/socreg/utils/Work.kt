package rnstudio.socreg.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import rnstudio.socreg.random_person.RandomPerson
import rnstudio.socreg.services.RegServiceListener
import kotlin.coroutines.CoroutineContext

class Work: CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
    private var regServiceListener: RegServiceListener? = null
    private var randomPerson: RandomPerson? = null
    private var gender = ""
    private var bDateFrom = ""
    private var bDateTo = ""
    private var phoneNumber = ""

    fun setRegServiceListener(_regServiceListener: RegServiceListener){
        regServiceListener = _regServiceListener
    }

    fun start(): Job {
        val job = launch {
            getPreferences()
            processing()
        }
        return  job
    }

    private fun processing(){
        getRandomPerson()
        getNumber()
    }

    private fun getRandomPerson(){
        val rp: RandomPerson = RandomPerson()
        randomPerson = rp.get(gender)
    }

    private fun getPreferences(){
        val dataStorePreferenceRepository = App.getDataStore()
        bDateFrom = dataStorePreferenceRepository.getUserBDateFromSync.toString()
        bDateTo = dataStorePreferenceRepository.getUserBDateToSync.toString()
        gender = dataStorePreferenceRepository.getUserGenderSync.toString()
    }

    private fun getNumber(){
        val db = App.instance.database
        val smsService = db.smsServiceDao().getDefaultService()
        phoneNumber = smsService.getBindedService()?.getNumber().toString()
    }
}