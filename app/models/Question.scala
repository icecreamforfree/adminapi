package models

import play.api.libs.json._
case class Question(qid: String, question: String, types: String) 

object QuestionFormat{
//   implicit val IncentiveWrites = Json.writes[Incentive]
//   implicit val IncentiveReads: Reads[Incentive] = Json.reads[Incentive]

    implicit val questionFormat = Json.format[Question]

}