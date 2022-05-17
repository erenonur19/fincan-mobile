package adapters;

import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eronka.fincan.R
import kotlin.collections.ArrayList
import com.squareup.picasso.Picasso
import datamodels.OrderItem
import datamodels.OrderItemItem
import kotlin.math.roundToInt

class RecyclerOrderItemAdapter(
        context: Context,
        private var itemList: ArrayList<OrderItem>,
        private val loadDefaultImage: Int,
        ) :
        RecyclerView.Adapter<RecyclerOrderItemAdapter.OrderListViewHolder>(){
        private var fullItemList = ArrayList<OrderItem>(itemList)
        var pool = RecyclerView.RecycledViewPool()
        var applicationContext = context
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_order_item, parent, false)
                fullItemList = ArrayList<OrderItem>(itemList)
                return OrderListViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
                val currentItem = itemList[position]
                if (currentItem.cafeImageUrl != ""){
                        Picasso.get().load(currentItem.cafeImageUrl).into(holder.cafeImage)
                }
                var items : ArrayList<OrderItemItem> = arrayListOf()
                for (item in currentItem.items){
                        var orderItem = OrderItemItem(
                                itemName = item["itemName"] as String,
                                quantity = (item["quantity"] as Long).toInt(),
                                price = (item["itemPrice"] as Double).toFloat()
                        )
                        items.add(orderItem)
                }
                holder.cafeName.text = currentItem.cafeName
                holder.date.text = currentItem.time
                var adapter  = RecyclerOrderItemItemAdapter(applicationContext,items)
                holder.orders.adapter = adapter
                holder.orders.layoutManager = LinearLayoutManager(holder.orders.context)
                holder.orders.setRecycledViewPool(pool)
                adapter.filterList(items)
                holder.subtotal.text = ((currentItem.subtotalPrice * 100).roundToInt() / 100).toString()
        }

        override fun getItemCount(): Int = itemList.size

        fun filterList(filteredList: ArrayList<OrderItem>) {
                itemList = filteredList
                notifyDataSetChanged()
        }

class OrderListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cafeImage: ImageView = itemView.findViewById(R.id.cafe_image)
        val cafeName: TextView = itemView.findViewById(R.id.cafe_name)
        val date: TextView = itemView.findViewById(R.id.item_date)
        val orders: RecyclerView = itemView.findViewById(R.id.order_items_prices)
        val subtotal : TextView = itemView.findViewById(R.id.order_subtotal_price)
        }

}