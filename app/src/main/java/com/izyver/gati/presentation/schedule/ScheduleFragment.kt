package com.izyver.gati.presentation.schedule

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.util.Log
import android.view.*
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

abstract class ScheduleFragment : BaseFragment(), OnScheduleClickListener {

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
        initializeRefreshLayout()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE -> {
                if (grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                    shareSchedule(arguments?.getInt(KEY_SHARE_INDEX) ?: return errorShareBitmap())
                }
            }
        }
    }

    override fun onLongClick(view: View, index: Int, x: Float, y: Float): Boolean {
        if (arguments == null) arguments = Bundle()
        arguments?.putInt(KEY_SHARE_INDEX, index)

        registerForContextMenu(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.showContextMenu(x, y)
        } else {
            view.showContextMenu()
        }
        vibrate(40L)
        unregisterForContextMenu(view)
        return true
    }

    override fun onShortClick(view: View, index: Int) {
//        viewModel.onDayClicked(index)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu.add(Menu.NONE, SHARE_IMAGE_CONTEXT_ITEM_ID, Menu.NONE, R.string.share)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            SHARE_IMAGE_CONTEXT_ITEM_ID -> {
                if (arguments == null) {
                    errorShareBitmap()
                    return false
                }
                checkAndShareImage(arguments?.getInt(KEY_SHARE_INDEX) ?: return false)
                return true
            }
        }
        return true
    }

    private fun initializeScheduleAdapter() {
        val cardAdapter = ScheduleCardAdapter()
        cardAdapter.onScheduleClick = this
        scheduleRecyclerView.adapter = cardAdapter
        scheduleRecyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.scheduleImage.observe(this, Observer { cardAdapter.setValues(it) })
    }


    private fun initializeRefreshLayout() {
        viewModel.isLoading.observe(this, Observer { scheduleRefreshLayout.isRefreshing = it })
        scheduleRefreshLayout.setOnRefreshListener { viewModel.reloadImages() }
    }


    private fun vibrate(milliseconds: Long) {
        val vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator? ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(milliseconds)
        }
    }


    private fun checkAndShareImage(index: Int): Boolean {
        val allowRead: Boolean = ContextCompat.checkSelfPermission(context
                ?: return false, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
        val allowWrite: Boolean = ContextCompat.checkSelfPermission(context
                ?: return false, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED

        if (allowWrite && allowRead) {
            shareSchedule(index)
        } else {
            if (arguments == null) arguments = Bundle()
            arguments?.putInt(KEY_SHARE_INDEX, index)
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE)
        }
        return false
    }

    @RequiresPermission(allOf = [READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE])
    private fun shareSchedule(indexOfList: Int) {
        GlobalScope.launch {
            val bitmap: Bitmap? = viewModel.getImageForShare(indexOfList)
            shareBitmap(bitmap, context ?: return@launch)
        }
    }

    private fun shareBitmap(bitmap: Bitmap?, context: Context) {
        if (bitmap == null) return errorShareBitmap()
        val bitmapPath = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, getString(R.string.schedule), getString(R.string.gati_schedule))
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
        const val SHARE_IMAGE_CONTEXT_ITEM_ID = 2
        const val KEY_SHARE_INDEX = "share_index"
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