package com.kolibreath.miweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.kolibreath.miweather.custom_recycler_view.MyRecyclerAdapter
import com.kolibreath.miweather.custom_recycler_view.MyRecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setAdapter()
    }

    private fun setAdapter(){
        val myRecyclerView = findViewById<MyRecyclerView>(R.id.mrv)
        val itemCount = 50
        val itemHeight = resources.getDimensionPixelSize(R.dimen.item_height)

        val testList = ArrayList<String>()
        for(i in 0 until itemCount) testList.add("i love muxi $i")
        val myAdapter = object: MyRecyclerAdapter{
            override fun onCreateViewHolder(row: Int, convertView: View?, parent: ViewGroup): View {

                val id = when(getItemViewType(row)) {
                    0 -> R.layout.item_custom_view0
                    1 -> R.layout.item_custom_view1
                    else -> -1
                }

                val resultView = convertView ?: layoutInflater.inflate(id, parent, false)
                if(getItemViewType(row) == 1)
                    resultView.findViewById<TextView>(R.id.tv_item1).text = testList[row]
                return resultView
            }

            override fun onBindViewHolder(row: Int, convertView: View?, parent: ViewGroup):View {
                val id = when(getItemViewType(row)) {
                    0 -> R.layout.item_custom_view0
                    1 -> R.layout.item_custom_view1
                    else -> -1
                }

                val resultView = convertView ?: layoutInflater.inflate(id, parent, false)
                if(getItemViewType(row) == 1)
                    resultView.findViewById<TextView>(R.id.tv_item1).text = testList[row]
                return resultView

            }

            override fun getItemViewType(row: Int): Int = row%2

            override fun getItemViewTypeCount(): Int {
                return 2
            }

            override fun getItemCount(): Int  = itemCount

            override fun getItemHeight(row: Int): Int  = itemHeight
        }
        myRecyclerView.setAdapter(myAdapter)
    }
}