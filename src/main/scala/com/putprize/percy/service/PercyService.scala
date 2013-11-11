package com.putprize.percy.service

import scala.collection.mutable.{HashMap => HMap}

import com.putprize.percy.{Model,Model1,Document}

import org.apache.log4j.Logger

class PercyService(
    val cT:Map[String,Double],
    val cV:Map[String,Map[String,Double]]
    ) {
  
  val _log = Logger.getLogger(this.getClass.getName)
  
  private
  val _countT = convertCountT(cT)
  val TM1 = _countT._1
  val TM2 = _countT._2
  val countT = _countT._3
  _log.info("Init CountT Done "+countT.size)
  
  private
  val _countV = convertCountV(cV)
  val VM1 = _countV._1
  val VM2 = _countV._2
  val countV = _countV._3
  _log.info("Init CountV Done "+countV.size)
  
  val INIT_V_VALUE = 1e-10
  val INIT_T_VALUE = 1e-5
  
  val K = countT.size
  val M = countV.size
  
  val model:Model = new Model1(countT,countV,INIT_T_VALUE,INIT_V_VALUE)
  
  /* ======================================================================= */
  
  def convertCountT(countT:Map[String,Double]) = {
    
    val M1 = HMap[String,Int]()
    val M2 = HMap[Int,String]()
    
    val T = new Array[Double](countT.size)
    var count = 0
    for (z <- countT.keySet){
      M1(z) = count
      M2(count) = z
      T(count) = countT(z)
      count += 1
    }
    
    (M1.toMap,M2.toMap,T)
  }
  
  def convertCountV(countV:Map[String,Map[String,Double]]) = {
    
    val M1 = HMap[String,Int]()
    val M2 = HMap[Int,String]()
    
    val T = new Array[Map[Int,Double]](countV.size)
    
    var count = 0
    for (v <- countV.keySet){
      M1(v) = count
      M2(count) = v
      T(count) = countV(v).map( kv => {
        (TM1(kv._1),kv._2.toDouble)
      }).toMap[Int,Double]
      count += 1
    }
    
    (M1.toMap,M2.toMap,T) // VM1,VM2,countV
  }
  
  /* ======================================================================= */
  
  
  def parseDocument(text:Array[String]) = {
    val m = HMap[String,Int]() 

    for (v <- text if VM1.contains(v)){
      val c = m.getOrElse(v, 0)
      m(v) = c+1
    }
    
    val vs = new Array[Int](m.size)
    val cs = new Array[Float](m.size)
    
    val ns = m.toArray
    for ( i <- 0 until ns.size){
      val v = ns(i)._1
      val c = ns(i)._2
      
      vs(i) = VM1(v)
      cs(i) = c
    }
    
    new Document(vs,cs,ns.size)
  }
  
  def parseDocument(m:Map[String,Double]) = {
    val ns = m.filter( u => VM1.contains(u._1) ).toList
    
    val vs = new Array[Int](m.size)
    val cs = new Array[Float](m.size)
    
    for (i <- 0 until ns.size){
      val v = ns(i)._1
      val c = ns(i)._2
      
      vs(i) = VM1(v)
      cs(i) = c.toFloat
    }
    
    new Document(vs,cs,ns.size) 
  }
  
  def formatTopic(zs:Array[Double]) = {
    val s = zs.reduce(_+_)
    
    (0 until zs.size).map( z => {
      (TM2(z),zs(z)/s)
    }).toMap
  }

}
