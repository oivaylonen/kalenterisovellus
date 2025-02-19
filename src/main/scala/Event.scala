package mycalendar

class Event (
  val name: String,
  val stertTime: String,
  val endTime: String,
  val description: String,
  val remainder: Boolean,
  val category: Category
):

  override def toString: String =
    s"Event($name, form $stertTime to $endTime, remainder=$remainder, cat=${category.name})"