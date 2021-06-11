package co.classplus_find.app

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import co.classplus_find.app.adapters.ViewPagerAdapter
import co.classplus_find.app.databinding.ActivityMainBinding
import co.classplus_find.app.ui.TutorProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener
{
    lateinit var binding: ActivityMainBinding
    lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupUi()
    }

    private fun setupUi(){
        val bnv = binding.bottomNavigation

        bnv.inflateMenu(R.menu.bottom_nav_menu_main_tutor)

        val fragList = ArrayList<Fragment>()
        fragList.add(TutorProfileFragment.newInstance())
//        fragList.add(SwipeViewFragment())
//        fragList.add(ActivityFragment())
        val pagerAdapter = ViewPagerAdapter(fragList, supportFragmentManager)
        viewPager = binding.viewPager
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 3
        bnv.setOnNavigationItemSelectedListener(this)
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account -> viewPager.currentItem = 0
            R.id.fire -> viewPager.currentItem = 1
            R.id.chat -> viewPager.currentItem = 2
        }
        return true
    }
}