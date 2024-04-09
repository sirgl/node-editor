package newImpl.vm

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import newImpl.model.Edge
import newImpl.model.Graph

class EdgeVM(private val edge: Edge, private val graph: Graph) {
    fun getStart(density: Density): Offset {
        val node = graph.current.getNode(edge.fromNode)
        val offset = node.offset
        val index = graph.current.getOutputPortIndex(edge.fromNode, edge.fromPort)
        with(density) {
            return Offset(offset.x + 135.dp.toPx(), offset.y + 76.dp.toPx() + (index * 15).dp.toPx())
        }
    }

    fun getEnd(density: Density): Offset {
        val node = graph.current.getNode(edge.toNode)
        val offset = node.offset
        val index = graph.current.getInputPortIndex(edge.toNode, edge.toPort)
        with(density) {
            return Offset(offset.x + 15.dp.toPx(), offset.y + 43.dp.toPx() + (index * 15).dp.toPx())
        }
    }
}

// TODO better to do it in a more stable way
fun getOutputOffsetRelativeToNode(inputCount: Int, outputIndex: Int) : Offset {
    when (inputCount) {
        0 -> {
            when (outputIndex) {
                0 -> return Offset(135.dp.value, 25.dp.value)
            }
        }
        1 -> {

        }
        2 -> {

        }
    }
    TODO()
}