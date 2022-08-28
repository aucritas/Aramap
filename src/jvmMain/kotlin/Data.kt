import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.MutableState

object Data{
    lateinit var pngFile: MutableState<String>
    lateinit var saveFolder: MutableState<String>
    lateinit var showFileDialog : MutableState<Boolean>
    lateinit var showPixels : MutableState<Boolean>
    lateinit var isPNGLoaded : MutableState<Boolean>
    lateinit var isSongLoaded : MutableState<Boolean>
    lateinit var songFile : MutableState<String>
    var heightLayers : Int = 0
    var widthLayers : Int = 0
    const val heightBlockSize = 9
    const val widthBlockSize = 16
    var pixelVerticalRows : ArrayList<ArrayList<Pixel>> = arrayListOf()
    lateinit var listState : LazyGridState
    lateinit var isPlayingSong: MutableState<Boolean>
    val player = AudioFilePlayer()
    var fileDialogType: String = "png"
}