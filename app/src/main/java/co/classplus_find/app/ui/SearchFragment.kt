package co.classplus_find.app.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import co.classplus_find.app.R
import co.classplus_find.app.databinding.FragmentSearchBinding
import co.classplus_find.app.databinding.FragmentTutorProfileBinding

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding

    companion object{
        fun newInstance() = SearchFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = activity
        return binding.root
    }
}