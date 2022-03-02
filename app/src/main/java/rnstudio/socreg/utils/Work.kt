package rnstudio.socreg.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class Work: CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    fun setRegServiceListener(){

    }

    fun start(): Job {
        val job = launch {

        }

        return  job
    }
}