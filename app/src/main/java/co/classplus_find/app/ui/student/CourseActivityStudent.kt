package co.classplus_find.app.ui.student

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.classplus_find.app.R
import co.classplus_find.app.adapters.CourseAdapter
import co.classplus_find.app.data.PreferenceHelper
import co.classplus_find.app.data.models.CourseModel
import co.classplus_find.app.databinding.ActivityCourseBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class CourseActivityStudent : AppCompatActivity() {

    lateinit var binding: ActivityCourseBinding
    lateinit var courseAdapter: CourseAdapter
    var ref: DatabaseReference? = null
    private lateinit var mPrefs : SharedPreferences
    var user: FirebaseUser?=null

    var courseList: ArrayList<CourseModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_course)
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
        courseAdapter = CourseAdapter(this,ArrayList(),0)

        binding.addPost.visibility = View.GONE

        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(this@CourseActivityStudent)
            adapter = courseAdapter
            setHasFixedSize(true)
        }
    }

    fun setupRemote(){
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("courses").exists()) {
                        courseList.clear()
                        for (snap in snapshot.child("courses").children){
                            courseList.add(CourseModel(snap.child("name").value.toString(),
                                snap.child("description").value.toString(),
                                snap.child("price").value.toString()
                            ))
                        }

                        if(courseList.size > 0){
                            binding.rvPosts.visibility = View.VISIBLE
                            courseAdapter.setList(courseList)
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
        courseList.add(CourseModel("11th batch","New batch for class 11", "500"))

        ref?.child("courses")?.setValue(courseList)
    }
}