package com.example.myapplication

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    var request: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        act1_text.setTextColor(0xffffb000.toInt())
        act1_text.setOnClickListener {
//            val i = Intent(this, SecondActivity::class.java)
//            i.putExtra("tag", act1_text.text)
//            startActivityForResult(i, 0)

            val o =
                createRequest("https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Ffeeds.bbci.co.uk%2Fnews%2Frss.xml")
                    .map {
                        Gson().fromJson(it,Feed::class.java)

                    }
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

            request = o.subscribe({
                for (item in it.items)
                    Log.w("test","title: ${item.title}")
                // todo что сделать при успешном запросе
            }, {
                Log.e("test","",it)
                // todo обработчик ошибок
            })

        }
        Log.e("Debug", "onCreate")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            val str = data.getStringExtra("tag2")
            act1_text.setText(str)
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        request?.dispose()
        super.onDestroy()
    }

    class Feed(
        val items: ArrayList<FeedItem>
    ){

    }

    class FeedItem(
        val title: String,
        val link: String,
        val thumbnail: String,
        val description: String

    ){

    }
}