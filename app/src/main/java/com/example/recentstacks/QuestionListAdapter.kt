package com.example.recentstacks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recentstacks.models.Item
import java.text.SimpleDateFormat
import java.util.*

class QuestionListAdapter(private val context: Context, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val CONTENT = 0
    private val AD = 1

    private fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
            val netDate = Date(s.toLong() * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    inner class QuestionViewHolder(questionView: View) : RecyclerView.ViewHolder(questionView),
        View.OnClickListener {
        var questionTitle: TextView = questionView.findViewById<TextView>(R.id.tvQuestionTitle)
        var opImage: ImageView = questionView.findViewById<ImageView>(R.id.iOpImage)
        var opName: TextView = questionView.findViewById<TextView>(R.id.tvOpName)
        var postDate: TextView = questionView.findViewById<TextView>(R.id.tvPostDate)

        init {
            questionView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            listener.onItemClick(position)
        }
    }

    class AdViewHolder(adView: View) : RecyclerView.ViewHolder(adView)

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.question_id == newItem.question_id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val vh: RecyclerView.ViewHolder = if (viewType == CONTENT) {
            val view = inflater.inflate(R.layout.question_view, parent, false)
            QuestionViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.ad_view, parent, false)
            AdViewHolder(view)
        }
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == CONTENT) {
            holder as QuestionViewHolder
            val question = differ.currentList[position]
            holder.questionTitle.text = question.title
            holder.opName.text = question.owner.display_name
            holder.postDate.text = getDateTime(question.creation_date.toString())
            Glide.with(context).load(question.owner.profile_image).into(holder.opImage)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList[position].item_type == 1) {
            return AD
        }
        return CONTENT
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}