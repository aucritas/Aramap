import Data.idx
import Data.showFileDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            App()
        }
    }
}

@Composable
fun fileDialog(
    parent: Frame? = null,
    onCloseRequest: (result: String?) -> Unit
) = AwtWindow(

    create = {
        object : FileDialog(parent, "Choose a file", LOAD) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest(file)
                    println(files[0].absolutePath) //wtf is this even...
                    Data.folder.value = files[0].absolutePath
                    File(Data.folder.value).walk().maxDepth(1).forEach {

                        if (it.isFile) {
                            Data.listaFiles.add(it.toString())
                            println(it.toString())
                        }
                    }
                    if (Data.listaFiles.size > 0) {
                        Data.showImage.value = true
                    }
                    showFileDialog.value = false
                    Data.isSongLoaded.value = true
                }
            }
        }
    },
    dispose = FileDialog::dispose
)

@Composable
fun App() {

    Data.folder = remember { mutableStateOf("") }
    Data.saveFolder = remember { mutableStateOf("") }
    showFileDialog = remember { mutableStateOf(false) }
    Data.showImage = remember { mutableStateOf(false) }
    Data.isSongLoaded = remember { mutableStateOf(false) }
    idx = remember { mutableStateOf(0) }


    if (showFileDialog.value) {
        fileDialog {}
    }

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { parsePNG() }) {
                Text("Analyze") //Todo: grey this if song not loaded
            }
            if (Data.isSongLoaded.value) {
                Button(onClick = { showFileDialog.value = true }) {
                    Text("Play/Stop")
                }
            } else {
                Button(onClick = { showFileDialog.value = true }) {
                    Text("Open")
                }
            }
            Button(onClick = { showFileDialog.value = true }) {
                Text("Restart") //Todo: grey this if song not loaded
            }

        }


    }


}

fun printPixelARGB(pixel: Int) {
    val alpha = pixel shr 24 and 0xff
    val red = pixel shr 16 and 0xff
    val green = pixel shr 8 and 0xff
    val blue = pixel and 0xff
    println("argb: $alpha, $red, $green, $blue")
}

fun parsePNG() {
    val filepath = Data.folder.value
    val imageData = javax.imageio.ImageIO.read(File(filepath))
    Data.heightLayers = imageData.height / Data.heightBlockSize
    Data.widthLayers = imageData.width / Data.widthBlockSize
    val totalrows = imageData.width * Data.heightLayers

    Data.pixelVerticalRows = arrayListOf()

    for (i in 0 until totalrows) {
        Data.pixelVerticalRows.add(ArrayList<Pixel>())
    }

    var row = 0
    var internalrow = 0
    var y = 0
    var iBoost = 0
    while (row < totalrows){
        println(" ${iBoost} ${internalrow} ${iBoost+y} ")
        val pixel: Int = imageData.getRGB(internalrow,y+(iBoost*Data.heightBlockSize))
        val alpha = pixel shr 24 and 0xff
        val red = pixel shr 16 and 0xff
        val green = pixel shr 8 and 0xff
        val blue = pixel and 0xff

        Data.pixelVerticalRows[row].add(Pixel(red, green, blue))

        y++
        if (y == Data.heightBlockSize){
            internalrow++
            row++
            y = 0
        }
        if (internalrow != 0){
            if ((internalrow).mod(imageData.width) == 0){
                iBoost ++
                internalrow = 0
            }
        }


    }
}