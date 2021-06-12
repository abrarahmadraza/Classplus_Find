package co.classplus_find.app.ui.student

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import co.classplus_find.app.R
import co.classplus_find.app.data.PreferenceHelper
import co.classplus_find.app.databinding.FragmentStudentProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class StudentProfileFragment : Fragment() {

    lateinit var binding: FragmentStudentProfileBinding
    var aboutMe=""
    var highestEducation=""
    private lateinit var mPrefs : SharedPreferences

    var ref: DatabaseReference? = null
    var role=""
    var user: FirebaseUser?=null

    companion object{
        fun newInstance() = StudentProfileFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_student_profile, container, false)
        binding.lifecycleOwner = activity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
        setupListeners()
//        setupRecyclerView()
    }

    private fun setupData(){
        mPrefs = (activity as Context).getSharedPreferences(PreferenceHelper.PREF_FILE, Context.MODE_PRIVATE)

        role=if(mPrefs.getInt(PreferenceHelper.PREF_IS_TUTOR,-1) == 1) "teacher" else "student"

        user= FirebaseAuth.getInstance().currentUser
        ref = FirebaseDatabase.getInstance()
            .getReference("users/" + user?.uid+"/"+role)

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

                    if (snapshot.child("aboutMe").exists()) {
                        aboutMe=snapshot.child("aboutMe").value.toString()
                        binding.aboutText.text=aboutMe
                    }
                    if (snapshot.child("currentEducation").exists()) {
                        highestEducation=snapshot.child("currentEducation").value.toString()
                        binding.highestEduText.text=highestEducation
                    }

                }else{

                    ref?.child("name")?.setValue(user?.displayName.toString())
                    ref?.child("photoUrl")?.setValue(user?.photoUrl.toString())
                    ref?.child("phone")?.setValue("Enter phone number")
                    ref?.child("designation")?.setValue("Teacher")
                    ref?.child("location")?.setValue("Select Location")
                    ref?.child("aboutMe")?.setValue("Tell students about you")
                    ref?.child("currentEducation")?.setValue("Education")

                    var temp=FirebaseDatabase.getInstance()
                        .getReference("users/" + user?.uid+"/teacher")
                    temp.child("name").setValue(user?.displayName.toString())
                    temp.child("photoUrl").setValue(user?.photoUrl.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }
    private fun setupListeners(){
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
                    ref!!.child("currentEducation").setValue(nameVal)
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
    }
}