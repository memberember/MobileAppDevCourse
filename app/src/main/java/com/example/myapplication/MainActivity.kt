package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    var request: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val o =
            createRequest("https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Ffeeds.bbci.co.uk%2Fnews%2Frss.xml")
                .map {
                    Gson().fromJson(it, Feed::class.java)

                }
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

        request = o.subscribe({

            showRecView(it.items)
//                for (item in it.items)
//                    Log.w("test","title: ${item.title}")
            // todo что сделать при успешном запросе
        }, {
            Log.e("test", "", it)
            // todo обработчик ошибок
        })


    }

//    fun showLinearLayout(feedList: ArrayList<FeedItem>) {
//
//        // создается инфлейтер
//        val inflater = layoutInflater
//
//        // пробегаемся по списку фидов
//        for (f in feedList) {
//
//            // указываем какую вьюху использовать, куда вставлять и заменять ли при вставке
//            val view = inflater.inflate(R.layout.list_item, act1_list, false)
//            view.item_title.text = f.title
//
//            // добавляем вьюху в лайнир
//            act1_list.addView(view)
//        }
//    }


//    fun showListView(feedList: ArrayList<FeedItem>) {
//        act1_recview.adapter = Adapter(feedList)
//    }

    fun showRecView(feedList: ArrayList<FeedItem>) {
        act1_recview.adapter = RecAdapter(feedList)
        act1_recview.layoutManager = LinearLayoutManager(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            val str = data.getStringExtra("tag2")
//            act1_text.setText(str)
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
    )

    class FeedItem(
        val title: String,
        val link: String,
        val thumbnail: String,
        val description: String

    )

    class Adapter(val items: ArrayList<FeedItem>) : BaseAdapter() {
        override fun getView(position: Int, converView: View?, parent: ViewGroup?): View {

            val inflater = LayoutInflater.from(parent!!.context)

            // указываем какую вьюху использовать, куда вставлять и заменять ли при вставке
            val view = converView ?: inflater.inflate(
                R.layout.list_item,
                parent,
                false
            )
            val item = getItem(position) as FeedItem
            view.item_title.text = item.title
            return view

        }

        override fun getItem(position: Int): Any {
            return items[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return items.size
        }

    }

    class RecAdapter(val items: ArrayList<FeedItem>) : RecyclerView.Adapter<RecHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecHolder {

            val inflater = LayoutInflater.from(parent!!.context)

            // указываем какую вьюху использовать, куда вставлять и заменять ли при вставке
            val view = inflater.inflate(
                R.layout.list_item,
                parent,
                false
            )
            return RecHolder(view)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: RecHolder, position: Int) {

            val item = items[position] as FeedItem
            holder?.bind(item)
        }

        // можно сделать пагинацию при помощи элементов, к примеру
        // когда тип элемента = 1 то значит что нужно выводить новую порцию данных
        override fun getItemViewType(position: Int): Int {
            return super.getItemViewType(position)
        }

    }

    class RecHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: FeedItem) {
            val defaultImage = "https://s.4pda.to/EmSL2n4fy6AgWnDxCWgsWIP5oDmaNp0Uxhs8.jpg"
            itemView.item_title.text = item.title
            itemView.item_description.text = item.description
            Picasso.with(itemView.item_thumb.context).load(if(item.thumbnail.isNotBlank()) item.thumbnail else defaultImage).into(itemView.item_thumb)

            itemView.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(item.link)
                itemView.item_thumb.context.startActivity(i)
            }
        }

    }
}