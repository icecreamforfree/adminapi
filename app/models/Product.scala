package models

case class Product(_id: String, brand: String, name: String, price: Double, salesURL: String)

object JsonFormats{
    import play.api.libs.json.{Json, OFormat}

    implicit val productFormat = Json.format[Product]

}