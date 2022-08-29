import java.io.File
import java.io.IOException
import javax.sound.sampled.*

class AudioFilePlayer {
    private lateinit var line: SourceDataLine
    var isPaused = false
    private var isKilled = false
    fun play(filePath: String?) {
        isKilled = false
        isPaused = false
        val file = filePath?.let { File(it) }
        try {
            AudioSystem.getAudioInputStream(file).use { `in` ->
                val outFormat = getOutFormat(`in`.format)
                val info = DataLine.Info(SourceDataLine::class.java, outFormat)
                line = AudioSystem.getLine(info) as SourceDataLine
                line.open(outFormat)
                line.start()
                stream(AudioSystem.getAudioInputStream(outFormat, `in`), line)
                line.drain()
                line.stop()
            }
        } catch (e: UnsupportedAudioFileException) {
            throw IllegalStateException(e)
        } catch (e: LineUnavailableException) {
            throw IllegalStateException(e)
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    private fun getOutFormat(inFormat: AudioFormat): AudioFormat {
        val ch = inFormat.channels
        val rate = inFormat.sampleRate
        return AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false)
    }

    @Throws(IOException::class)
    private fun stream(`in`: AudioInputStream, line: SourceDataLine) {
        val buffer = ByteArray(4)
        var n = 0
        while (n != -1 && !isKilled) {
            if (isPaused) {
                continue
            }
            line.write(buffer, 0, n)
            n = `in`.read(buffer, 0, buffer.size)
        }
    }


    fun pause() {
        isPaused = true
        line.stop()
    }

    fun resume() {
        isPaused = false
        line.start()
    }

    fun invertpause() {
        if (isPaused) {
            resume()
        } else {
            pause()
        }
    }

    fun kill() {
        isKilled = true
    }
}