package com.alvinalexander.akkademos

import akka.actor._
import java.awt._
import javax.swing._
import scala.util.Random

/**
 * Case objects used by the actors.
 */
case object PingMessage
case object PongMessage
case object StartMessage
case object StopMessage

case object DisplayMainFrame
case object DoPing
case object DoPong

/**
 * The Ping actor. Notice that its constructor takes an ActorRef.
 */
class Ping(pong: ActorRef) extends Actor {
  var count = 0
  val MAX_NUM_PINGS = 100
  val mainFrameActor = context.actorSelection("../mainFrameActor")
  def incrementCounter { count += 1 }
  def receive = {
    case StartMessage =>
        incrementCounter
        pong ! PingMessage
    case PongMessage => 
        incrementCounter
        if (count > MAX_NUM_PINGS) {
          sender ! StopMessage
          println("ping stopped")
          context.stop(self)
        } else {
          sender ! PingMessage
          mainFrameActor ! DoPing
          Thread.sleep(100)  // blocking! bad!
        }
  }
}

/**
 * The Pong actor.
 */
class Pong extends Actor {
  val mainFrameActor = context.actorSelection("../mainFrameActor")
  def receive = {
    case PingMessage =>
        sender ! PongMessage
        mainFrameActor ! DoPong
        Thread.sleep(100)  // blocking! bad!
    case StopMessage =>
        println("pong stopped")
        context.stop(self)
  }
}

/**
 * An Actor to handle all the interactions with the JFrame. 
 */
class MainFrameActor extends Actor {

  val WIDTH = 600
  val HEIGHT = 400
  val pingPongPanel = new PingPongPanel
  val mainFrame = new JFrame {
    setMinimumSize(new Dimension(WIDTH, HEIGHT))
    setPreferredSize(new Dimension(WIDTH, HEIGHT))
  }
  configureMainFrame

  def receive = {
    case DisplayMainFrame => showMainFrame
    case DoPing => doAction("PING")
    case DoPong => doAction("PONG")
    case _ =>
  }

  def doAction (action: String) { 
    SwingUtilities.invokeLater(new Runnable {
      def run {
        action match {
          case "PING" => pingPongPanel.doPing
          case "PONG" => pingPongPanel.doPong
        }
      }
    })
  }
  
  def configureMainFrame {
    mainFrame.setTitle("Akka Ping Pong Demo")
    mainFrame.setBackground(Color.BLACK)
    mainFrame.getContentPane.add(pingPongPanel)
    mainFrame.setLocationRelativeTo(null)
  }
  
  def showMainFrame {
    SwingUtilities.invokeLater(new Runnable {
      def run {
        mainFrame.setVisible(true)
      }
    })
  }
}

/**
 * The "main" part of the application.
 */
object PingPongTest extends App {
  
  // create the actor system
  val actorSystem = ActorSystem("PingPongSystem")

  // create the actors
  val mainFrameActor = actorSystem.actorOf(Props[MainFrameActor], name = "mainFrameActor")
  val pong = actorSystem.actorOf(Props[Pong], name = "pong")
  val ping = actorSystem.actorOf(Props(new Ping(pong)), name = "ping")

  // display the main frame (jframe)
  mainFrameActor ! DisplayMainFrame
  
  Thread.sleep(5*1000)

  // start the action
  ping ! StartMessage
  
  // shut down the actor system
  //actorSystem.shutdown
}














