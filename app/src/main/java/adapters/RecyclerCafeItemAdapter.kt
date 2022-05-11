package adapters

import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.eronka.fincan.R
import datamodels.CafeItem
import java.util.*
import kotlin.collections.ArrayList
import com.squareup.picasso.Picasso

class RecyclerCafeItemAdapter (
    var context: Context,
    private var itemList: ArrayList<CafeItem>,
    private val loadDefaultImage: Int,
    val listener: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerCafeItemAdapter.ItemListViewHolder>(), Filterable {
    private var fullItemList = ArrayList<CafeItem>(itemList)

    interface OnItemClickListener {
        fun onItemClick(item: CafeItem)
    }

    class ItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cafeImage: ImageView = itemView.findViewById(R.id.item_image)
        val cafeName: TextView = itemView.findViewById(R.id.item_name)
        val cafeStars: TextView = itemView.findViewById(R.id.item_stars)
        val cafeAddress: TextView = itemView.findViewById(R.id.list_cafes_item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_cafes_item, parent, false)
        fullItemList = ArrayList<CafeItem>(itemList)
        return ItemListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val currentItem = itemList[position]
        if (currentItem.imageUrl != null && currentItem.imageUrl != ""){
            Picasso.get().load(currentItem.imageUrl).into(holder.cafeImage)
        }
        holder.cafeName.text = currentItem.cafeName
        holder.cafeStars.text = currentItem.cafeStars.toString()
        holder.cafeAddress.text = currentItem.cafeAddress
        holder.itemView.setOnClickListener {
            listener.onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun filterList(filteredList: ArrayList<CafeItem>) {
        itemList = filteredList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return searchFilter;
    }

    private val searchFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<CafeItem>()
            if (constraint!!.isEmpty()) {
                filteredList.addAll(fullItemList)
            } else {
                val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()

                for (item in fullItemList) {
                    if (item.cafeName.toLowerCase(Locale.ROOT).contains(filterPattern)) {
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
            itemList.addAll(results!!.values as ArrayList<CafeItem>)
            notifyDataSetChanged()
        }

    }
}