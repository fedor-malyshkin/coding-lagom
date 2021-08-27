package coding.lagomstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import coding.lagomstream.api.CodinglagomStreamService
import coding.lagom.api.CodingLagomService

import scala.concurrent.Future

/**
  * Implementation of the CodinglagomStreamService.
  */
class CodinglagomStreamServiceImpl(codinglagomService: CodingLagomService) extends CodinglagomStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(codinglagomService.hello(_).invoke()))
  }
}
