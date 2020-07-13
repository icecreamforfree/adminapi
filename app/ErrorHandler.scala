package ErrorHandler

import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent._
import javax.inject.Singleton

@Singleton
class ErrorHandler extends HttpErrorHandler {

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    println("testing error handler")
    
    Future.successful(
        statusCode match {
        case 400=> Ok(" Oops!!!! BAD REQUEST")
        case 401=> Ok(" Oops!!!! UNAUTHORIZED")
        case 404=> Ok("Oops!!!! Page not Found")
        case 405=> Ok("invalid input " + message)
        case 500=> Ok(" Oops!!!! Internal Server Error")
        case _=> Ok("error" + message)
        }
        // Status(statusCode)(" A client error occurred: " + message)
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }
}