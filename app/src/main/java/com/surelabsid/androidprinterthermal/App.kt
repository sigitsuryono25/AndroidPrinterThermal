package com.surelabsid.androidprinterthermal

import android.app.Application
import com.surelabsid.utils.SunmiPrintHelper

//import com.mazenrashed.printooth.Printooth

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        init()
    }

    /**
     * Connect print service through interface library
     */
    private fun init() {
        SunmiPrintHelper.getInstance().initSunmiPrinterService(this)
    }
}