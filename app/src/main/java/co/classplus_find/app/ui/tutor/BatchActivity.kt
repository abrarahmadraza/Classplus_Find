package co.classplus_find.app.ui.tutor

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import co.classplus_find.app.R
import co.classplus_find.app.adapters.BatchAdapter
import co.classplus_find.app.data.PreferenceHelper
import co.classplus_find.app.data.models.BatchModel
import co.classplus_find.app.databinding.ActivityBatchBinding
import co.classplus_find.app.databinding.FragmentTutorProfileBinding
import co.classplus_find.app.ui.tutor.TutorProfileFragment.Companion.PARAM_UID
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class BatchActivity : AppCompatActivity() {

    lateinit var binding: ActivityBatchBinding
    lateinit var batchAdapter: BatchAdapter
    var ref: DatabaseReference? = null
    private lateinit var mPrefs : SharedPreferences
    var user: FirebaseUser?=null

    var uid: String? = null

    var showAction: Int = 0

    var batchList: ArrayList<BatchModel> = ArrayList()

    companion object{
        var PARAM_SHOW_ACTION = "PARAM_SHOW_ACTION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_batch)
        setupData()
        setupUi()
        setupListeners()
        setupRemote()
    }

    private fun setupData(){
        mPrefs = this.getSharedPreferences(PreferenceHelper.PREF_FILE, Context.MODE_PRIVATE)

        uid = intent.getStringExtra(PARAM_UID)

        ref = FirebaseDatabase.getInstance()
            .getReference("users/" + uid+"/teacher")

        showAction = intent.getIntExtra(PARAM_SHOW_ACTION,0)

        if(showAction == 1){
            binding.addPost.visibility = View.GONE
        }

        user= FirebaseAuth.getInstance().currentUser
    }

    private fun setupUi(){
        batchAdapter = BatchAdapter(this,ArrayList(),showAction)

        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(this@BatchActivity)
            adapter = batchAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupRemote(){
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("batches").exists()) {
                        batchList.clear()
                        for (snap in snapshot.child("batches").children){
                            batchList.add(BatchModel(snap.child("name").value.toString(),
                                snap.child("description").value.toString(),
                                snap.child("date").value.toString()
                                ))
                        }

                        if(batchList.size > 0){
                            binding.rvPosts.visibility = View.VISIBLE
                            batchAdapter.setList(batchList)
                            binding.emptyState.visibility = View.GONE
                        }
                        else{
                            binding.emptyState.visibility = View.VISIBLE
                            binding.rvPosts.visibility = View.GONE
                        }
                    }
                }else{
                    binding.emptyState.visibility = View.VISIBLE
                    binding.rvPosts.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupListeners(){
        binding.addPost.setOnClickListener{
            createPost()
        }
    }

    private fun createPost(){
        batchList.add(BatchModel("11th batch","New batch for class 11", "12-10-21"))

        ref?.child("batches")?.setValue(batchList)
    }
}