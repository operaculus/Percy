package com.putprize
//
//import akka.actor._
//import akka.pattern.ask
//import akka.util.duration._
//import akka.util.Timeout
//
//case object Tick
//case object Get
//
//class Counter extends Actor {
//  var count = 0
//
//  def receive = {
//    case Tick => count += 1
//    case Get  => sender ! count
//  }
//}
//
//object Percy extends App {
//  val system = ActorSystem("Percy")
//
//  val counter = system.actorOf(Props[Counter])
//
//  counter ! Tick
//  counter ! Tick
//  counter ! Tick
//
//  implicit val timeout = Timeout(5 seconds)
//
//  (counter ? Get) onSuccess {
//    case count => println("Count is " + count)
//  }
//
//  system.shutdown()
//}

import java.io.File
import com.typesafe.config._

import scala.io.Source
import scala.collection.mutable.ArrayBuffer

import com.putprize.percy.PercyStream

object Percy extends App {
  
  println("Hello Percy")
  
  val c= ConfigFactory.parseFile(new File("Percy.properties"))
  
  val x = c.getInt("CPU_NUM")
  import collection.parallel.ForkJoinTasks.defaultForkJoinPool._

  println(getParallelism)
  //setParallelism(x)
  println(getParallelism)
  
  
  val mode = c.getString("MODE")
  
  if (mode == "one"){
    val locationSize = c.getString("LOCATION_SIZE")
    val ROOT = c.getString("ROOT_TOPIC")
    
    val locationData = c.getString("LOCATION_DATA")
    
    val locationCT = c.getString("LOCATION_CT")
    val locationCV = c.getString("LOCATION_CV")
    
    val K = c.getInt("TOPIC_NUM")
    val I = c.getInt("ITER_NUM")
    
    PercyStream.run(locationSize, locationData, locationCT, locationCV, ROOT, K, I)
  }
  
  if (mode == "stream"){
    val locationSize = c.getString("LOCATION_SIZE")
    val ROOT1 = c.getString("ROOT_DATA")
    val ROOT2 = c.getString("ROOT_TOPIC")
  
    val locationNs = c.getString("LOCATION_NS")
    val ns = new ArrayBuffer[String]
  
    val source = Source.fromFile(locationNs,"UTF-8")
    val lineIterator = source.getLines
    for (line <- lineIterator) {
       ns.append(line.trim())
    }
  
    val locationCT = c.getString("LOCATION_CT")
    val locationCV = c.getString("LOCATION_CV")
  
    val saveN = c.getInt("SAVE_NUM")
    val bSize = c.getInt("BATCH_NUM")
  
    val K = c.getInt("TOPIC_NUM")
    val I = c.getInt("ITER_NUM")
  
    PercyStream.run(locationSize,ns.toArray,locationCT,locationCV,ROOT1,ROOT2,K,bSize,saveN,I)    
  }
  
  if (mode == "server"){
    val locationModelCT = c.getString("LOCATION_MODEL_CT")
    val locationModelCV = c.getString("LOCATION_MODEL_CV")
    val serverPort = c.getInt("SERVER_PORT")
    
    PercyServer.run(locationModelCT, locationModelCV, serverPort)
  }

}