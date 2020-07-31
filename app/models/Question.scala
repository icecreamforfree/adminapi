package models

import play.api.libs.json._
case class Question(qid: String, question: String, types: String) 

object QuestionFormats{
    implicit val questionFormat = Json.format[Question]
}