package co.classplus_find.app.ui.student

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import co.classplus_find.app.R
import co.classplus_find.app.ui.tutor.TimelineFragment
import co.classplus_find.app.ui.tutor.TutorProfileFragment

class Details : AppCompatActivity() {
    var uid=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        uid=intent?.extras?.getString("uid").toString()
        findViewById<LinearLayout>(R.id.profile).setOnClickListener {
            findViewById<FrameLayout>(R.id.frame).visibility = View.VISIBLE
            var fragment = TutorProfileFragment.newInstance(uid)
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, fragment, "Fragment2")
                .addToBackStack("Fragment2")
                .commit()
        }

        findViewById<LinearLayout>(R.id.timeline).setOnClickListener {
            findViewById<FrameLayout>(R.id.frame).visibility = View.VISIBLE
            var fragment = TimelineFragment.newInstance(uid)
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, fragment, "Fragment1")
                .addToBackStack("Fragment1")
                .commit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(supportFragmentManager.backStackEntryCount == 0){
            findViewById<FrameLayout>(R.id.frame).visibility = View.GONE
        }
    }
}