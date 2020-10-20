package com.example.myapplication

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.view.*

open class Feed(
    var items: RealmList<FeedItem> = RealmList<FeedItem>()
) : RealmObject()

open class FeedItem(
    var title: String = "",
    var link: String = "",
    var thumbnail: String = "",
    var description: String = ""
) : RealmObject()

class MainActivity : AppCompatActivity() {
    var request: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val o =
            createRequest("https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Ffeeds.bbci.co.uk%2Fnews%2Frss.xml")
                .map {
                    Gson().fromJson(it, FeedApi::class.java)

                }
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

        request = o.subscribe({

            //  преобразовываем фиды в фиды для БД
            val Feed = Feed(
                it.items.mapTo(RealmList<FeedItem>(),
                    { feed ->
                        FeedItem(
                            feed.title,
                            feed.link, feed.thumbnail, feed.description
                        )
                    })
            )

                // удаляем все фиды с бд если такие существуют
            Realm.getDefaultInstance().executeTransaction { realm ->
                val oldList = realm.where(Feed::class.java).findAll()
                if(oldList.size>0)
                    for (item in oldList)
                        item.deleteFromRealm()

                // добавляем в бд фид
                realm.copyToRealm(Feed)

            }
            showRecView()

//                for (item in it.items)
//                    Log.w("test","title: ${item.title}")
            // todo что сделать при успешном запросе
        }, {
            Log.e("test", "", it)
            showRecView()

            // todo обработчик ошибок
        })


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

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

    fun showRecView() {

        val feed = Realm.getDefaultInstance().executeTransaction {realm ->
           val feed =  realm.where(Feed::class.java).findAll()
            if(feed.size>0)
            {
                act1_recview.adapter = RecAdapter(feed[0]!!.items)
                act1_recview.layoutManager = LinearLayoutManager(this)
            }
        }

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

    class FeedApi(
        val items: ArrayList<FeedItemApi>
    )

    class FeedItemApi(
        val title: String,
        val link: String,
        val thumbnail: String,
        val description: String

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
            itemView.item_description.text = item.description
            Picasso.with(itemView.item_thumb.context)
                .load(if (item.thumbnail.isNotBlank()) item.thumbnail else defaultImage)
                .into(itemView.item_thumb)

            itemView.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(item.link)
                itemView.item_thumb.context.startActivity(i)
            }
        }

    }
}