package co.classplus_find.app.ui.tutor

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.classplus_find.app.R
import co.classplus_find.app.adapters.TagsAdapter
import co.classplus_find.app.data.PreferenceHelper
import co.classplus_find.app.databinding.FragmentTimelineBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TimelineFragment: Fragment() {

    lateinit var binding: FragmentTimelineBinding
    var ref: DatabaseReference? = null
    private lateinit var mPrefs : SharedPreferences
    var role=""
    var user: FirebaseUser?=null
    var adapter: TagsAdapter? = null

    companion object{
        fun newInstance() = TimelineFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_timeline, container, false)
        binding.lifecycleOwner = activity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        setupUi()
    }

    private fun setupData(){
        mPrefs = (activity as Context).getSharedPreferences(PreferenceHelper.PREF_FILE, Context.MODE_PRIVATE)

        role=if(mPrefs.getInt(PreferenceHelper.PREF_IS_TUTOR,-1) == 1) "teacher" else "student"

        ref = FirebaseDatabase.getInstance()
            .getReference("users/" + FirebaseAuth.getInstance().currentUser!!.uid+"/"+role)

        user= FirebaseAuth.getInstance().currentUser
    }

    private fun setupUi(){
        adapter = TagsAdapter(this, ArrayList())

        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this.adapter
            setHasFixedSize(true)
        }

        setupPosts()
    }

    private fun setupPosts(){

    }
}