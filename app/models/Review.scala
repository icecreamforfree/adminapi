package models

import play.api.libs.json._

case class Review(reviews: String)

object ReviewFormats{
    implicit val reviewFormat = Json.format[Review]
}