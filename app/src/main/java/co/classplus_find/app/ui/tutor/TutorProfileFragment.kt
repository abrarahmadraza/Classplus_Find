package co.classplus_find.app.ui.tutor

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import co.classplus_find.app.R
import co.classplus_find.app.data.PreferenceHelper
import co.classplus_find.app.data.PreferenceHelper.Companion.PREF_IS_TUTOR
import co.classplus_find.app.databinding.FragmentTutorProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class TutorProfileFragment : Fragment() {

    lateinit var binding: FragmentTutorProfileBinding
    var ref: DatabaseReference? = null
    private lateinit var mPrefs : SharedPreferences


    var role=""
    var user:FirebaseUser?=null
    var displayName =""

    companion object{
        fun newInstance() = TutorProfileFragment().apply {
            arguments = Bundle().apply {
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
    }

    private fun setupData(){
        mPrefs = (activity as Context).getSharedPreferences(PreferenceHelper.PREF_FILE, Context.MODE_PRIVATE)

        role=if(mPrefs.getInt(PREF_IS_TUTOR,-1) == 1) "teacher" else "student"

        ref = FirebaseDatabase.getInstance()
            .getReference("users/" + FirebaseAuth.getInstance().currentUser!!.uid+"/"+role)

        user=FirebaseAuth.getInstance().currentUser
    }

    private fun setupListeners(){

        binding.nameEdit?.setOnClickListener(View.OnClickListener { item: View? ->
            val builder =
                AlertDialog.Builder(activity)
            val view: View =
                LayoutInflater.from(activity).inflate(R.layout.name_edit, null)
            val editText = view.findViewById<EditText>(R.id.newName)
            editText.setText(displayName)
            val submit =
                view.findViewById<Button>(R.id.submit)
            builder.setView(view)
            val dialog = builder.create()
            submit.setOnClickListener { item1: View? ->
                var nameVal=""+editText.text.toString()
                if (nameVal != "") {
                    ref!!.child("name").setValue(nameVal)
                        .addOnCompleteListener {
                            binding.name?.setText(nameVal)
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
                        displayName = snapshot.child("name").value.toString()
                        binding.name?.text=displayName
                    }
                    if (snapshot.child("phone").exists()) {
                        binding.phone?.text=snapshot.child("phone").value.toString()
                    }
                    if (snapshot.child("location").exists()) {
                        binding.location?.text=snapshot.child("location").value.toString()
                    }
                    binding.email?.text=user?.email.toString()

                }else{

                    ref?.child("name")?.setValue(user?.displayName.toString())
                    ref?.child("photoUrl")?.setValue(user?.photoUrl.toString())
                    ref?.child("phone")?.setValue("---")
                    ref?.child("location")?.setValue("---")
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}