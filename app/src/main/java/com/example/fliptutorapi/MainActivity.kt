package com.example.fliptutorapi

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.GsonBuilder

import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {


//    private val textView = findViewById<TextView>(R.id.text_field)
    private val permissions = arrayOf(android.Manifest.permission.INTERNET)
    private val client = OkHttpClient()
//    private var response:Response? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        make_request.setOnClickListener {
            val correctAnswer = findViewById<EditText>(R.id.correct_answer)
            val studentAnswer = findViewById<EditText>(R.id.student_answer)
//            Toast.makeText(this, studentAnswer.text,Toast.LENGTH_LONG).show()
            run("http://kongaevans.pythonanywhere.com/?correct_answer='${correctAnswer.text}'" +
                    "&student_answer='${studentAnswer.text}'")
        }
        clear.setOnClickListener {
            result.text = "Empty Result"
        }

    }

    private fun run(url: String) {
        if (hasNoPermissions()) {
            requestPermission()
        }else {
            val request = Request.Builder()
                .url(url)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    TODO("not implemented") //To change b
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body()?.string()

                    val gson = GsonBuilder().create()

                    val homeFeed = gson.fromJson(body, HomeFeed::class.java)

                    val similarity = homeFeed.similarity

                    runOnUiThread {
                        result.text = "The sentences are ${similarity.toString()} percent similar"
                    }
                }
            }
            )
        }


    }
    private fun hasNoPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, permissions, 0)
    }
}

class HomeFeed( val similarity:Float)
