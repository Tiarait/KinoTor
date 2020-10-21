package com.kinotor.tiar.kinotor.ui


import android.content.ContentValues
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import com.kinotor.tiar.kinotor.R
import com.kinotor.tiar.kinotor.ui.fragments.ImageFragment

/**
 * Created by Tiar on 10.2018.
 */
class ImgActivity : AppCompatActivity() {
  private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
  private var images = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//        if (PreferenceManager.getDefaultSharedPreferences(this)
//                        .getBoolean("fullscreen", false)) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        }
    setContentView(R.layout.activity_img)
    val toolbar = findViewById<Toolbar>(R.id.toolbar_i)
    setSupportActionBar(toolbar)
    if (supportActionBar != null) {
      supportActionBar!!.setDisplayHomeAsUpEnabled(true)
      supportActionBar!!.setDisplayShowHomeEnabled(true)
    }
    val position = intent.extras?.getInt("Position")!!
    images = intent.extras?.getString("Img")!!
    title = "${position + 1} из ${images.split(",").size}"
    mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
    val container = findViewById<ViewPager>(R.id.container_i)
    container.adapter = mSectionsPagerAdapter
    container.currentItem = position

    container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
      override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
      }

      override fun onPageSelected(position: Int) {
        Log.d(ContentValues.TAG, "onPageSelected()")
      }

      override fun onPageScrollStateChanged(state: Int) {
        title = "${container.currentItem + 1} из ${images.split(",").size}"
        Log.d(ContentValues.TAG, "onPageScrollStateChanged()")
      }
    })
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> finish()
    }
    return super.onOptionsItemSelected(item)
  }

  inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
      Log.d(ContentValues.TAG, "Adapter position: $position of $count")
      return ImageFragment.newInstance(position, images)
    }

    override fun getCount(): Int {
      return images.split(",").size
    }
  }
}