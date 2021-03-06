package co.classplus_find.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import co.classplus_find.app.adapters.ViewPagerAdapter
import co.classplus_find.app.data.PreferenceHelper
import co.classplus_find.app.data.PreferenceHelper.Companion.PREF_IS_TUTOR
import co.classplus_find.app.databinding.ActivityMainBinding
import co.classplus_find.app.ui.chat.ChatFragment
import co.classplus_find.app.ui.signup.ChooseUserActivity
import co.classplus_find.app.ui.student.SearchFragment
import co.classplus_find.app.ui.student.StudentProfileFragment
import co.classplus_find.app.ui.tutor.TimelineFragment
import co.classplus_find.app.ui.tutor.TutorProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener
{
    lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager
    private lateinit var mPrefs : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        setupData()
        setupUi()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.logout -> {
                Firebase.auth.signOut()
                val intent = Intent(this, ChooseUserActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setupUi(){
        val fragList: ArrayList<Fragment> = ArrayList()
        val bnv = binding.bottomNavigation

        if(mPrefs.getInt(PREF_IS_TUTOR,-1) == 1){
            bnv.inflateMenu(R.menu.bottom_nav_menu_main_tutor)
            fragList.add(TutorProfileFragment.newInstance())
            fragList.add(TimelineFragment.newInstance())
            fragList.add(ChatFragment.newInstance())
        }
        else{
            bnv.inflateMenu(R.menu.bottom_nav_menu_main_student)
            fragList.add(StudentProfileFragment.newInstance())
            fragList.add(SearchFragment.newInstance())
            fragList.add(ChatFragment.newInstance())
        }


        val pagerAdapter = ViewPagerAdapter(fragList, supportFragmentManager)
        viewPager = binding.viewPager
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 3
        bnv.setOnNavigationItemSelectedListener(this)
    }

    private fun setupData(){
        mPrefs = (this as Context).getSharedPreferences(PreferenceHelper.PREF_FILE, Context.MODE_PRIVATE)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account -> viewPager.currentItem = 0
            R.id.fire -> viewPager.currentItem = 1
            R.id.chat -> viewPager.currentItem = 2
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var viewPager=findViewById<ViewPager>(R.id.view_pager)
        var fragment=supportFragmentManager.fragments.get(viewPager.currentItem)
        fragment.onActivityResult(requestCode, resultCode, data)
    }
}