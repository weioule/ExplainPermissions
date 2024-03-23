package com.weioule.explainpermissionsutil

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by weioule
 * on 2023/12/16
 */
class ExplainAdapter(context: Context, list: MutableList<ExplainBean>) :
    RecyclerView.Adapter<ExplainAdapter.ViewHolder>() {

    private var context = context
    private var list: MutableList<ExplainBean> = list

    fun replaceData(data: MutableList<ExplainBean>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View =
            LayoutInflater.from(context).inflate(R.layout.item_explain_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = list[position].name + "使用说明:"
        holder.content.text = list[position].explain
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var content: TextView

        init {
            title = itemView.findViewById(R.id.tv_title) as TextView
            content = itemView.findViewById(R.id.tv_content) as TextView
        }
    }
}