package db

import java.lang.Object
import play.api.libs.json._
import models._

trait Prod {
    def getProduct: JsValue
    def getReview(id: String): List[Review]
    def deleteProduct(id: String) : Boolean
    // def addProduct: Unit
    // def updateProduct: Unit 
}