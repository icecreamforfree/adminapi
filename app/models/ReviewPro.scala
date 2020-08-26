package models

import play.api.libs.json._
import java.util.Date

case class ReviewPro(qid: String, incentive_date: String, incentive_id: String, product_id : String, reviews : String, user_id: String)

object ReviewProFormats{
    implicit val reviewFormat = Json.format[ReviewPro]
}
