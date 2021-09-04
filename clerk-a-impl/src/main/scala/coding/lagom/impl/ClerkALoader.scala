package coding.lagom.impl

import akka.cluster.sharding.typed.scaladsl.Entity
import coding.lagom.api.{ClerkAService, ClerkBService}
import com.lightbend.lagom.scaladsl.akka.discovery.AkkaDiscoveryComponents
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import play.api.libs.ws.ahc.AhcWSComponents


class ClerkALoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new ClerkAApplication(context) with AkkaDiscoveryComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ClerkAApplication(context) with AkkaDiscoveryComponents

  override def describeService = Some(readDescriptor[ClerkAService])
}

abstract class ClerkAApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[ClerkAService](wire[ClerkAServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = ClerkASerializerRegistry

  lazy val clerkBService: ClerkBService = serviceClient.implement[ClerkBService]

  // Initialize the sharding of the Aggregate. The following starts the aggregate Behavior under
  // a given sharding entity typeKey.
  clusterSharding.init(
    Entity(ClerkAState.typeKey)(entityContext => ClerkABehavior.create(entityContext))
  )

}
