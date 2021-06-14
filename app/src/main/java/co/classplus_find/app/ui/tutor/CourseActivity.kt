package co.classplus_find.app.ui.tutor

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.classplus_find.app.R
import co.classplus_find.app.adapters.BatchAdapter
import co.classplus_find.app.adapters.CourseAdapter
import co.classplus_find.app.data.PreferenceHelper
import co.classplus_find.app.data.models.BatchModel
import co.classplus_find.app.data.models.CourseModel
import co.classplus_find.app.databinding.ActivityBatchBinding
import co.classplus_find.app.databinding.ActivityCourseBinding
import co.classplus_find.app.util.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class CourseActivity : AppCompatActivity(),CourseAdapter.OnCourseBought {

    lateinit var binding: ActivityCourseBinding
    lateinit var courseAdapter: CourseAdapter
    var ref: DatabaseReference? = null
    private lateinit var mPrefs : SharedPreferences
    var user: FirebaseUser?=null


    var uid: String? = null

    var showAction: Int = 0

    var courseList: ArrayList<CourseModel> = ArrayList()

    companion object{
        var PARAM_SHOW_ACTION = "PARAM_SHOW_ACTION"
    }

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


        uid = intent.getStringExtra(TutorProfileFragment.PARAM_UID)

        ref = FirebaseDatabase.getInstance()
            .getReference("users/" + uid+"/teacher")

        showAction = intent.getIntExtra(BatchActivity.PARAM_SHOW_ACTION,0)

        if(showAction == 1){
            binding.addPost.visibility = View.GONE
        }

        user= FirebaseAuth.getInstance().currentUser
    }

    private fun setupUi(){
        courseAdapter = CourseAdapter(this,ArrayList(),showAction)

        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(this@CourseActivity)
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
        courseList.add(CourseModel("Introductory Physics","Basic Introduction of Physics", "500"))

        ref?.child("courses")?.setValue(courseList)
    }

    override fun onCourseJoined(course: CourseModel, adapterPosition: Int) {
        this.toast("Bought "+course.name)
    }
}