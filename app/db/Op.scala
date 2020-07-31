package db

import java.lang.Object
import play.api.libs.json._
import models._

trait Op {
    def getProduct: JsValue
    def getReview(id: String): List[Review]
    def deleteProduct(id: String) : Boolean
    def addProduct(products: List[Product]): Unit
    def updateProduct(products: List[Product]): Unit 

    def getIncentive: JsValue
    def deleteIncentive(id: String) : Boolean
    def addIncentive(incentives: List[Incentive]): Unit
    def updateIncentive(incentives: List[Incentive]): Unit 

    def getInfoQuestion: JsValue
    def deleteInfoQuestion(id: String) : Boolean
    def addInfoQuestion(questions: List[Question]): Unit
    def updateInfoQuestion(questions: List[Question]): Unit 

    def getReviewQuestion: JsValue
    def deleteReviewQuestion(id: String) : Boolean
    def addReviewQuestion(questions: List[Question]): Unit
    def updateReviewQuestion(questions: List[Question]): Unit 
}