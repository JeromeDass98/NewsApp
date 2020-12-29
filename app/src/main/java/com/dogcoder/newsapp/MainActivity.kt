package com.dogcoder.newsapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.getbase.floatingactionbutton.FloatingActionsMenu
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),NewsItemClicked,PopupMenu.OnMenuItemClickListener {

    private lateinit var adapter:NewsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchData()
        adapter = NewsListAdapter(this)
        recyclerView.adapter = adapter
    }

    private fun fetchData(url:String=getString(R.string.url)+"in&"+getString(R.string.apiKey)) {
        val jsonObjectRequest = object: JsonObjectRequest(
            Method.GET,
            url,
            null,
            Response.Listener {
                val newsJsonArray = it.getJSONArray("articles")
                val newsArray = ArrayList<News>()
                for(i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("author"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage")
                    )
                    newsArray.add(news)
                }
                adapter.updateNews(newsArray)
            },
            Response.ErrorListener {
            }

        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    fun cnPopUp(view:View){
        val popupMenu = PopupMenu(this,view)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.inflate(R.menu.county_list)
        popupMenu.show()
    }

    override fun onItemClicked(item: News) {
        val builder = CustomTabsIntent.Builder()
        builder.setStartAnimations(this,R.anim.slide_in_right, R.anim.slide_out_left)
        builder.setExitAnimations(this,R.anim.slide_in_left, R.anim.slide_out_right)
        val customTabsIntent = builder.build()
        customTabsIntent.intent.setPackage("com.android.chrome")
        customTabsIntent.launchUrl(this, Uri.parse(item.url))
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        var url = getString(R.string.url)
        val btn:FloatingActionsMenu = findViewById(R.id.filterButton)
        when(item?.itemId){
            R.id.cn_in -> url +="in&"
            R.id.cn_us -> url +="us&"
            R.id.cn_eg -> url +="eg&"
            R.id.cn_es -> url +="es&"
            R.id.cn_cn -> url +="cn&"
            R.id.cn_cu -> url +="cu&"
            R.id.cn_ar -> url +="ar&"
        }
        url+=getString(R.string.apiKey)
        fetchData(url)
        btn.toggle()
        return false
    }
}