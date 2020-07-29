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
class FirestoreReviewQuestionController @Inject()(cc: ControllerComponents)(implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {

  val app = new FirebaseSetup
  case class Question(qid: String, question: String, types: String) 

  implicit val QuestionWrites  = Json.writes[Question]
  implicit val QuestionReads: Reads[Question] = Json.reads[Question]

  def getQuestion = Action {
    var list = List[List[String]]()

    val querySnapshot = app.db.collection("review_question").get().get()
    val docs = querySnapshot.getDocuments()

    docs.forEach(doc => {
      val qid = doc.getId()
      val question = doc.getString("question")
      val types = doc.getString("type")
      list = List(qid,question,types) :: list
    })

    val seclist = list.map(l => Question(qid= l(0), question= l(1), types = l(2)))
    val result : JsValue = Json.toJson(seclist)
    Ok(result)
  }
  
 def addQuestion = Action(parse.json) { request =>
 
  val result = Json.fromJson[List[Question]](request.body)
  
  result match {
    case JsSuccess(questions: List[Question], path: JsPath) =>
      questions.map(question => collect(question))

      def collect(question: Question) = {
        val db = app.db.collection("review_question").document(question.qid)
        val p = new ImmutableMap.Builder[Object,Object]()
        .put("question", question.question)
        .put("types" , question.types)
        .build()

        val add = db.set(p)
      }
      Ok("succeed")
    case e @ JsError(_) =>
      var errorList = List[JsObject]()
      val errors = JsError.toJson(e).fields

      for (err <- errors){
        errorList = Json.obj("location" -> err._1 , "details" -> err._2(0)("msg")(0)) :: errorList
      }

      Results.Status(405)(Json.obj("status" -> true,"description" -> "Invalid Input", "error" -> Json.obj("data" -> errorList)))
  }
 }

 def updateQuestion = Action(parse.json) { request =>
  val result = Json.fromJson[List[Question]](request.body)

  result match {
    case JsSuccess(questions: List[Question], path: JsPath) => 
        try{
          for(question <- questions){
            val db = app.db.collection("review_question").document(question.qid)
            val p = new ImmutableMap.Builder[String, Object]()
            .put("question", question.question)
            .put("types" , question.types)
            .build()
            val add = db.update(p)
            println(add.get())
          }
          Ok("done")
      
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

      Results.Status(405)(Json.obj("status" -> true ,"description" -> "Invalid Input", "error" -> Json.obj("data" -> errorList)))  
      }

   }

 def deleteQuestion(id: String) = Action { request =>
  val exist = app.db.collection("review_question").document(id).get().get().exists()

  exist match {
    case true => {
      val db = app.db.collection("review_question").document(id).delete()
      Ok("data with id " + id + " is deleted") 
    }
    case _ => Results.Status(400)(id + " doesnt exist")
  }
 }
}
