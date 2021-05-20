package com.mobit.mobit.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobit.mobit.R
import com.mobit.mobit.data.CoinInfo
import java.text.DecimalFormat

class FragmentCoinListAdapter(
    var items: ArrayList<CoinInfo>,
    var filteredItems: ArrayList<CoinInfo>
) :
    RecyclerView.Adapter<FragmentCoinListAdapter.ViewHolder>() {

    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClicked(view: View, coinInfo: CoinInfo)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val korCoinName: TextView
        val engCoinName: TextView
        val realTimePrice: TextView
        val changeRate: TextView
        val totalTradePrice: TextView

        init {
            korCoinName = itemView.findViewById(R.id.korCoinName)
            engCoinName = itemView.findViewById(R.id.engCoinName)
            realTimePrice = itemView.findViewById(R.id.realTimePrice)
            changeRate = itemView.findViewById(R.id.changeRate)
            totalTradePrice = itemView.findViewById(R.id.totalTradePrice)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_coinlist_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val formatter = DecimalFormat("###,###")
        val changeFormatter = DecimalFormat("###,###.##")

        holder.korCoinName.text = filteredItems[position].name
        holder.engCoinName.text = filteredItems[position].code.split('-')[1] + "/KRW"
        holder.realTimePrice.text = formatter.format(filteredItems[position].price.endPrice.toInt())
        holder.changeRate.text =
            changeFormatter.format(filteredItems[position].price.changeRate * 100) + "%"
        var temp = (filteredItems[position].price.totalTradePrice24 / 100000).toInt()
        holder.totalTradePrice.text = formatter.format(temp) + "백만"

        if (filteredItems[position].price.changeRate > 0) {
            holder.realTimePrice.setTextColor(Color.parseColor("#bd4e3a"))
            holder.changeRate.setTextColor(Color.parseColor("#bd4e3a"))
        } else if (filteredItems[position].price.changeRate < 0) {
            holder.realTimePrice.setTextColor(Color.parseColor("#135fc1"))
            holder.changeRate.setTextColor(Color.parseColor("#135fc1"))
        } else {
            holder.realTimePrice.setTextColor(Color.parseColor("#FFFFFF"))
            holder.changeRate.setTextColor(Color.parseColor("#FFFFFF"))
        }
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }
}