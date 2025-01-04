package requests

import play.api.libs.json._

case class Visitor(
                    visitorId: Option[Int] = None,
                    name: String,
                    email: String,
                    contactNumber: String
                  )

object Visitor {
  implicit val visitorReads: Reads[Visitor] = Json.reads[Visitor]

  implicit val visitorWrites: Writes[Visitor] = Json.writes[Visitor]

  implicit val visitorFormat: Format[Visitor] = Format(visitorReads, visitorWrites)
}
