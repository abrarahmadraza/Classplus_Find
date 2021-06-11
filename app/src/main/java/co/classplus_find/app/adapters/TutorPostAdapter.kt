//package co.classplus_find.app.adapters
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.RecyclerView
//import co.classplus_find.app.R
//
//
//class TutorPostAdapter(var context: Context, var transactionsList: ArrayList<FeesStudentModel>) : RecyclerView.Adapter<FeesStudentsAdapter.StudentsViewHolder>() {
//
//    lateinit var onStudentClicked: OnStudentClicked
//
//    init {
//        if (fragment is OnStudentClicked)
//            onStudentClicked = fragment
//    }
//
//    inner class StudentsViewHolder(itemsView: ItemFeesBinding) : RecyclerView.ViewHolder(itemsView.root) {
//        var name = itemsView.tvName
//        var amount = itemsView.tvAmount
//        var label = itemsView.tvFeeLabel
//        var dueDate = itemsView.tvDueDate
//        var layout = itemsView.itemFeesLayout
//
//        init {
//            itemsView.root.setOnClickListener {
//                if(adapterPosition != RecyclerView.NO_POSITION)
//                    onStudentClicked.onStudentClicked(transactionsList[adapterPosition], adapterPosition)
//            }
//        }
//    }
//
//    fun setList(transactionsList: ArrayList<FeesStudentModel>) {
//        this.transactionsList = transactionsList
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsViewHolder {
//        val binding: ItemFeesBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_fees, parent, false);
//        return StudentsViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: StudentsViewHolder, position: Int) {
//        val transaction = transactionsList[position]
//        holder.apply {
//            name.text = transaction.name
//            amount.text = transaction.amount.toString()
//            label.text = when {
//                transaction.isPaid == 0 -> fragment.getText(R.string.not_paid)
//                transaction.modeOfPayment == 0 -> fragment.getText(R.string.paid_offline)
//                else -> fragment.getText(R.string.paid_online)
//            }
//            dueDate.text = when {
//                transaction.isPaid == 1 -> StringUtils.getDateTimeFromUtcString(transaction.transactionDateTime, fragment.getString(R.string.date_format_Z_gmt), "MMM dd, YYYY")
//                transaction.isRecurring == 1 -> "Due: " + StringUtils.getDateTimeFromUtcString(transaction.dueDate, fragment.getString(R.string.date_format_Z_gmt), "MMM dd, YYYY")
//                else -> "Total Fees"
//            }
//            if(position == itemCount -1){
//                layout.setPadding(0,0,0, ViewUtils.dpToPx(80F))
//            }
//            else{
//                layout.setPadding(0,0,0, 0)
//            }
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return transactionsList.size
//    }
//
//    interface onDeleteClicked {
//        fun onDeleteClicked(feesStudentModel: FeesStudentModel, adapterPosition: Int)
//    }
//}