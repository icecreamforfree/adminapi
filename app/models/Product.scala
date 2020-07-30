package models

import play.api.libs.json._

case class Product(pid: String, brand: String, name: String, price: Double, sales_url: String)

object ProductFormats{
    implicit val productFormat = Json.format[Product]
}