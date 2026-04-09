package com.example.nailong

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.nailong.ui.theme.NailongTheme

class MainActivity : ComponentActivity() {
    
    private var mediaPlayer: MediaPlayer? = null
    private var surfaceView: SurfaceView? = null
    private var hasAppliedInitialLayout = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            NailongTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFBFDFC))
                ) {
                    var isPlaying by remember { mutableStateOf(false) }

                    AndroidView(
                        factory = { ctx ->
                            val rootLayout = FrameLayout(ctx).apply {
                                setBackgroundColor(android.graphics.Color.parseColor("#FBFDFC"))
                            }
                            val surface = SurfaceView(ctx).apply {
                                setZOrderOnTop(false)
                                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                holder.addCallback(object : SurfaceHolder.Callback {
                                    override fun surfaceCreated(holder: SurfaceHolder) {
                                        Log.d("Nailong", "Surface created")
                                        surfaceView = this@apply
                                        initMediaPlayer(holder)
                                    }

                                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                                        Log.d("Nailong", "Surface changed: ${width}x${height}")
                                        rootLayout.post {
                                            updateVideoLayout(width, height)
                                        }
                                    }

                                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                                        Log.d("Nailong", "Surface destroyed")
                                    }
                                })
                            }
                            surfaceView = surface
                            rootLayout.addView(surface, FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            ).apply {
                                gravity = android.view.Gravity.FILL
                            })
                            rootLayout
                        },
                        modifier = Modifier.fillMaxSize(),
                        update = { rootView ->
                            val mp = mediaPlayer
                            if (mp != null && mp.videoWidth > 0 && mp.videoHeight > 0) {
                                updateVideoLayout(rootView.width, rootView.height)
                            }
                        }
                    )

                    Button(
                        onClick = {
                            mediaPlayer?.let { mp ->
                                if (isPlaying) {
                                    mp.pause()
                                    mp.seekTo(0)
                                    isPlaying = false
                                } else {
                                    mp.start()
                                    isPlaying = true
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(bottom = 32.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A1A1A).copy(alpha = 0.85f)
                        )
                    ) {
                        Text(
                            text = if (isPlaying) "停止" else "循环播放",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }

    private fun initMediaPlayer(holder: SurfaceHolder) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDisplay(holder)
                isLooping = true
                setVolume(1f, 1f)
                
                val rawFd = resources.openRawResourceFd(R.raw.nl)
                setDataSource(rawFd.fileDescriptor, rawFd.startOffset, rawFd.length)
                
                setOnPreparedListener { mp ->
                    Log.d("Nailong", "MediaPlayer prepared, video: ${mp.videoWidth}x${mp.videoHeight}")
                    updateVideoLayout(surfaceView?.width ?: 0, surfaceView?.height ?: 0)
                    mp.seekTo(0, MediaPlayer.SEEK_CLOSEST)
                }
                
                setOnVideoSizeChangedListener { mp, width, height ->
                    Log.d("Nailong", "Video size changed: ${width}x${height}")
                    updateVideoLayout(surfaceView?.width ?: 0, surfaceView?.height ?: 0)
                }
                
                setOnErrorListener { _, what, extra ->
                    Log.e("Nailong", "MediaPlayer error: what=$what, extra=$extra")
                    false
                }
                
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("Nailong", "Failed to init MediaPlayer", e)
        }
    }

    private fun updateVideoLayout(viewWidth: Int, viewHeight: Int) {
        val mp = mediaPlayer ?: return
        val sv = surfaceView ?: return
        
        val videoWidth = mp.videoWidth
        val videoHeight = mp.videoHeight
        
        if (videoWidth == 0 || videoHeight == 0 || viewWidth == 0 || viewHeight == 0) return
        
        val layoutParams = sv.layoutParams as FrameLayout.LayoutParams
        
        if (hasAppliedInitialLayout && 
            layoutParams.width == viewWidth && 
            layoutParams.height == viewHeight) {
            return
        }
        
        Log.d("Nailong", "Layout: view=${viewWidth}x${viewHeight}, video=${videoWidth}x${videoHeight}")
        
        val viewRatio = viewWidth.toFloat() / viewHeight.toFloat()
        val videoRatio = videoWidth.toFloat() / videoHeight.toFloat()
        
        if (viewRatio > videoRatio) {
            val scale = viewHeight.toFloat() / videoHeight.toFloat()
            val scaledWidth = (videoWidth * scale).toInt()
            layoutParams.width = scaledWidth
            layoutParams.height = viewHeight
        } else {
            val scale = viewWidth.toFloat() / videoWidth.toFloat()
            val scaledHeight = (videoHeight * scale).toInt()
            layoutParams.width = viewWidth
            layoutParams.height = scaledHeight
        }
        
        layoutParams.gravity = android.view.Gravity.CENTER
        sv.layoutParams = layoutParams
        sv.post { sv.requestLayout() }
        hasAppliedInitialLayout = true
        
        Log.d("Nailong", "New layout: ${layoutParams.width}x${layoutParams.height}")
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        surfaceView = null
    }
}
