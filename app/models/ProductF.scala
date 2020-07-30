package models

import play.api.libs.json._

case class ProductF(pid: String, brand: String, name: String, price: Double, salesURL: String)

object ProductFFormats{
    implicit val productFFormat = Json.format[ProductF]
}