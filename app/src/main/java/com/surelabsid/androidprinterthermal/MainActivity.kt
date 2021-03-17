package com.surelabsid.androidprinterthermal

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.surelabsid.utils.BluetoothUtil
import com.surelabsid.utils.ESCUtil
import com.surelabsid.utils.SunmiPrintHelper

class MainActivity : AppCompatActivity(), View.OnClickListener {

    var bitmap: Bitmap? = null
    var bitmap1: Bitmap? = null
    var mytype = 0
    var myorientation = 1
    var handler: Handler? = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPrinterStyle()
        setSubTitle()

        if (!BluetoothUtil.isBlueToothPrinter) {
            SunmiPrintHelper.getInstance().setAlign(1)
        } else {
            val send = ESCUtil.alignCenter()
            BluetoothUtil.sendData(send)
        }

        val img = findViewById<ImageView>(R.id.imageView)
        Glide.with(this).asBitmap().load("http://retribusi.server4111.com/pelataran.png")
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    bitmap = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        findViewById<Button>(R.id.print).setOnClickListener{

            if (!BluetoothUtil.isBlueToothPrinter) {
                SunmiPrintHelper.getInstance().printBitmap(bitmap, myorientation)
                SunmiPrintHelper.getInstance().feedPaper()
            } else {
                if (mytype == 0) {
                    BluetoothUtil.sendData(ESCUtil.printBitmap(bitmap1, 0))
                } else if (mytype == 1) {
                    BluetoothUtil.sendData(ESCUtil.selectBitmap(bitmap1, 0))
                } else if (mytype == 2) {
                    BluetoothUtil.sendData(ESCUtil.selectBitmap(bitmap1, 1))
                } else if (mytype == 3) {
                    BluetoothUtil.sendData(ESCUtil.selectBitmap(bitmap1, 32))
                } else if (mytype == 4) {
                    BluetoothUtil.sendData(ESCUtil.selectBitmap(bitmap1, 33))
                }
                BluetoothUtil.sendData(ESCUtil.nextLine(3))
            }
        }


    }

    override fun onClick(view: View?) {

    }

    private fun initPrinterStyle() {
        if (BluetoothUtil.isBlueToothPrinter) {
            BluetoothUtil.sendData(ESCUtil.init_printer())
        } else {
            SunmiPrintHelper.getInstance().initPrinter()
        }
    }

    private fun setSubTitle() {
        val actionBar = supportActionBar
        if (actionBar != null) {
            if (BluetoothUtil.isBlueToothPrinter) {
                actionBar.setSubtitle("bluetooth®")
            } else {
                when (SunmiPrintHelper.getInstance().sunmiPrinter) {
                    SunmiPrintHelper.NoSunmiPrinter -> {
                        actionBar.subtitle = "no printer"
                    }
                    SunmiPrintHelper.CheckSunmiPrinter -> {
                        actionBar.subtitle = "connecting"
                        handler?.postDelayed({ setSubTitle() }, 2000)
                    }
                    SunmiPrintHelper.FoundSunmiPrinter -> {
                        actionBar.subtitle = ""
                    }
                    else -> {
                        SunmiPrintHelper.getInstance().initSunmiPrinterService(this)
                    }
                }
            }
        }
    }
}