package com.izyver.gati.presentation.schedule

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.izyver.gati.R
import com.izyver.gati.bussines.models.ScheduleType
import com.izyver.gati.bussines.models.ScheduleType.DAYTIME
import com.izyver.gati.bussines.models.ScheduleType.DISTANCE
import com.izyver.gati.presentation.BaseFragment
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.StringQualifier

abstract class ScheduleFragment : BaseFragment(), OnScheduleLongClickListener{

    protected abstract val viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadImages()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeScheduleAdapter()
    }

    private fun initializeScheduleAdapter() {
        val cardAdapter = ScheduleCardAdapter()
        cardAdapter.onScheduleLongClick = this
        scheduleRecyclerView.adapter = cardAdapter
        scheduleRecyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.scheduleImage.observe(this, Observer { cardAdapter.setValues(it) })
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE -> {
                if (grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED){
                    shareSchedule(1)
                }
            }
        }
    }

    override fun onLongClicked(index: Int): Boolean {
        val allowRead: Boolean = ContextCompat.checkSelfPermission(context
                ?: return false, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
        val allowWrite: Boolean = ContextCompat.checkSelfPermission(context
                ?: return false, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED

        if (allowWrite && allowRead) {
            shareSchedule(index)
        } else {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE)
        }
        return false
    }

    @RequiresPermission(allOf = [READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE])
    fun shareSchedule(indexOfList: Int) {
        GlobalScope.launch {
            val bitmap: Bitmap? = viewModel.getImageForShare(indexOfList)
            shareBitmap(bitmap, context ?: return@launch)
        }
    }

    private fun shareBitmap(bitmap: Bitmap?, context: Context) {
        if (bitmap == null) return errorShareBitmap()
        val bitmapPath = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Розклад", "розклад за якийсь день")
        val bitmapUri = Uri.parse(bitmapPath)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_title)))
    }

    private fun errorShareBitmap() {
        Toast.makeText(context ?: return, R.string.error_share_image, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun getTag(scheduleType: ScheduleType): String {
            return when (scheduleType) {
                DAYTIME -> Daytime.TAG
                DISTANCE -> Distance.TAG
            }
        }

        fun newInstance(scheduleType: ScheduleType): ScheduleFragment {
            return when (scheduleType) {
                DISTANCE -> Distance()
                DAYTIME -> Daytime()
            }
        }
        const val REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE = 1
    }

    class Daytime : ScheduleFragment() {
        override val viewModel: ScheduleViewModel by viewModel(StringQualifier(DAYTIME.name))

        companion object {
            const val TAG: String = "ScheduleFragmentDaytime"
        }
    }

    class Distance : ScheduleFragment() {
        override val viewModel: ScheduleViewModel by viewModel(StringQualifier(DISTANCE.name))

        companion object {
            const val TAG: String = "ScheduleFragmentDistance"
        }
    }
}