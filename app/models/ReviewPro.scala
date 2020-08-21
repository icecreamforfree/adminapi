package models

import play.api.libs.json._

case class ReviewPro(qid: String, incentive_date: String, incentive_id: String, product_id : String)

object ReviewProFormats{
    implicit val reviewFormat = Json.format[ReviewPro]
}
