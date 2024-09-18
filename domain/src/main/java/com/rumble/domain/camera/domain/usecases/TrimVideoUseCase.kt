package com.rumble.domain.camera.domain.usecases

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import javax.inject.Inject

class TrimVideoUseCase @Inject constructor(
    private val getMediaFileUriUseCase: GetMediaFileUriUseCase,
) {
    private val defaultBufferSize = 1024 * 1024
    private val videoType = "video/"
    private val audioType = "audio/"

    operator fun invoke(
        inputVideoUrl: String,
        outputFilePath: String,
        startTime: Float,
        endTime: Float
    ) {
        val startTimeMs = startTime.toLong() * 1000
        val endTimeMs = endTime.toLong() * 1000

        val inputPath = getMediaFileUriUseCase(inputVideoUrl)
        val fileInputStream = FileInputStream(File(inputPath))

        val extractor = MediaExtractor().apply { setDataSource(fileInputStream.fd) }
        val muxer = MediaMuxer(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        val retrieverSrc = MediaMetadataRetriever().apply { setDataSource(fileInputStream.fd) }
        retrieverSrc.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.let { degreesString ->
            degreesString.toIntOrNull()?.let { degrees ->
                if (degrees > 0) muxer.setOrientationHint(degrees)
            }
        }

        val indexMap = mutableMapOf<Int, Int>()
        var bufferSize = -1
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith(videoType) == true || mime?.startsWith(audioType) == true) {
                extractor.selectTrack(i)
                indexMap[i] = muxer.addTrack(format)
                if (format.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
                    val newSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
                    bufferSize = if (newSize > bufferSize) newSize else bufferSize
                }
            }
        }
        if (bufferSize < 0) {
            bufferSize = defaultBufferSize
        }

        extractor.seekTo(startTimeMs, MediaExtractor.SEEK_TO_CLOSEST_SYNC)

        val offset = 0
        val dstBuf = ByteBuffer.allocate(bufferSize)
        val bufferInfo = MediaCodec.BufferInfo()
        muxer.start()
        while (true) {
            bufferInfo.offset = offset
            bufferInfo.size = extractor.readSampleData(dstBuf, offset)
            if (bufferInfo.size < 0) {
                bufferInfo.size = 0
                break
            } else {
                bufferInfo.presentationTimeUs = extractor.sampleTime
                if (bufferInfo.presentationTimeUs > (endTimeMs)) {
                    break
                } else {
                    val isKeyFrame = extractor.sampleFlags and MediaExtractor.SAMPLE_FLAG_SYNC != 0
                    val flags = if (isKeyFrame) MediaCodec.BUFFER_FLAG_KEY_FRAME else 0
                    bufferInfo.flags = flags
                    indexMap[extractor.sampleTrackIndex]?.let {
                        muxer.writeSampleData(it, dstBuf, bufferInfo)
                    }
                    extractor.advance()
                }
            }
        }

        muxer.stop()
        muxer.release()
        extractor.release()
    }
}