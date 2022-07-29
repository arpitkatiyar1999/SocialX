package com.example.socialx.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.socialx.R
import com.example.socialx.databinding.ListLitemNewsInfoBinding
import com.example.socialx.models.Article
import com.example.socialx.ui.WebViewActivity
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.addAll
import kotlin.collections.ArrayList

class ArticlesAdapter(private val context: Context, private val articlesList:ArrayList<Article>):RecyclerView.Adapter<ArticlesAdapter.ViewHolder>(),Filterable{
    private val articleListFull:ArrayList<Article> = ArrayList()
    private var isAdded=false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        initializeList()
        println(articlesList.size)
        println(articleListFull.size)
        return ViewHolder(ListLitemNewsInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    private fun initializeList() {
        if(!isAdded)
        {
            articleListFull.addAll(articlesList)
            isAdded=true
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         var article=articlesList[position]
        with(holder.binding)
        {
            title.text=article.title
            description.text=article.description
            Picasso.get().load(article.urlToImage).placeholder(context.getDrawable(R.drawable.ic_baseline_image_24)!!).into(image)
            source.text=article.source.name
            time.text=getTime(article.publishedAt)
            root.setOnClickListener{
                val intent = Intent(root.context, WebViewActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("url", article.url)
                context.startActivity(intent)
            }
        }

    }
    private fun getTime(date:String):String
    {
        val publishedDate= SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(date)
        val currentDate=SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(Date()))
        var diff:Long=currentDate.time-publishedDate.time
        val diffSeconds = diff / 1000 % 60
        val diffMinutes = diff / (60 * 1000) % 60
        val diffHours = diff / (60 * 60 * 1000)
        return if(diffHours in 24L..47)
            "1 day ago"
        else if(diffHours>=48L)
            "${diffHours/24L} days ago"
        else if(diffHours!=0L)
            "$diffHours hrs ago"
        else if(diffMinutes!=0L)
            "$diffMinutes min ago"
        else
            "$diffSeconds min ago"
    }
    override fun getItemCount():Int = articlesList.size

    class ViewHolder(val binding: ListLitemNewsInfoBinding):RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter =articleFilter
    private val articleFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: ArrayList<Article> = ArrayList()
            if (constraint == null || constraint.isBlank()) {
                filteredList.addAll(articleListFull)
            } else {
                val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim()
                for (item in articleListFull) {
                    if (item.title.lowercase(Locale.ROOT).contains(filterPattern) || item.source.name.lowercase(Locale.getDefault()).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            articlesList.clear()
            articlesList.addAll(results.values as ArrayList<Article>)
            notifyDataSetChanged()
        }
    }

}