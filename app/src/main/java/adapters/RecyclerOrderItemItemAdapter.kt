package adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.eronka.fincan.R
import kotlin.collections.ArrayList
import datamodels.OrderItemItem

class RecyclerOrderItemItemAdapter(
    context: Context,
    private var itemList: ArrayList<OrderItemItem>) : RecyclerView.Adapter<RecyclerOrderItemItemAdapter.OrderItemListViewHolder>(){
    private var fullItemList = ArrayList<OrderItemItem>(itemList)

    class OrderItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.order_item_name)
        val price : TextView = itemView.findViewById(R.id.list_cafes_item)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_order, parent, false)
        fullItemList = ArrayList<OrderItemItem>(itemList)
        return OrderItemListViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderItemListViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.itemName.text = currentItem.itemName + " x " + currentItem.quantity.toString()
        holder.price.text = (currentItem.price * currentItem.quantity).toString() + "₺"
    }

    override fun getItemCount(): Int = itemList.size

    fun filterList(filteredList: ArrayList<OrderItemItem>) {
        itemList = filteredList
        notifyDataSetChanged()
    }
}
