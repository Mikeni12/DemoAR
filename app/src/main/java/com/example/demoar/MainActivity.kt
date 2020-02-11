package com.example.demoar

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var videoRenderable: ModelRenderable
    private val HEIGHT = 1.25f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val texture = ExternalTexture()
        val mediaPlayer = MediaPlayer.create(this, R.raw.presi).apply {
            setSurface(texture.surface)
            isLooping = true
        }

        ModelRenderable
            .builder()
            .setSource(this, R.raw.video_screen)
            .build()
            .thenAccept { modelRenderable ->
                videoRenderable = modelRenderable
                videoRenderable.material.setExternalTexture("videoTexture", texture)
                videoRenderable.material.setFloat4("keyColor", Color(0.01843f, 1.0f, 0.098f))
            }

        val arFragment = fragment as ArFragment

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            val anchorNode = AnchorNode(hitResult.createAnchor())
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()

                texture.surfaceTexture.setOnFrameAvailableListener {
                    anchorNode.renderable = videoRenderable
                    texture.surfaceTexture.setOnFrameAvailableListener(null)
                }
            } else {
                anchorNode.renderable = videoRenderable
            }

            val width = mediaPlayer.videoWidth
            val height = mediaPlayer.videoHeight

            anchorNode.localScale = Vector3(HEIGHT * (width / height), HEIGHT, 1.0f)
            arFragment.arSceneView.scene.addChild(anchorNode)
        }
    }
}
