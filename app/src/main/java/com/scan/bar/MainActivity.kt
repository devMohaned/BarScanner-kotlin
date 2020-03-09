package com.scan.bar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent




class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btnScanBarcode -> {
                startActivity(Intent(this@MainActivity,ScannedBarcodeActivity::class.java))
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnScanBarcode.setOnClickListener(this)
    }
}
