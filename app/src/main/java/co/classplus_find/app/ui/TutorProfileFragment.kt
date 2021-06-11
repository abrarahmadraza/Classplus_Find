package co.classplus_find.app.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import co.classplus_find.app.R
import co.classplus_find.app.databinding.FragmentTutorProfileBinding

class TutorProfileFragment : Fragment() {

    lateinit var binding: FragmentTutorProfileBinding

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
}