package co.classplus_find.app.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import co.classplus_find.app.R
import co.classplus_find.app.data.models.BatchModel
import co.classplus_find.app.data.models.CourseModel
import co.classplus_find.app.databinding.ItemCourseBinding

class CourseAdapter(var context: Context, var courseList: ArrayList<CourseModel>, var isTutor: Int) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private lateinit var onCourseBought: OnCourseBought

    init {
        if (context is OnCourseBought)
            onCourseBought = context as OnCourseBought
    }

    inner class CourseViewHolder(itemsView: ItemCourseBinding) : RecyclerView.ViewHolder(itemsView.root) {
        var name = itemsView.name
        var desc = itemsView.desc
        var price = itemsView.price
        var join = itemsView.btn
    }

    fun setList(courseList: ArrayList<CourseModel>) {
        this.courseList = courseList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding: ItemCourseBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_course, parent, false);
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val batch = courseList[position]
        holder.apply {
            name.text = batch.name
            desc.text = batch.description
            price.text = batch.price

            if(isTutor == 1){
                join.visibility = View.GONE
            }
            else{
                join.visibility = View.VISIBLE
            }

            holder.join.setOnClickListener {
                onCourseBought.onCourseJoined(courseList[position],position)
            }
        }
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    interface OnCourseBought {
        fun onCourseJoined(course: CourseModel, adapterPosition: Int)
    }
}