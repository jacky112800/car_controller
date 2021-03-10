package com.example.car

import android.media.MediaCodec
import android.media.MediaFormat
import android.view.Surface

class H264_Decder(path: String, var width: Int, var height: Int, surface: Surface) : Thread() {
    private val TAG = "H264"
    var bytes_H264: ByteArray? = null
    lateinit var medidCC: MediaCodec
    var saveImage: Long = 0

    init {
        var th = client_th_H264()
        th.start()
        bytes_H264 = client_th_H264.get_data_H264
        medidCC= MediaCodec.createByCodecName("video/avc")
        val mediafm=MediaFormat.createVideoFormat("video/avc",width,height)
        mediafm.setInteger(MediaFormat.KEY_FRAME_RATE,15)
        medidCC.configure(mediafm,null,null,0)
    }

}