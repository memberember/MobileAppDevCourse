package com.example.myapplication

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.view.*

open class Feed(
    var items: RealmList<FeedItem> = RealmList<FeedItem>()
) : RealmObject()

open class FeedItem(
    var title: String = "",
    var link: String = "",
    var thumbnail: String = "",
    var description: String = "",
    var guid: String = ""
) : RealmObject()


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        // если активити не восстановилось то восстанавливаем его
        if (savedInstanceState == null) {

            // если нужно передать что то при создании активити, то сделаем так
            val bundle = Bundle()
            bundle.putString("param", "value")
            val f = MainFragment()
            f.arguments = bundle


            this@MainActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_place, f).commitAllowingStateLoss()
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
//        request?.dispose()
        super.onDestroy()
    }


    class FeedApi(
        val items: ArrayList<FeedItemApi>
    )

    class FeedItemApi(
        val title: String,
        val link: String,
        val thumbnail: String,
        val description: String,
        val guid: String
    )

    class Adapter(val items: ArrayList<FeedItemApi>) : BaseAdapter() {
        override fun getView(position: Int, converView: View?, parent: ViewGroup?): View {

            val inflater = LayoutInflater.from(parent!!.context)

            // указываем какую вьюху использовать, куда вставлять и заменять ли при вставке
            val view = converView ?: inflater.inflate(
                R.layout.list_item,
                parent,
                false
            )
            val item = getItem(position) as FeedItemApi
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

    class RecAdapter(val items: RealmList<FeedItem>) : RecyclerView.Adapter<RecHolder>() {
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

            val item = items[position]!!
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
            itemView.item_description.text = Html.fromHtml(item.description)
            Picasso.with(itemView.item_thumb.context)
                .load(if (item.thumbnail.isNotBlank()) item.thumbnail else defaultImage)
                .into(itemView.item_thumb)

            itemView.setOnClickListener {
//                val i = Intent(Intent.ACTION_VIEW)
//                i.data = Uri.parse(item.link)
//                (itemView.item_thumb.context as MainActivity).showArticle(item.link)
                (itemView.item_thumb.context as MainActivity).playMusic(item.guid)
            }
        }

    }

    private fun playMusic(url: String) {
        val i = Intent(this, PlayService::class.java)
        Log.w("Debug", url)

        // todo кастыль потому что не грузит с апишки
        var uri = "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3"
        i.putExtra("mp3", uri)
        startService(i)
    }

    fun showArticle(url: String) {
        // если активити не восстановилось то восстанавливаем его

        // если нужно передать что то при создании активити, то сделаем так
        val bundle = Bundle()
        bundle.putString("url", url)
        val f = SecondFragment()
        f.arguments = bundle

//        fragment_place2

        if (fragment_place2 != null) {
            fragment_place2.visibility = View.VISIBLE
            this@MainActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_place2, f).commitAllowingStateLoss()
        } else
            this@MainActivity.getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_place, f).addToBackStack("main").commitAllowingStateLoss()
    }
}