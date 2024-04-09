package newImpl.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import newImpl.model.AddEdge
import newImpl.model.AddNode
import newImpl.model.Change
import newImpl.model.Graph
import newImpl.vm.GraphVM

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Workspace(vm: GraphVM, applyChanges: (changes: List<Change>) -> Unit) {
    Box(
        Modifier.fillMaxSize()
            .onPointerEvent(eventType = PointerEventType.Move, onEvent = {
                vm.cursorPosition.value = it.changes.first().position
            })
    ) {
        DragableScreen {
            Column {
                Text(vm.cursorPosition.value.toString(), fontSize = 10.sp)
                Text("Nodes: " + vm.nodes.value.size, fontSize = 10.sp)
            }
            ContextMenuArea(items = {
                listOf(
                    ContextMenuItem(
                        label = "Create new node",
                        onClick = {
                            val nodeName = vm.graph.current.suggestNewNodeName("Node")
                            applyChanges(listOf(AddNode(
                                name = nodeName,
                                position = vm.cursorPosition.value,
                                inputPorts = listOf("Input 1"),
                                outputPorts = listOf("Output 1", "Output 2", "Output 3")
                            )))
                        })
                )
            }) {
                Box(Modifier.fillMaxSize()) {
                    for (node in vm.nodes.value) {
                        Node(true, node, vm, applyChanges, vm.dragModel)
                    }
                }
            }

            Edges(vm)
        }
    }
}

@Composable
private fun Edges(vm: GraphVM) {
    Canvas(Modifier.fillMaxSize()) {
        val startOffset = vm.dragModel.startOffset
        if (vm.dragModel.isDragging && startOffset != null) {
            val paint = Paint()
            paint.strokeWidth = 5f
            paint.color = Color.Red
            paint.style = PaintingStyle.Stroke

            val path = org.jetbrains.skia.Path()
            path.apply {
                val startX = startOffset.x + 5.dp.toPx()
                val startY = startOffset.y + 5.dp.toPx()
                moveTo(startX, startY)
                val cursor = vm.cursorPosition.value
                cubicTo(startX + 100, startY, cursor.x - 100, cursor.y, cursor.x, cursor.y)
            }

            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawPath(path, paint.asFrameworkPaint())
            }
        }


        val paint = Paint()
        paint.strokeWidth = 5f
        paint.color = Color.Black
        paint.style = PaintingStyle.Stroke
        val path = org.jetbrains.skia.Path()
        for (edgeVM in vm.edges.value) {
            path.apply {
                val start = edgeVM.getStart(this@Canvas)
                val startX = start.x
                val startY = start.y
                val end = edgeVM.getEnd(this@Canvas)
                val endX = end.x
                val endY = end.y
                moveTo(startX, startY)
                lineTo(endX, endY)
            }

            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawPath(path, paint.asFrameworkPaint())
            }
            // TODO draw edge

        }
    }
}


class DragModelImpl(private val onDragEnd: (inputPortId: PortId) -> Unit) : DragModel {
    private val _isDragging = mutableStateOf(false)
    override var isDragging: Boolean
        get() = _isDragging.value
        set(value) {
            if (!value) {
                val portId = inputPortId
                if (portId != null) {
                    onDragEnd(portId)
                }
            }
            _isDragging.value = value
        }

    override var startOffset: Offset? by mutableStateOf(null)
    override var inputPortId: PortId? by mutableStateOf(null)

}

fun main() {
    val graph = Graph()
    val vm = GraphVM(graph)
    graph.update(listOf(AddNode("Node", Offset(100f, 200f), listOf("Input 1", "Input 2"), listOf("Output 1"))))
    graph.update(listOf(AddNode("Node1", Offset(400f, 600f), listOf("Input 1"), listOf("Output 1", "Output 2"))))
    val nodeIds = graph.current.nodes.keys.toList()
    val firstNode = nodeIds[0]
    val secondNode = nodeIds[1]
    val firstNodeOutputPorts = graph.current.getOutputPorts(firstNode)
    val inputPorts = graph.current.getInputPorts(secondNode)
    graph.update(listOf(AddEdge(
        fromNode = firstNode,
        fromPort = firstNodeOutputPorts.first().id,
        toNode = secondNode,
        toPort = inputPorts.first().id
    )))
    application {
        println("App recreated")
        Window(onCloseRequest = ::exitApplication) {
            Workspace(vm = vm, applyChanges = { changes ->
                graph.update(changes)
            })
        }
    }
}