package ge.aksovreli.messengerapp.viewmodels.chat

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AudioManager(private val context: Context) {
    var isRecording = false
    var audioRecorded = false
    var recordedFile: File = File(context.cacheDir, "recorded_message.3gp")

    private var recorder: MediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { MediaRecorder(context)
    } else { MediaRecorder() }

    init {
        // Build media recorder
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(FileOutputStream(recordedFile).fd)
            prepare()
        }
    }


    private fun startRecordingAudio(): String? {
        var errMsg: String? = null

        try {
            if (!isRecording) {
                recorder.start()
                isRecording = true
            }
        } catch (e: IOException) {
            errMsg = e.message
            isRecording = false
        }

        audioRecorded = false
        return errMsg
    }


    private fun stopRecordingAudio(): String? {
        var errMsg: String? = null

        try {
            if (isRecording) {
                recorder.stop()
                recorder.release()
                audioRecorded = true
            }
        } catch (e: IOException) {
            errMsg = e.message
        }

        isRecording = false
        return errMsg
    }

    fun toggleAudioRecording(): String? {
        return if (isRecording) {
            Toast.makeText(context, "Stopped recording", Toast.LENGTH_LONG).show()
            stopRecordingAudio()
        } else {
            Toast.makeText(context, "Started recording", Toast.LENGTH_LONG).show()
            startRecordingAudio()
        }
    }

    fun playAudio(fileName: String) {
        val mp = MediaPlayer()
        val uri = Firebase.storage.reference.child(fileName)
        uri.downloadUrl.addOnSuccessListener{ link ->
            mp.setDataSource(link.toString())
            mp.prepare()
            mp.setOnPreparedListener {
                mp.start()
            }
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
        }
    }

    fun playAudio(uri: Uri) {
        val mp = MediaPlayer()
        mp.setDataSource(context, uri)
        mp.prepare()
        mp.setOnPreparedListener {
            mp.start()
        }
    }
}