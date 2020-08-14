package auth

import javax.inject.Inject
import pdi.jwt._
import play.api.http.HeaderNames
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scala.language.postfixOps
import com.google.firebase.auth.{FsirebaseAuth, FirebaseToken, FirebaseAuthException}

// Our custom action implementation
class AuthActionZ @Inject()(bodyParser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(bodyParser) {

  // A regex for parsing the Authorization header value
  private val headerTokenRegex = """Bearer (.+?)""".r


  // Called when a request is invoked. We should validate the bearer token here
  // and allow the request to proceed if it is valid.
  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] =
    extractBearerToken(request) map { token =>
      validateJwt(token) match {
        case _ : FirebaseToken => block(request)      // token was valid - proceed!
        case _ : Throwable => Future.successful(Results.Unauthorized("failed"))  // token was invalid - return 401
        // case true => block(request)      // token was valid - proceed!
        // case false => Future.successful(Results.Unauthorized("failed"))  // token was invalid - return 401
      }
    } getOrElse Future.successful(Results.Unauthorized)     // no token was sent - return 401

  // Helper for extracting the token value
  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.headers.get(HeaderNames.AUTHORIZATION) collect {
      case headerTokenRegex(token) => token
    }

  def validateJwt(token: String) = {
    try {
      val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
      true
    }
    catch {
      case e: FirebaseAuthException => Failure(e)
      // case e: Throwable => println(e)
      e
    }
  } 
}