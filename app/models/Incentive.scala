package models

import play.api.libs.json._
case class Incentive(iid: String, code: String, start_date: String, end_date: String,tc: String, condition: String, product_id: String)

object IncentiveFormats{
  implicit val incentiveFormats = Json.format[Incentive]
}