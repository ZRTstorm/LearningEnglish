package com.example.learningenglish.ui.learning.pronunciation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import com.arthenica.ffmpegkit.FFmpegKit
import kotlin.math.abs


class AudioRecorder {


    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(context: Context): File {
        val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        outputFile = File(outputDir, "recorded_audio_${System.currentTimeMillis()}.mp4")

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

    fun currentAmplitude(): Float {
        return recorder?.maxAmplitude?.toFloat() ?: 0f
    }
}

fun convertMp4ToWav(inputFile: File, outputFile: File, onFinish: (Boolean) -> Unit) {
    val command = "-y -i ${inputFile.absolutePath} -ar 44100 -ac 1 -c:a pcm_s16le ${outputFile.absolutePath}"
    FFmpegKit.executeAsync(command) { session ->
        val returnCode = session.returnCode
        onFinish(returnCode.isValueSuccess)
    }
}

@Composable
fun VoiceWaveform(amplitudes: List<Float>) {
    Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
        val centerY = size.height / 2
        val widthPerBar = size.width / amplitudes.size.coerceAtLeast(1)
        amplitudes.forEachIndexed { index, amp ->
            val normAmp = (amp / Short.MAX_VALUE)
            val barHeight = normAmp * size.height
            drawLine(
                color = Color.Red,
                start = Offset(x = index * widthPerBar, y = centerY - barHeight / 2),
                end = Offset(x = index * widthPerBar, y = centerY + barHeight / 2),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
    }
}

/*
fun convertPcmToWav(inputFile: File, outputFile: File, onFinish: (Boolean) -> Unit) {
    val command = "-f s16le -ar 44100 -ac 1 -i ${inputFile.absolutePath} ${outputFile.absolutePath}"
    FFmpegKit.executeAsync(command) { session ->
        val returnCode = session.returnCode
        onFinish(returnCode.isValueSuccess)
    }
}

 */


/*
class AudioRecorder {
/*
    private var recorder: AudioRecord? = null
    private var buffer: ShortArray = ShortArray(1024)
    private var recordingFile: File? = null

    fun start(context: android.content.Context): File? {
        val sampleRate = 44100
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1234
            )
            return null
        }

        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        recordingFile = File(context.cacheDir, "recorded_audio_${System.currentTimeMillis()}.pcm")
        recorder?.startRecording()
        return recordingFile
    }

    fun stop(): File? {
        recorder?.stop()
        recorder?.release()
        recorder = null
        return recordingFile
    }

    fun currentAmplitude(): Float {
        recorder?.read(buffer, 0, buffer.size)
        return buffer.maxOfOrNull { abs(it.toFloat()) } ?: 0f
    }
}
*/



/*
fun convertMp4ToWav(inputFile: File, outputFile: File, onFinish: (Boolean) -> Unit) {
    val command = "-i ${inputFile.absolutePath} -ar 44100 -ac 1 -c:a pcm_s16le ${outputFile.absolutePath}"
    FFmpegKit.executeAsync(command) { session ->
        val returnCode = session.returnCode
        onFinish(returnCode.isValueSuccess)
    }
}
 */