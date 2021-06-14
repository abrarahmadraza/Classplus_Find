package co.classplus_find.app.ui.tutor

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import co.classplus_find.app.MainActivity
import co.classplus_find.app.R
import co.classplus_find.app.adapters.TagsAdapter
import co.classplus_find.app.data.PreferenceHelper
import co.classplus_find.app.data.PreferenceHelper.Companion.PREF_IS_TUTOR
import co.classplus_find.app.databinding.FragmentTutorProfileBinding
import co.classplus_find.app.ui.tutor.BatchActivity.Companion.PARAM_SHOW_ACTION
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class TutorProfileFragment : Fragment(),TagsAdapter.OnTagRemoved {

    lateinit var binding: FragmentTutorProfileBinding
    var ref: DatabaseReference? = null
    private lateinit var mPrefs : SharedPreferences
    var tagsAdapter: TagsAdapter? = null


    var uid: String? = null

    var showAction: Int = 0
    var tagList: ArrayList<String> = ArrayList()
    var role=""
    var user:FirebaseUser?=null
    var designationText =""
    var aboutMe=""
    var highestEducation=""

    companion object{
        var PARAM_UID  = "PARAM_UID"
        fun newInstance(uid: String? = null) = TutorProfileFragment().apply {
            arguments = Bundle().apply {
                putString(PARAM_UID,uid)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_tutor_profile, container, false)
        binding.lifecycleOwner = activity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
        setupListeners()
        setupRecyclerView()
    }

    private fun setupData(){
        mPrefs = (activity as Context).getSharedPreferences(PreferenceHelper.PREF_FILE, Context.MODE_PRIVATE)

        role=if(mPrefs.getInt(PREF_IS_TUTOR,-1) == 1) "teacher" else "student"

        if(arguments?.getString(TimelineFragment.PARAM_UID) != null){
            uid = arguments?.getString(TimelineFragment.PARAM_UID)
            ref = FirebaseDatabase.getInstance()
                .getReference("users/" +uid+"/"+"teacher")
            binding.apply {
                aboutEdit.visibility = View.GONE
                eduEdit.visibility = View.GONE
                addTag.visibility = View.GONE
                nameEdit.visibility = View.GONE
                editContact.visibility = View.GONE
            }
            showAction = 1
        }
        else {
            uid = FirebaseAuth.getInstance().currentUser!!.uid
            ref = FirebaseDatabase.getInstance()
                .getReference("users/" + uid + "/" + role)
        }

        user=FirebaseAuth.getInstance().currentUser
    }

    private fun setupListeners(){
        setupEdit()

        binding.batches.setOnClickListener {
            val intent = Intent(requireContext(), BatchActivity::class.java)
                .putExtra(PARAM_SHOW_ACTION,showAction)
                .putExtra(PARAM_UID,uid)
            startActivity(intent)
        }

        binding.courses.setOnClickListener {
            val intent = Intent(requireContext(), CourseActivity::class.java)
                .putExtra(PARAM_SHOW_ACTION,showAction)
                .putExtra(PARAM_UID,uid)
            startActivity(intent)
        }

        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("photoUrl").exists()) {
                        binding.pic?.let {
                            activity?.let { it1 ->
                                Glide.with(it1)
                                    .load(snapshot.child("photoUrl").value)
                                    .placeholder(R.drawable.profile_pic)
                                    .error(R.drawable.profile_pic)
                                    .into(it)
                            }
                        }
                    }
                    if (snapshot.child("name").exists()) {
                        binding.name?.text= snapshot.child("name").value.toString()
                    }
                    if(snapshot.child("designation").exists()){
                        designationText = snapshot.child("designation").value.toString()
                        binding.designation?.text=designationText
                    }
                    if(snapshot.child("tag").exists()){
                        tagList = snapshot.child("tag").value as ArrayList<String>
                        if(tagList.size > 0) {
                            binding.tagEmpty.visibility = View.GONE
                            tagsAdapter?.setList(tagList)
                        }
                        else{
                            binding.tagEmpty.visibility = View.VISIBLE
                        }
                    }
                    if (snapshot.child("phone").exists()) {
                        binding.phone?.text=snapshot.child("phone").value.toString()
                    }
                    if (snapshot.child("location").exists()) {
                        binding.location?.text=snapshot.child("location").value.toString()
                    }
                    if (snapshot.child("aboutMe").exists()) {
                        aboutMe=snapshot.child("aboutMe").value.toString()
                        binding.aboutText.text=aboutMe
                    }
                    if (snapshot.child("highestEducation").exists()) {
                        highestEducation=snapshot.child("highestEducation").value.toString()
                        binding.highestEduText.text=highestEducation
                    }
                    binding.email?.text=user?.email.toString()

                }else{
                    ref?.child("name")?.setValue(user?.displayName.toString())
                    ref?.child("photoUrl")?.setValue(user?.photoUrl.toString())
                    ref?.child("phone")?.setValue("Enter phone number")
                    ref?.child("designation")?.setValue("Teacher")
                    ref?.child("location")?.setValue("Select Location")
                    ref?.child("aboutMe")?.setValue("Tell students about you")
                    ref?.child("highestEducation")?.setValue("Education")
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupRecyclerView(){
        tagsAdapter = TagsAdapter(this, ArrayList())

        binding.rvTags.apply {
            layoutManager = StaggeredGridLayoutManager(3,LinearLayout.HORIZONTAL)
            adapter = tagsAdapter
            setHasFixedSize(true)
        }

    }

    private fun setupEdit(){
        binding.nameEdit?.setOnClickListener(View.OnClickListener { item: View? ->
            val builder =
                AlertDialog.Builder(activity)
            val view: View =
                LayoutInflater.from(activity).inflate(R.layout.name_edit, null)
            val editText = view.findViewById<EditText>(R.id.newName)
            editText.setText(designationText)
            val submit =
                view.findViewById<Button>(R.id.submit)
            builder.setView(view)
            val dialog = builder.create()
            submit.setOnClickListener { item1: View? ->
                var nameVal=""+editText.text.toString()
                if (nameVal != "") {
                    ref!!.child("designation").setValue(nameVal)
                        .addOnCompleteListener {
                            binding.designation?.setText(nameVal)
                            Toast.makeText(
                                activity,
                                "Updated!",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        }
                } else {
                    Toast.makeText(
                        activity,
                        "Field can not be empty!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dialog.show()
        })

        binding.aboutEdit.setOnClickListener {
            val builder =
                AlertDialog.Builder(activity)
            val view: View =
                LayoutInflater.from(activity).inflate(R.layout.name_edit, null)
            val editText = view.findViewById<EditText>(R.id.newName)
            editText.setText(aboutMe)
            val submit =
                view.findViewById<Button>(R.id.submit)
            view.findViewById<TextView>(R.id.label).text="About me"
            builder.setView(view)
            val dialog = builder.create()
            submit.setOnClickListener { item1: View? ->
                var nameVal=""+editText.text.toString()
                if (nameVal != "") {
                    ref!!.child("aboutMe").setValue(nameVal)
                        .addOnCompleteListener {
                            binding.aboutText?.setText(nameVal)
                            Toast.makeText(
                                activity,
                                "Updated!",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        }
                } else {
                    Toast.makeText(
                        activity,
                        "Field can not be empty!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dialog.show()
        }

        binding.eduEdit.setOnClickListener {
            val builder =
                AlertDialog.Builder(activity)
            val view: View =
                LayoutInflater.from(activity).inflate(R.layout.name_edit, null)
            val editText = view.findViewById<EditText>(R.id.newName)
            editText.setText(highestEducation)
            val submit =
                view.findViewById<Button>(R.id.submit)
            view.findViewById<TextView>(R.id.label).text="Highest Education"
            builder.setView(view)
            val dialog = builder.create()
            submit.setOnClickListener { item1: View? ->
                var nameVal=""+editText.text.toString()
                if (nameVal != "") {
                    ref!!.child("highestEducation").setValue(nameVal)
                        .addOnCompleteListener {
                            binding.highestEduText?.setText(nameVal)
                            Toast.makeText(
                                activity,
                                "Updated!",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        }
                } else {
                    Toast.makeText(
                        activity,
                        "Field can not be empty!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dialog.show()
        }

        binding.addTag.setOnClickListener {
            val builder =
                AlertDialog.Builder(activity)
            val view: View =
                LayoutInflater.from(activity).inflate(R.layout.name_edit, null)
            val editText = view.findViewById<EditText>(R.id.newName)
            editText.setText("")
            val submit =
                view.findViewById<Button>(R.id.submit)
            view.findViewById<TextView>(R.id.label).text="Add Tag"
            builder.setView(view)
            val dialog = builder.create()
            submit.setOnClickListener { item1: View? ->
                var nameVal=""+editText.text.toString()
                if (nameVal != "") {
                    tagList.add(nameVal)
                    ref!!.child("tag").setValue(tagList)
                        .addOnCompleteListener {
                            Toast.makeText(
                                activity,
                                "Updated!",
                                Toast.LENGTH_SHORT
                            ).show()
                            tagsAdapter?.setList(tagList)
                            dialog.dismiss()
                        }
                } else {
                    Toast.makeText(
                        activity,
                        "Field can not be empty!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dialog.show()
        }
    }

    override fun onTagRemoved(tagList: ArrayList<String>, adapterPosition: Int) {
        ref?.child("tag")?.setValue(tagList)
    }
}