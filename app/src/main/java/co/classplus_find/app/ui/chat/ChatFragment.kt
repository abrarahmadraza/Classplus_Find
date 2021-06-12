package co.classplus_find.app.ui.chat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import co.classplus_find.app.R
import co.classplus_find.app.data.PreferenceHelper
import co.classplus_find.app.databinding.FragmentChatBinding
import com.bumptech.glide.Glide
import com.github.siyamed.shapeimageview.CircularImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class ChatFragment: Fragment() {

    lateinit var binding: FragmentChatBinding
    var resultList = ArrayList<DataSnapshot>()
    var map: Map<String, DataSnapshot> = HashMap()
    var role=""
    var ref:DatabaseReference?=null
    private lateinit var mPrefs : SharedPreferences

    companion object{
        fun newInstance() = ChatFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_chat, container, false)
        binding.lifecycleOwner = activity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init(){
        mPrefs = (activity as Context).getSharedPreferences(PreferenceHelper.PREF_FILE, Context.MODE_PRIVATE)
        role=if(mPrefs.getInt(PreferenceHelper.PREF_IS_TUTOR,-1) == 1) "teacher" else "student"

        ref = FirebaseDatabase.getInstance()
            .getReference("chats")
        var uid:String = FirebaseAuth.getInstance().currentUser?.uid.toString()
        ref?.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    binding.container.removeAllViews()
                    for(snap in snapshot.children){
                        if(snap.key.toString().contains(uid)){
                            make_item(snap)
                        }
                    }
                    binding.emptyState.visibility = View.GONE
                }else{
                    binding.emptyState.visibility = View.VISIBLE
                }
            }
        })
    }

    fun make_item(s:DataSnapshot){
        var arr: List<String> = s.key.toString().split("_")
        var uid:String = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var recUid=""
        recUid=if(arr.indexOf(uid)==0) arr.get(1) else arr.get(0)
        var tempRef=FirebaseDatabase.getInstance().getReference("users/"+recUid)
        tempRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snap: DataSnapshot) {
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
                view.findViewById<Button>(R.id.chat).visibility=View.GONE
                view.findViewById<ConstraintLayout>(R.id.parent)
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
        })

    }
}