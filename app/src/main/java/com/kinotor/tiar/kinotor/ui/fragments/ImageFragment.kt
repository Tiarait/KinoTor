package com.kinotor.tiar.kinotor.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.chrisbanes.photoview.PhotoView
import com.kinotor.tiar.kinotor.R
import com.squareup.picasso.Picasso

/**
 * Created by Tiar on 10.2018.
 */
class ImageFragment : Fragment() {
    private val TAG = "ImageFragment"
    private var images = ""
    private var positionPost: Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_img, container, false)

        images = arguments!!.getString(ARG_SECTION_IMG)!!.replace("[","")
                .replace("]","")
        positionPost = arguments!!.getInt(ARG_SECTION_NUMBER)

        val imgUrl = images.split(",")[positionPost]

        Log.d(TAG, "Num: $positionPost of: ${images.split(",").size} img: $imgUrl")
        Log.d(TAG, "Num: $images" )

        onLoadImage(imgUrl.trim(), rootView)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
    }

    private fun onLoadImage(img_url: String, view: View) {
        val img = view.findViewById<PhotoView>(R.id.img)
        Log.d(TAG, "Cur: $img_url" )
        Picasso.get().load(img_url).into(img)
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_position"
        private const val ARG_SECTION_IMG = "section_id"
        fun newInstance(position: Int, img: String): ImageFragment {
            val fragment = ImageFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, position)
            args.putString(ARG_SECTION_IMG, img)
            fragment.arguments = args
            return fragment
        }
    }
}