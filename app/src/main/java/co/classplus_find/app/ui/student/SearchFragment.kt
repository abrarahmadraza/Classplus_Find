package co.classplus_find.app.ui.student

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import co.classplus_find.app.R
import co.classplus_find.app.databinding.FragmentSearchBinding
import co.classplus_find.app.ui.chat.ChatActivity
import co.classplus_find.app.util.toast
import com.bumptech.glide.Glide
import com.github.siyamed.shapeimageview.CircularImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.util.*

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    var resultList = ArrayList<DataSnapshot>()
    var map: HashMap<String,DataSnapshot> = HashMap()
    var role = ""
    var ref: DatabaseReference? = null

    companion object {
        fun newInstance() = SearchFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = activity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ref=FirebaseDatabase.getInstance().getReference("users")
        ref?.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(snap in snapshot.children){
                        query(snap)
                    }
                }
            }
        })
        init()
    }


    fun query(s: DataSnapshot) {
        var uid=""
        FirebaseAuth.getInstance()?.currentUser?.uid?.let { it1 ->
            uid=it1
        }
        if (uid!=s.key.toString() && s.child("teacher").exists()) {
            map.put(
                s.key.toString(),s
            )
            resultList.add(s)
        }
        Log.i("query", resultList.toString())
    }

    fun init() {
        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    if (!TextUtils.isEmpty(query)) {
                        val v: View = getLayoutInflater().inflate(R.layout.loading, null)
                        binding.container.removeAllViews()
                        binding.container.addView(v)
                        var tempArr=ArrayList<String>()
                        var tempSnap =ArrayList<DataSnapshot>()
                        for (snap in resultList) {
                            var value = ""
                            if (snap.child("teacher").child("tag").exists())
                                value = snap.child("teacher").child("tag").value.toString()
                            if (snap.child("teacher").child("name").exists())
                                value += snap.child("teacher").child("name").value.toString()
                            if (snap.child("teacher").child("designation").exists())
                                value += snap.child("teacher").child("designation").value.toString()

                            val ratio: Int =
                                FuzzySearch.weightedRatio(query, value)

                            if (ratio > 50) {
                                tempArr.add(
                                    "$ratio:::" + snap.key
                                )
                            }
                        }
                        Log.i("temp", tempArr.toString())
                        if (tempArr.size != 0) {
                            Collections.sort(tempArr)
                            Collections.reverse(tempArr)
                            for (str in tempArr) {
                                val path = str!!.split(":::").toTypedArray()[1]
                                tempSnap.add(map.get(path)!!)
                            }
                            /////////////////////////////////////File Item
                            binding.container.removeAllViews()
                            for (snap in tempSnap) {
                                val view: View =
                                    getLayoutInflater().inflate(R.layout.list_item_person, null)
                                view.findViewById<TextView>(R.id.name).text =
                                    snap.child("teacher").child("name").value.toString()
                                if(snap.child("teacher").child("designation").exists()) {
                                    view.findViewById<TextView>(R.id.designation).text =
                                        snap.child("teacher").child("designation").value.toString()
                                }else{
                                    view.findViewById<TextView>(R.id.designation).visibility=View.GONE
                                }
                                activity?.let{
                                    Glide.with(it)
                                        .load(snap.child("teacher").child("photoUrl").value)
                                        .placeholder(R.drawable.profile_pic)
                                        .error(R.drawable.profile_pic)
                                        .into(view.findViewById<CircularImageView>(R.id.pic))
                                }
                                view.findViewById<Button>(R.id.chat)
                                    .setOnClickListener {
                                        var arr=ArrayList<String>()
                                        FirebaseAuth.getInstance()?.currentUser?.uid?.let { it1 ->
                                            arr.add(
                                                it1
                                            )
                                        }
                                        arr.add(snap.key.toString())
                                        Collections.sort(arr)
                                        var tempStr= "${arr.get(0)}_${arr.get(1)}"
                                        var intent= Intent(activity, ChatActivity::class.java)
                                        intent.putExtra("chatId",tempStr)
                                        intent.putExtra("recipient",snap.key.toString())
                                        startActivity(intent)
                                    }
                                binding.container.addView(view)

                            }
                            /////////////////////////////////////
                        } else {
                            (v.findViewById<View>(R.id.img) as ImageView).visibility = View.GONE
                            (v.findViewById<View>(R.id.text) as TextView).text = "No Item Found!"
                        }

//                    Log.i("temp", String.valueOf(map.get(path)));
                    } else {
                        activity?.toast("No search input!")
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })
    }
}