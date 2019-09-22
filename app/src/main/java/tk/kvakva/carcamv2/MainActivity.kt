package tk.kvakva.carcamv2

import android.app.Application
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.otaliastudios.cameraview.CameraException
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.VideoResult
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val LOG_TAG = "MAinACtivity"

class MainActivity : AppCompatActivity() {

    private lateinit var camViewModel: CamViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        camViewModel = ViewModelProvider(this).get(CamViewModel::class.java)

        camV.setLifecycleOwner(this)
        camV.videoMaxDuration = 60000

        camV.addCameraListener(object : CameraListener() {
            override fun onCameraError(exception: CameraException) {
                super.onCameraError(exception)
                Toast.makeText(applicationContext, "ERROE: onCammeraError", Toast.LENGTH_LONG)
                    .show()
                Log.e(LOG_TAG, "ERROR: onCameraError")
            }

            override fun onVideoTaken(result: VideoResult) {
                super.onVideoTaken(result)
                val fileN = result.file
                Log.v(
                    LOG_TAG,
                    "************************ onVideoTaken ************** filename: ${fileN.name}  ----------"
                )
                txtView.text = fileN.name
                if (camViewModel.isRecordingVideo.value == true) {
                    camViewModel.setTimeOutRec()
                }
            }

            override fun onVideoRecordingEnd() {
                super.onVideoRecordingEnd()
                Log.v(LOG_TAG, "******************* onVideoRecordingEnd *************")
            }

            override fun onCameraOpened(options: CameraOptions) {
                super.onCameraOpened(options)
                Log.v(LOG_TAG, "^^^^ onCameraOOpend ^^^^")
                if (camViewModel.isRecordingVideo.value == true)
                    startRecord()
            }

            override fun onCameraClosed() {
                super.onCameraClosed()
                Log.v(LOG_TAG, "&&&& onCameraClosed &&&&&")
            }


        })

        val mObserver = Observer<Boolean> {
            if (camViewModel.timeOutRec.value == true) {
                camViewModel.unsetTimeOutRec()
                startRecord()
            }
        }
        camViewModel.timeOutRec.observe(this, mObserver)

    }


    fun onClickRec(view: View) {
        if (view.id == R.id.videoRecordBtn) {
            if (camViewModel.isRecordingVideo.value == true) {
                stopRecord()
                Log.v(
                    LOG_TAG,
                    "********* stopingRecord onClickRec(v) camViewModel.isRecordingVideo.value == true *****"
                )
            } else {
                startRecord()
                Log.v(
                    LOG_TAG,
                    "******** startingRecord onClickRec(v) camViewModel.isRecordingVideo.value == false *****"
                )
            }
        }
    }

    private fun startRecord() {

        val sTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss.SSS"))
        } else {
            SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS", Locale.US).format(Date())
        }

/*
        val resolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "CuteKitten001")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/PerracoLabs")
        }

        val uri  = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        resolver.openOutputStream(uri!!).use {

        }
 private fun getVideoFilePath(context: Context?): String {
        val filename = "${System.currentTimeMillis()}.mp4"
        val dir = context?.getExternalFilesDirs("CarCamVideos")?.last()
        val listFiles = dir?.listFiles { file, s ->
            s.endsWith("mp4",true)
        }
        if (listFiles != null) {
            if(listFiles.size>=20){
                for(i in 0..listFiles.size-20){
                    listFiles.sorted()[i].delete()
                }
            }
        }

        listFiles?.forEach {
            Log.i("asdfasdfasdf","!!!! $it")
        }
        val freeBytes = File(dir?.absolutePath).freeSpace
        Log.i("asdfasdf","!!!!!!!!!!!!!!!!!!!!!!!!!!! ${dir?.absolutePath} ${freeBytes} ////////")
        val extDirs = context?.getExternalFilesDirs("qqww")
        extDirs?.forEach {
            Log.i("ASDASDASD","!!!!!!!!!!!!!!!!!!!!!!!!!!!! ${it.absolutePath} ${File(it.absolutePath).freeSpace} !!!! ")
        }
        val diri = context?.filesDir
        Log.i("ASDASD","!!!!!!!!!!!!!!!! ${diri?.absolutePath} ${File(diri?.absolutePath).freeSpace}")
        return if (dir == null) {
            filename
        } else {
            "${dir.absolutePath}/$filename"
        }
    }

*/

/*
        val fff = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),"yui.jpg")

        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val listFiles = dir?.listFiles { file, s ->
            s.endsWith("mp4",true)
        }
*/


/*
        val resolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "CuteKitten001")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/PerracoLabs")
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)



        resolver.openOutputStream(uri!!).use {
            // TODO something with the stream
        }

*/


        val f = File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "$sTime.mp4")
        if (camV.isTakingVideo)
            Log.e(LOG_TAG, "!!!!!! startRecord() !!! camV.isTakeingVideo!!!!!!")
        else
            camV.takeVideo(f, camViewModel.duration.value ?: 300000)
        camViewModel.startVideo()
        videoRecordBtn.setImageResource(android.R.drawable.star_on)
        Log.v(LOG_TAG, "********* startRecord() f: ${f.name} *****************")
        txtView.append("\n${f.name}")
    }

    private fun stopRecord() {
        camViewModel.stopVideo()
        videoRecordBtn.setImageResource(android.R.drawable.star_off)

        if (camV.isTakingVideo)
            camV.stopVideo()
        else
            Log.e(LOG_TAG, "!!!!!!! stopRecord() but camV.isRakingVideo false !!!!!!!!!!!")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menuopt, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun onClickMenu(item: MenuItem) {
        when (item.itemId) {
            R.id.optionsid -> startActivity(Intent(this, SettingsActivity::class.java))
            else -> super.onOptionsItemSelected(item)
        }
    }


}


class CamViewModel(aplica: Application) : AndroidViewModel(aplica) {


    private val _isRecordingVideo = MutableLiveData<Boolean>()
    val isRecordingVideo: LiveData<Boolean>
        get() = _isRecordingVideo

    private val _timeOutRec = MutableLiveData<Boolean>()
    val timeOutRec: LiveData<Boolean>
        get() = _timeOutRec

    private val _duration = MutableLiveData<Int>()
    val duration: LiveData<Int>
        get() = _duration

    init {
        val sp = PreferenceManager.getDefaultSharedPreferences(getApplication())
        Log.i("Preper", "All Prefferences ${sp.all}")
        _duration.value = (sp.getString("timeOutKey", "5")?.toInt() ?: 5) * 60000
        Log.v("init", "                              duration = ${duration.value}")
        Log.v(
            "InitCamViewModel",
            "!!!!!!!!!!!!!!!!!!!!!!!!!!! view model init !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
        )
        _isRecordingVideo.value = false
        _timeOutRec.value = false
    }

    fun setTimeOutRec() {
        _timeOutRec.value = true
    }

    fun unsetTimeOutRec() {
        _timeOutRec.value = false
    }

    fun stopVideo() {
        _isRecordingVideo.value = false
    }

    fun startVideo() {
        _isRecordingVideo.value = true
    }

    override fun onCleared() {
        super.onCleared()
        Log.v(
            "onClearedCamViewModel",
            "!!!!!!!!!!!!!!!!!!!!!!! view model onCleared !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
        )
    }
}