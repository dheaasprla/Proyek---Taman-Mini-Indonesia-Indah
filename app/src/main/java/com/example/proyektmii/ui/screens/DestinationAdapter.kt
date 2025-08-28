package com.example.proyektmii.ui.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.proyektmii.R
import com.example.proyektmii.data.Destination

/**
 * Adapter kompatibel dengan DestinationSelectionActivity:
 * konstruktor menerima selectedName/selectedQty serta callbacks onSelect/onIncrease/onDecrease.
 *
 * Untuk mengurangi OutOfMemoryError:
 * - Glide digunakan dengan RequestOptions().override(...) sehingga bitmaps tidak dipanggil dalam ukuran penuh.
 * - Gunakan drawable image yang sudah dioptimalkan bila memungkinkan.
 */
class DestinationAdapter(
    private val items: List<Destination>,
    private var selectedName: String?,
    private var selectedQty: Int,
    private val onSelect: (Destination) -> Unit,
    private val onIncrease: (Destination) -> Unit,
    private val onDecrease: (Destination) -> Unit
) : RecyclerView.Adapter<DestinationAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.ivDestinationImage)
        val tvTitle: TextView = view.findViewById(R.id.tvDestinationTitle)
        val tvPrice: TextView = view.findViewById(R.id.tvDestinationPrice)
        val btnPlus: ImageButton = view.findViewById(R.id.btnPlus)
        val btnMinus: ImageButton = view.findViewById(R.id.btnMinus)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
        val qtyContainer: View = view.findViewById(R.id.qtyContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_destination, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dest = items[position]
        holder.tvTitle.text = dest.name
        holder.tvPrice.text = "Tiket: Rp ${String.format("%,d", dest.price)}"

        val isSelected = selectedName != null && selectedName == dest.name
        // show/hide quantity controls
        holder.qtyContainer.visibility = if (isSelected) View.VISIBLE else View.GONE
        holder.tvQty.text = if (isSelected) selectedQty.toString() else "0"

        // load image with Glide and limit size to reduce memory usage
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            // override size (px) — adjust to fit your item design; smaller sizes reduce memory use
            .override(400, 240)
            .centerCrop()

        // dest.imageRes might be null depending on your model; fallback placeholder if null
        val ctx = holder.itemView.context
        val imageSource = try {
            // Prefer an int resource if present
            dest.imageRes ?: R.drawable.ic_placeholder
        } catch (t: Throwable) {
            R.drawable.ic_placeholder
        }

        Glide.with(ctx)
            .load(imageSource)
            .apply(requestOptions)
            .into(holder.ivImage)

        // Entire item click acts as select (if not selected)
        holder.itemView.setOnClickListener {
            onSelect(dest)
            // update local selectedName/Qty so adapter reflects immediately if host doesn't recreate
            selectedName = dest.name
            selectedQty = 1
            notifyDataSetChanged()
        }

        holder.btnPlus.setOnClickListener {
            onIncrease(dest)
            // if this is the current selected dest, increment local selectedQty for UI
            if (isSelected) {
                selectedQty = selectedQty + 1
            } else {
                selectedName = dest.name
                selectedQty = 1
            }
            notifyDataSetChanged()
        }

        holder.btnMinus.setOnClickListener {
            onDecrease(dest)
            if (isSelected) {
                val newQty = (selectedQty - 1).coerceAtLeast(0)
                if (newQty <= 0) {
                    // deselect
                    selectedName = null
                    selectedQty = 0
                } else selectedQty = newQty
            }
            notifyDataSetChanged()
        }
    }

    /** Optional: helper supaya activity bisa update adapter selection state dari luar. */
    fun updateSelection(selectedName: String?, selectedQty: Int) {
        this.selectedName = selectedName
        this.selectedQty = selectedQty
        notifyDataSetChanged()
    }
}
