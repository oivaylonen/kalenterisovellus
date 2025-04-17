package `type`

import java.time.LocalDateTime
import scalafx.scene.paint.Color

case class Event (
  val name: String,
  val startTime: LocalDateTime,
  val endTime: LocalDateTime,
  val description: String = "",
  val remainder: Boolean = false,
  val category: Category = Category("Other", Color.Black),
  val allDay: Boolean = false
):
