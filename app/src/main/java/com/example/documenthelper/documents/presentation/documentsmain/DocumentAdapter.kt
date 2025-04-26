package com.example.documenthelper.documents.presentation.documentsmain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.documenthelper.R
import com.example.documenthelper.databinding.DocumentRcViewItemBinding
import com.example.documenthelper.documents.data.room.DocumentEntity

class DocumentAdapter(private val listener : OnDocumentClickLisnter) : RecyclerView.Adapter<DocumentAdapter.ViewHolder>() {

    val list : ArrayList<DocumentEntity> = arrayListOf()

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        private val binding = DocumentRcViewItemBinding.bind(view)

        fun bind(document: DocumentEntity, listener : OnDocumentClickLisnter) = with(binding) {

            documentNameTV.text = document.name

            itemView.setOnClickListener {
                listener.onClick(document)
            }
        }
    }

    fun update(newList : ArrayList<DocumentEntity>) {
        val diffUtil = DiffUtilCallBack(list, newList)
        val diff = DiffUtil.calculateDiff(diffUtil)

        list.clear()
        list.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.document_rc_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], listener)
    }

    class DiffUtilCallBack(
        private val old : List<DocumentEntity>,
        private val new : List<DocumentEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = old.size

        override fun getNewListSize(): Int = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = old[oldItemPosition]
            val newItem = new[newItemPosition]

            return oldItem == newItem

        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = old[oldItemPosition]
            val newItem = new[newItemPosition]

            return oldItem == newItem
        }

    }

    interface OnDocumentClickLisnter {
        fun onClick(document: DocumentEntity)
    }
}