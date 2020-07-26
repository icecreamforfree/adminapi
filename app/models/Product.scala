package models
import play.api.libs.json.Json

case class Product(_id: String, brand: String, name: String, price: Double, salesURL: String)

object JsonFormats{
    implicit val productFormat = Json.format[Product]

}