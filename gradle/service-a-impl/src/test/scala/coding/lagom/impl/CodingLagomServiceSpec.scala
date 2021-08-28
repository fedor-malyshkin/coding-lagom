package coding.lagom.impl

import coding.lagom.api._
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class CodingLagomServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {
/*
  private val server = ServiceTest.startServer(
    ServiceTest.defaultSetup
      .withCassandra()
  ) { ctx =>
    new CodingLagomApplication(ctx) with LocalServiceLocator
  }

  val client: CodingLagomService = server.serviceClient.implement[CodingLagomService]

  override protected def afterAll(): Unit = server.stop()

  "coding-lagom service" should {

    "say hello" in {
      client.hello("Alice").invoke().map { answer =>
        answer should ===("Hello, Alice!")
      }
    }

    "allow responding with a custom message" in {
      for {
        _ <- client.useGreeting("Bob").invoke(GreetingMessage("Hi"))
        answer <- client.hello("Bob").invoke()
      } yield {
        answer should ===("Hi, Bob!")
      }
    }
  }


 */
}
