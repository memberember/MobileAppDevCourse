package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainFragment : Fragment() {

    var request: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val param = arguments!!.getString("param")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_main, container, false)

//view.act1_recview

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val o =
            createRequest("https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Ffeeds.twit.tv%2Fbrickhouse.xml")
                .map {
                    Gson().fromJson(it, MainActivity.FeedApi::class.java)

                }
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

        request = o.subscribe({

            //  преобразовываем фиды в фиды для БД
            val Feed = Feed(
                it.items.mapTo(
                    RealmList<FeedItem>(),
                    { feed ->
                        FeedItem(
                            feed.title,
                            feed.link,
                            feed.thumbnail,
                            feed.description,
                            feed.guid
                        )
                    })
            )

            // удаляем все фиды с бд если такие существуют
            Realm.getDefaultInstance().executeTransaction { realm ->
                val oldList = realm.where(Feed::class.java).findAll()
                if (oldList.size > 0)
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


    fun showRecView() {

        val feed = Realm.getDefaultInstance().executeTransaction { realm ->
            if (!isVisible)
                return@executeTransaction
            val feed = realm.where(Feed::class.java).findAll()
            if (feed.size > 0) {
                act1_recview.adapter = MainActivity.RecAdapter(feed[0]!!.items)
                act1_recview.layoutManager = LinearLayoutManager(activity)
            }
        }

    }

    override fun onDestroyView() {
        request?.dispose()
        super.onDestroyView()
    }
}