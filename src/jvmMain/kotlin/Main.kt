import Data.idx
import Data.showFileDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import kotlin.math.absoluteValue

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
fun redLine() {
    Box(
        modifier = Modifier.width(10.dp).fillMaxHeight().clip(RectangleShape).background(Color(255, 0, 0, 100))
    )
}

@Composable
fun songThingyPixels() {

    var conta = 4
    val linearized =
        arrayListOf<Pixel>() //todo: there's probably a better way to do this, but at this scale it should not matter much

    //Cheating the UI by adding fake black row at start
    for (i in 0 until 9 * 4) {
        linearized.add(Pixel(40, 60, 80))
    }

    for (pixelVerticalRow in Data.pixelVerticalRows) {
        for (pixel in pixelVerticalRow) {
            linearized.add(pixel)
            conta++
        }
    }

    //Cheating the UI by adding fake black row at end as well
    for (i in 0 until 9 * 100) {
        linearized.add(Pixel(40, 60, 80))
        conta++
    }
    Box {


        LazyHorizontalGrid(
            state = Data.listState,
            rows = GridCells.Fixed(Data.heightBlockSize),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(conta) { item ->
                Box(
                    modifier = Modifier.size(10.dp).clip(RectangleShape)
                        .background(Color(linearized[item].r, linearized[item].g, linearized[item].b))
                )
            }
        }
        Box(modifier = Modifier.padding(start = 40.dp)) { redLine() }

    }

}


@Composable
fun App() {

    Data.folder = remember { mutableStateOf("") }
    Data.saveFolder = remember { mutableStateOf("") }
    showFileDialog = remember { mutableStateOf(false) }
    Data.showImage = remember { mutableStateOf(false) }
    Data.isSongLoaded = remember { mutableStateOf(false) }
    Data.showPixels = remember { mutableStateOf(false) }
    Data.listState = rememberLazyGridState()
    Data.isPlayingSong = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    var lineNumber by remember { mutableStateOf(0) }
    val progress by remember { mutableStateOf(0.1f) }
    var multiplier by remember { mutableStateOf(1) }

    if (showFileDialog.value) {
        fileDialog {}
    }

    if (Data.showPixels.value) {
        Column(verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxHeight(0.3f)) { songThingyPixels() }
    }






    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        LinearProgressIndicator(
            backgroundColor = Color.White,

            progress = (lineNumber.toFloat()) / (Data.pixelVerticalRows.size * 9),
            color = Color.Blue,
            modifier = Modifier.padding(top = 100.dp, bottom = 100.dp).align(Alignment.CenterHorizontally).scale(2f)
        )

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { parsePNG() }) {
                Text("Analyze") //Todo: grey this if song not loaded
            }
            if (Data.isSongLoaded.value) {
                Button(onClick = {
                    if (!Data.isPlayingSong.value) {
                        Data.isPlayingSong.value = true
                        scope.launch {
                            while (Data.isPlayingSong.value) {
                                println("$lineNumber ${Data.pixelVerticalRows.size * 9}")
                                Data.listState.animateScrollToItem(lineNumber)
                                lineNumber += (Data.heightBlockSize * multiplier)
                                if (lineNumber + (9 * 3) > (Data.pixelVerticalRows.size * 9)) {
                                    Data.isPlayingSong.value = false
                                }
                                delay(100)
                            }
                        }
                    } else {

                        Data.isPlayingSong.value = false

                    }


                }) {
                    Text("Play/Stop")
                }
            } else {
                Button(onClick = { showFileDialog.value = true }) {
                    Text("Open")
                }
            }
            Button(onClick = { scope.launch { Data.listState.scrollToItem(0); lineNumber = 0 } }) {
                Text("Restart") //Todo: grey this if song not loaded
            }

            Button(onClick = {
                scope.launch {
                    Data.listState.animateScrollToItem(Data.pixelVerticalRows.size * 9 - (20 *9))
                    lineNumber = Data.pixelVerticalRows.size * 9 - (20 *9)
                }
            }) {
                Text("DEV")
            }

        }


    }


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
    while (row < totalrows) {
        val pixel: Int = imageData.getRGB(internalrow, y + (iBoost * Data.heightBlockSize))
        val alpha =
            pixel shr 24 and 0xff //Todo: Alpha can probably be ignored as it does not seem to be used for colors?
        val red = pixel shr 16 and 0xff
        val green = pixel shr 8 and 0xff
        val blue = pixel and 0xff

        Data.pixelVerticalRows[row].add(Pixel(red, green, blue))

        y++
        if (y == Data.heightBlockSize) {
            internalrow++
            row++
            y = 0
        }
        if (internalrow != 0) {
            if ((internalrow).mod(imageData.width) == 0) {
                iBoost++
                internalrow = 0
            }
        }
    }
    Data.showPixels.value = true
}