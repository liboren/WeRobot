package modules

import actor.{Master, Slave}
import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}
import play.api.libs.concurrent.AkkaGuiceSupport

/**
 * User: Taoz
 * Date: 8/23/2015
 * Time: 10:55 PM
 */

class ActorModule(environment: Environment,
                  configuration: Configuration) extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {

    bindActor[Master]("configured-master")
    bindActor[Slave]("configured-slave")

  }


}
