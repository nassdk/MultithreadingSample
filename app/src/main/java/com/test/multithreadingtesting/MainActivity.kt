package com.test.multithreadingtesting

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var title: TextView
    private lateinit var btnClick: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initClick()

        doWork()
    }

    private fun initViews() {

//        btnClick = findViewById(R.id.btnClick)
//        title = findViewById(R.id.tvTitle)
    }

    private fun initClick() {

        btnClick.setOnClickListener {
            title.text = (System.currentTimeMillis()).toString()
        }
    }

    private fun doWork() {

//        Thread {
//            while (true) {
//                Thread.sleep(1000)
//                Log.e(TAG, "event")
//            }
//        }.start()

        thread {
            while (true) {
                Thread.sleep(1000)

                title.post {
                    title.text = (System.currentTimeMillis()).toString()
                }
//                title.text = (System.currentTimeMillis()).toString()
                Log.e(TAG, "event")
            }
        }
    }

    companion object {
        private const val TAG = "MainActivityLogger"
    }
}