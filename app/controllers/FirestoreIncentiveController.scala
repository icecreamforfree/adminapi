package controllers

import com.google.common.collect.ImmutableMap
import javax.inject._
import play.api.mvc._
import db.FirebaseSetup
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.lang.Object
import com.google.api.gax.rpc.NotFoundException
import scala.collection.mutable.HashMap 
import java.util.Map
import scala.collection.JavaConverters._
// import db.Ehandler._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class FirestoreIncentiveController @Inject()(cc: ControllerComponents)(implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {

  val app = new FirebaseSetup
  case class Incentive(iid: String, code: String, start_date: String, end_date: String,tc: String, condition: String, product_id: String)

  implicit val IncentiveWrites = Json.writes[Incentive]
  implicit val IncentiveReads: Reads[Incentive] = Json.reads[Incentive]

  def getIncentive = Action {
    var list = List[List[String]]()

    val querySnapshot = app.db.collection("incentive").get().get()
    val docs = querySnapshot.getDocuments()

    docs.forEach(doc => {
        val iid = doc.getId()
        val code = doc.getString("code")
        val sdate = doc.getString("start_date")
        val edate = doc.getString("end_date")
        val tc = doc.getString("tc")
        val condition = doc.getString("condition")
        val pid = doc.getString("product_id")
        list = List(iid,code, sdate,edate,tc,condition,pid) :: list
    })

    val seclist = list.map(l => Incentive(iid= l(0), code= l(1),  start_date= l(2), end_date = l(3), tc=l(4), condition=l(5) ,product_id=l(6)))
    val result : JsValue = Json.toJson(seclist)
    Ok(result)
  }
  

 def addIncentive = Action(parse.json) { request =>
 
  val result = Json.fromJson[List[Incentive]](request.body)
  
  result match {
    case JsSuccess(incentives: List[Incentive], path: JsPath) =>
      incentives.map(incentive => collect(incentive))

      def collect(incentive: Incentive) = {
        val db = app.db.collection("incentive").document(incentive.iid)
        val p = new ImmutableMap.Builder[Object,Object]()
        .put("code", incentive.code)
        .put("start_date" , incentive.start_date)
        .put("end_date", incentive.start_date)
        .put("tc", incentive.tc)
        .put("condition", incentive.condition)
        .put("product_id", incentive.product_id)
        .build()

        val add = db.set(p)
      }
      Ok(Json.obj("status" -> "succeed", "operation" -> "add"))
    case e @ JsError(_) =>
      var errorList = List[JsObject]()
      val errors = JsError.toJson(e).fields

      for (err <- errors){
        errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
      }

      Results.Status(405)(Json.obj("status" -> true,"operation" -> "Invalid Input", "error" -> Json.obj("data" -> errorList)))
  }
 }

 def updateIncentive = Action(parse.json) { request =>
  val result = Json.fromJson[List[Incentive]](request.body)

  result match {
    case JsSuccess(incentives: List[Incentive], path: JsPath) => 
        try{
          for(incentive <- incentives){
            val db = app.db.collection("incentive").document(incentive.iid)
            val p = new ImmutableMap.Builder[String, Object]()
            .put("code", incentive.code)
            .put("start_date" , incentive.start_date)
            .put("end_date", incentive.start_date)
            .put("tc", incentive.tc)
            .put("condition", incentive.condition)
            .put("product_id", incentive.product_id)
            .build()
            val add = db.update(p)
          }
        Ok(Json.obj("status" -> "succeed", "operation" -> "update"))
      
         } catch   {
          //  Catches all types of error/exceptions
          //  Use case to handle known/possible errors and, e: Throwable for anything else

          case notFound: java.util.concurrent.ExecutionException => BadRequest("Doc not found")
          // @todo Solve this shit, find the specific error exception class instead of a catch all case of ExecutionException
          case e: Throwable =>{
            println("gsus test", e.getClass().getSimpleName())
            BadRequest(Json.obj("UNKNOWN" -> e.getMessage))}
         }
        
    case e @ JsError(_) =>
      var errorList = List[JsObject]()
      val errors = JsError.toJson(e).fields

      BadRequest(Json.obj("mes" -> JsError.toJson(e)))

      for (err <- errors){
        errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
      }

      Results.Status(405)(Json.obj("status" -> true ,"operation" -> "Invalid Input", "error" -> Json.obj("data" -> errorList)))  
      }

   }

 def deleteIncentive(id: String) = Action { request =>
  val exist = app.db.collection("incentive").document(id).get().get().exists()

  exist match {
    case true => {
      val db = app.db.collection("incentive").document(id).delete()
      Ok(Json.obj("status" -> "succeed", "operation" -> "delete"))
    }
    case _ => Results.Status(400)(id + " doesnt exist")
  }
 }
}
