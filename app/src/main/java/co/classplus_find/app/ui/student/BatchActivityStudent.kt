package co.classplus_find.app.ui.student

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.classplus_find.app.R
import co.classplus_find.app.adapters.BatchAdapter
import co.classplus_find.app.data.PreferenceHelper
import co.classplus_find.app.data.models.BatchModel
import co.classplus_find.app.databinding.ActivityBatchBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class BatchActivityStudent : AppCompatActivity() {

    lateinit var binding: ActivityBatchBinding
    lateinit var batchAdapter: BatchAdapter
    var ref: DatabaseReference? = null
    private lateinit var mPrefs : SharedPreferences
    var user: FirebaseUser?=null

    var batchList: ArrayList<BatchModel> = ArrayList()

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

        ref = FirebaseDatabase.getInstance()
            .getReference("users/" + FirebaseAuth.getInstance().currentUser!!.uid+"/student")

        user= FirebaseAuth.getInstance().currentUser
    }

    private fun setupUi(){
        batchAdapter = BatchAdapter(this,ArrayList(),0)

        binding.addPost.visibility = View.GONE

        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(this@BatchActivityStudent)
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