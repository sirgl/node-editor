package newImpl.model

import androidx.compose.ui.geometry.Offset
import java.util.*

sealed class Change
class AddEdge(val fromNode: UUID, val fromPort: UUID, val toNode: UUID, val toPort: UUID) : Change()
class AddNode(val name: String, val position: Offset, val inputPorts: List<String>, val outputPorts: List<String>, val content: NodeContent) : Change()
class ReplaceContent(val nodeId: UUID, val newContent: NodeContent) : Change()
class UpdateNode(val newNode: Node) : Change()