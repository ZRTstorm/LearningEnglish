package com.example.learningenglish.ui.learning.pronunciation

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import java.io.File
import com.arthenica.ffmpegkit.FFmpegKit


class AudioRecorder {

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(context: Context): File {
        val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        outputFile = File(outputDir, "recorded_audio.mp4")

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile!!.absolutePath)
            prepare()
            start()
        }

        return outputFile!!
    }

    fun stopRecording(): File? {
        return try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            outputFile
        } catch (e: Exception) {
            Log.e("AudioRecorder", "녹음 중지 실패: ${e.message}")
            null
        }
    }
}

fun convertMp4ToWav(inputFile: File, outputFile: File, onFinish: (Boolean) -> Unit) {
    val command = "-i ${inputFile.absolutePath} -ar 44100 -ac 1 -c:a pcm_s16le ${outputFile.absolutePath}"
    FFmpegKit.executeAsync(command) { session ->
        val returnCode = session.returnCode
        onFinish(returnCode.isValueSuccess)
    }
}
