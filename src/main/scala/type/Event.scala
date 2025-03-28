package `type`

import `type`.Category

class Event (
  val name: String,
  val startTime: String,
  val endTime: String,
  val description: String,
  val remainder: Boolean,
  val category: Category
):

  override def toString: String =
    s"Event($name, form $startTime to $endTime, remainder=$remainder, cat=${category.name})"