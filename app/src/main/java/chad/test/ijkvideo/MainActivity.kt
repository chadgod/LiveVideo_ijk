package chad.test.ijkvideo

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
    private lateinit var frameLayout: FrameLayout
    private lateinit var actContext: MainActivity
    private var liveVideoHelper: LiveVideoSurfaceView? = null
    private var liveVideoSource = ""
    private val streaming = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        actContext = this
        frameLayout = findViewById(R.id.frame_layout)
        if (liveVideoHelper == null) {
            liveVideoHelper = LiveVideoSurfaceView(this, frameLayout)
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val playBtn = Button(this)
        playBtn.text = "play"
        frameLayout.addView(playBtn,params)
        playBtn.setOnClickListener {
            if( streaming == "" ){
                Toast.makeText(it.context, "fix rtmp address", Toast.LENGTH_LONG).show()
            }
            else {
                playLiveVideo(streaming)
                Toast.makeText(it.context, "play", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "playLiveVideo")
            }
        }
    }
    fun getContext(): Context {
        return actContext
    }
    fun playLiveVideo(source: String) {
        liveVideoSource = source
        liveVideoHelper!!.play(liveVideoSource)
    }
    fun stopLiveVideo() {
        liveVideoSource = ""
        if (liveVideoHelper != null) {
            liveVideoHelper!!.release(true)
        }
    }
}
