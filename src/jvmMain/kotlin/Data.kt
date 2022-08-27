import androidx.compose.runtime.MutableState

object Data{
    lateinit var folder: MutableState<String>
    lateinit var saveFolder: MutableState<String>
    var listaFiles: MutableList<String> = arrayListOf()
    lateinit var showFileDialog : MutableState<Boolean>
    lateinit var showImage : MutableState<Boolean>
    lateinit var idx: MutableState<Int>
    lateinit var isSongLoaded : MutableState<Boolean>
}