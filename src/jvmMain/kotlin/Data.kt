import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.MutableState

object Data{
    lateinit var folder: MutableState<String>
    lateinit var saveFolder: MutableState<String>
    var listaFiles: MutableList<String> = arrayListOf()
    lateinit var showFileDialog : MutableState<Boolean>
    lateinit var showPixels : MutableState<Boolean>
    lateinit var showImage : MutableState<Boolean>
    lateinit var idx: MutableState<Int>
    lateinit var isSongLoaded : MutableState<Boolean>
    var heightLayers : Int = 0
    var widthLayers : Int = 0
    const val heightBlockSize = 9
    const val widthBlockSize = 16
    lateinit var pixelVerticalRows : ArrayList<ArrayList<Pixel>>
    lateinit var listState : LazyGridState
    lateinit var isPlayingSong: MutableState<Boolean>
}