package com.scan.bar

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.BarcodeDetector
import android.widget.TextView
import android.view.SurfaceView
import android.widget.Button
import android.content.Intent

import android.net.Uri
import kotlinx.android.synthetic.main.activity_scanned_barcode.*
import java.nio.file.Files.size
import com.google.android.gms.vision.barcode.Barcode
import android.util.SparseArray
import com.google.android.gms.vision.Detector
import android.widget.Toast
import android.view.SurfaceHolder
import android.Manifest.permission
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.util.Log

import java.io.IOException
import java.lang.Exception
import java.lang.IllegalArgumentException


class ScannedBarcodeActivity : AppCompatActivity() {

    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    private val REQUEST_CAMERA_PERMISSION = 201
    var intentData = ""
    var isEmail = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanned_barcode)

        btnAction.setOnClickListener {
            if (intentData.length > 0) {
                if (isEmail)
              /*      startActivity(
                        Intent(
                            this@ScannedBarcodeActivity,
                            EmailActivity::class.java
                        ).putExtra("email_address", intentData)
                    )*/
                Toast.makeText(this@ScannedBarcodeActivity,"It's an email", Toast.LENGTH_LONG)
                else {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(intentData)))
                    }catch(exception: IllegalArgumentException)
                    {
                        Toast.makeText(this@ScannedBarcodeActivity, "Couldn't open the barcode",Toast.LENGTH_LONG);
                        Log.e("ScannedBarcodeActivity", exception.toString());
                    }catch (exception: Exception)
                    {
                        Toast.makeText(this@ScannedBarcodeActivity, "Couldn't open the barcode",Toast.LENGTH_LONG);
                        Log.e("ScannedBarcodeActivity", exception.toString());
                    }
                }
            }
        }
    }


    private fun initialiseDetectorsAndSources() {

        Toast.makeText(applicationContext, "Barcode scanner started", Toast.LENGTH_SHORT).show()

        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        surfaceView!!.getHolder().addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@ScannedBarcodeActivity,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource?.start(surfaceView!!.getHolder())
                    } else {
                        ActivityCompat.requestPermissions(
                            this@ScannedBarcodeActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            REQUEST_CAMERA_PERMISSION
                        )
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }


            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource?.stop()
            }
        })


        barcodeDetector?.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(
                    applicationContext,
                    "To prevent memory leaks barcode scanner has been stopped",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {


                    txtBarcodeValue?.post(Runnable {
                        if (barcodes.valueAt(0).email != null) {
                            txtBarcodeValue?.removeCallbacks(null)
                            intentData = barcodes.valueAt(0).email.address
                            txtBarcodeValue?.setText(intentData)
                            isEmail = true
                            btnAction.text = "ADD CONTENT TO THE MAIL"
                        } else {
                            isEmail = false
                            btnAction.text = "LAUNCH URL"
                            intentData = barcodes.valueAt(0).displayValue
                            txtBarcodeValue?.setText(intentData)

                        }
                    })

                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        cameraSource?.release()
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
    }
}
