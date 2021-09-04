package coding.lagom.api

import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

object ClerkAService {
  val TOPIC_NAME = "greetingsA"
}

/**
  * The coding-lagom service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the ClerkAService.
  */
trait ClerkAService extends Service {

  /**
    * Example: curl http://localhost:9000/api/hello/Alice
    */
  def hello(id: String): ServiceCall[NotUsed, String]

  /**
    * Example: curl -H "Content-Type: application/json" -X POST -d '{"message": "Hi"}' http://localhost:9000/api/hello/Alice
    */
  def useGreeting(id: String): ServiceCall[ClerkAGreetingMessage, Done]

  def tick(interval: Int): ServiceCall[String, Source[String, NotUsed]]


  /**
    * This gets published to Kafka.
    */
  def greetingsTopic(): Topic[ClerkAGreetingMessageChanged]

  override final def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named("clerk-b")
      .withCalls(
        pathCall("/api/hello/:id", hello _),
        pathCall("/api/hello/:id", useGreeting _),
        pathCall("/tick/:interval", tick _)
      )
      .withTopics(
        topic(ClerkAService.TOPIC_NAME, greetingsTopic _)
          // Kafka partitions messages, messages within the same partition will
          // be delivered in order, to ensure that all messages for the same user
          // go to the same partition (and hence are delivered in order with respect
          // to that user), we configure a partition key strategy that extracts the
          // name as the partition key.
          .addProperty(
            KafkaProperties.partitionKeyStrategy,
            PartitionKeyStrategy[ClerkAGreetingMessageChanged](_.name)
          )
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}

/**
  * The greeting message class.
  */
case class ClerkAGreetingMessage(message: String)

object ClerkAGreetingMessage {
  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[ClerkAGreetingMessage] = Json.format[ClerkAGreetingMessage]
}


/**
  * The greeting message class used by the topic stream.
  * Different than [[ClerkAGreetingMessage]], this message includes the name (id).
  */
case class ClerkAGreetingMessageChanged(name: String, message: String)

object ClerkAGreetingMessageChanged {
  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[ClerkAGreetingMessageChanged] = Json.format[ClerkAGreetingMessageChanged]
}
