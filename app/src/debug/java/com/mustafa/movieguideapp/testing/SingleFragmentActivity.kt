package com.mustafa.movieguideapp.testing

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentContainerView
import com.mustafa.movieguideapp.R

/**
 * Used for testing fragments inside a fake activity.
 */
class SingleFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = FragmentContainerView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
            id = R.id.container
        }
        setContentView(content)
    }

    @Suppress("unused")
    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment, "TEST")
                .commit()
    }

    @Suppress("unused")
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
    }
}
