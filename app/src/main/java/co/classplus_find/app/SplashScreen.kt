package co.classplus_find.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import co.classplus_find.app.ui.ChooseUserActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            val intent = Intent(this, ChooseUserActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}