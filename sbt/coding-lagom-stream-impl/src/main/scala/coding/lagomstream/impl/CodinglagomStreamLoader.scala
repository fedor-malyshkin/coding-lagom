package coding.lagomstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import coding.lagomstream.api.CodinglagomStreamService
import coding.lagom.api.CodingLagomService
import com.softwaremill.macwire._

class CodinglagomStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new CodinglagomStreamApplication(context) {
      override def serviceLocator: NoServiceLocator.type = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new CodinglagomStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[CodinglagomStreamService])
}

abstract class CodinglagomStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[CodinglagomStreamService](wire[CodinglagomStreamServiceImpl])

  // Bind the CodinglagomService client
  lazy val codinglagomService: CodingLagomService = serviceClient.implement[CodingLagomService]
}
