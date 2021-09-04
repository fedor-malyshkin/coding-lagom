package coding.lagom.impl

import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef}
import akka.stream.scaladsl.Source
import akka.util.Timeout
import akka.{Done, NotUsed}
import coding.lagom.api
import coding.lagom.api.{ClerkAGreetingMessage, ClerkAService, ClerkBService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport.BadRequest
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
  * Implementation of the ClerkAService.
  */
class ClerkAServiceImpl(clarkB: ClerkBService)
                       (clusterSharding: ClusterSharding,
                        persistentEntityRegistry: PersistentEntityRegistry)
                       (implicit ec: ExecutionContext)
  extends ClerkAService {

  /**
    * Looks up the entity for the given ID.
    */
  private def entityRef(id: String): EntityRef[ClerkACommand] =
    clusterSharding.entityRefFor(ClerkAState.typeKey, id)

  implicit val timeout: Timeout = Timeout(5.seconds)

  val log: Logger = LoggerFactory.getLogger(this.getClass)


  override def hello(id: String): ServiceCall[NotUsed, String] = ServiceCall {
    _ =>
      // Look up the sharded entity (aka the aggregate instance) for the given ID.
      val ref = entityRef(id)

      // Ask the aggregate instance the Hello command.
      ref
        .ask[Greeting](replyTo => Hello(id, replyTo))
        .map(greeting => greeting.message)
  }

//  override def hello(id: String): ServiceCall[NotUsed, String] = ServiceCall {
//    _ =>
//      clarkB.hello("xxx").invoke()
//  }


  override def useGreeting(id: String): ServiceCall[ClerkAGreetingMessage, Done] = ServiceCall { request =>
    // Look up the sharded entity (aka the aggregate instance) for the given ID.
    val ref = entityRef(id)

    // Tell the aggregate to use the greeting message specified.
    ref
      .ask[Confirmation](
        replyTo => UseGreetingMessage(request.message, replyTo)
      )
      .map {
        case Accepted => Done
        case _ => throw BadRequest("Can't upgrade the greeting message.")
      }
  }

  override def tick(intervalMs: Int) = ServiceCall { tickMessage =>
    Future.successful(
      Source
        .tick(
          intervalMs.milliseconds,
          intervalMs.milliseconds,
          tickMessage
        )
        .mapMaterializedValue(_ => NotUsed)
    )
  }

  override def greetingsTopic(): Topic[api.ClerkAGreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset { fromOffset =>
      persistentEntityRegistry
        .eventStream(ClerkAEvent.Tag, fromOffset)
        .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(
                            helloEvent: EventStreamElement[ClerkAEvent]
                          ): api.ClerkAGreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) =>
        api.ClerkAGreetingMessageChanged(helloEvent.entityId, msg)
    }
  }
}
