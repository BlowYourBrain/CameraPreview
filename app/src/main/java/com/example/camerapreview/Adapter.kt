package com.example.camerapreview

import android.hardware.Camera
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min

private const val CAMERA_PREVIEW = 0
private const val COMMON_ITEM = 1

class Adapter(private val count: Int) : RecyclerView.Adapter<CommonViewHolder>() {

    private var camera: Camera? = null
    private var cameraViewHolder: CommonViewHolder? = null

    /** A safe way to get an instance of the Camera object. */
    private fun getCameraInstance(): Camera? {
        return camera ?: try {
            Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            Log.d(TAG, e.message ?: "")
            // Camera is not available (in use or does not exist)
            null // returns null if camera is unavailable
        }.also { camera = it }
    }

    private fun createCameraViewHolder(parent: ViewGroup): CommonViewHolder {
        return cameraViewHolder ?: kotlin.run {
            val camera = getCameraInstance()?.apply {
                val list = parameters.supportedPreviewSizes
                var minSize: Camera.Size = list.first()
                list.forEach {
                    if (minSize.width > it.width) {
                        minSize = it
                    }
                    Log.d(TAG, "w = ${it.width}, h = ${it.height}")
                }

                parameters.setPreviewSize(minSize.width, minSize.height)
                setDisplayOrientation(90)
            } ?: return createCommonViewHolder(parent)
            val cameraPreview = CameraPreview(parent.context, camera)
            val container =
                LayoutInflater.from(parent.context).inflate(R.layout.common_entity, parent, false)
            (container as FrameLayout).addView(cameraPreview)
            CommonViewHolder(container)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            CAMERA_PREVIEW
        } else {
            COMMON_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return if (viewType == CAMERA_PREVIEW) {
            createCameraViewHolder(parent)
        } else {
            createCommonViewHolder(parent)
        }
    }

    private fun createCommonViewHolder(parent: ViewGroup): CommonViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.common_entity, parent, false)
        return CommonViewHolder(view)
    }

    override fun getItemCount(): Int {
        return count
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        if (holder.itemView is CameraPreview) {
            try {
                camera?.startPreview()
            } catch (e: Throwable) {
                Log.d(TAG, e.localizedMessage ?: "")
            }
        }
    }
}

class CommonViewHolder(view: View) : RecyclerView.ViewHolder(view) {

}