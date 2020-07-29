package controllers

import javax.inject._
import play.api.mvc._
import db.FirebaseSetup
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.lang.Object


@Singleton
class MainController @Inject()(cc: ControllerComponents)(implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {
    // val data = System.getenv("DATABASE")
    // def getProduct = Action {
    //   if(data == "PSQL") {
        
    //   }
    //   else if(data == "FIRESTORE") Ok("firestore")
    // }

    // def addProduct = Action {
    //   if(data == "PSQL") Redirect("/psql/products")
    //   else if(data == "FIRESTORE") Redirect("/firestore/products")
    // } 
}
