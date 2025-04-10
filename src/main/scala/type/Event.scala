package `type`

class Event (
  val name: String,
  val startTime: String,
  val endTime: String,
  val description: String,
  val remainder: Boolean,
  val category: Category,
  val allDay: Boolean = false
):

  override def toString: String =
    s"Event($name, form $startTime to $endTime, remainder=$remainder, cat=${category.name})"