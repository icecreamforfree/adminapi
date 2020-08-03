package models

import play.api.libs.json._
import java.util.Date

case class Incentive(iid: String, code: String, start_date: Date, end_date: Date,tc: String, condition: String, product_id: String)

object IncentiveFormats{
  implicit val incentiveFormats = Json.format[Incentive]
}