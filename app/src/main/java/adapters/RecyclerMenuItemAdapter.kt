package adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eronka.fincan.R
import com.squareup.picasso.Picasso
import datamodels.MenuItem
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

class RecyclerMenuItemAdapter(
    context: Context,
    private var itemList: ArrayList<MenuItem>,
    private val loadDefaultImage: Int,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerMenuItemAdapter.ItemListViewHolder>(), Filterable {

    private var fullMenuList = ArrayList<MenuItem>(itemList)

    interface OnItemClickListener {
        fun onItemClick(item: MenuItem)
        fun onPlusBtnClick(item: MenuItem)
        fun onMinusBtnClick(item: MenuItem)
    }

    class ItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.item_image)
        val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemStars: TextView = itemView.findViewById(R.id.item_stars)
        val itemCategory: TextView = itemView.findViewById(R.id.item_category)
        val itemPrice: TextView = itemView.findViewById(R.id.item_price)
        val increase : ImageView = itemView.findViewById(R.id.increase_item_quantity_iv)
        val decrease : ImageView = itemView.findViewById(R.id.decrease_item_quantity_iv)
        val itemQuantity: TextView = itemView.findViewById(R.id.item_quantity_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_menu_item, parent, false)
        fullMenuList = ArrayList<MenuItem>(itemList)
        return ItemListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val currentItem = itemList[position]
        if (currentItem.imageUrl != null && currentItem.imageUrl != ""){
            Picasso.get().load(currentItem.imageUrl).into(holder.itemImage)
        }

        holder.itemName.text = currentItem.itemName
        holder.itemStars.text = currentItem.itemStars.toString()
        holder.itemCategory.text = currentItem.itemCategory
        if(currentItem.quantity == 0){
            holder.itemPrice.text = currentItem.itemPrice.toString() + "₺"
        }else{
            holder.itemPrice.text = (currentItem.itemPrice * currentItem.quantity).toString() + "₺"
        }
        holder.itemPrice.text = currentItem.itemPrice.toString() + "₺"
        holder.itemQuantity.text = currentItem.quantity.toString()
        holder.increase.setOnClickListener {
            //val n = currentItem.quantity
            //holder.itemQuantityTV.text = (n+1).toString()
            listener.onPlusBtnClick(currentItem)
        }

        holder.decrease.setOnClickListener {
            //val n = currentItem.quantity
            //if (n == 0) return@setOnClickListener
            //holder.itemQuantityTV.text = (n-1).toString()
            listener.onMinusBtnClick(currentItem)
        }
        holder.itemView.setOnClickListener {
            listener.onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun filterList(filteredList: ArrayList<MenuItem>) {
        itemList = filteredList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return searchFilter;
    }

    private val searchFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<MenuItem>()
            if (constraint!!.isEmpty()) {
                filteredList.addAll(fullMenuList)
            } else {
                val filterPattern = constraint.toString().lowercase(Locale.ROOT).trim()

                for (item in fullMenuList) {
                    if (item.itemName.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            itemList.clear()
            itemList.addAll(results!!.values as ArrayList<MenuItem>)
            notifyDataSetChanged()
        }
    }
}