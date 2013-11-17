package com.putprize.percy

import scala.math.{log,random}

import java.io.PrintWriter

trait Model {
  
  def K:Int
  
  def M:Int
  
  def pvzBeta(v:Int, z:Int):Double
  
  def logPvzBeta(v:Int, z:Int):Double
  
  def INIT_T_VALUE:Double
  
  def INIT_V_VALUE:Double  
  
}

// For Inference
class Model1(
    val countT: Array[Double],
    val countV: Array[Map[Int,Double]],
    val initT: Double,
    val initV: Double
    ) extends Model {
  
  def K = countT.size
  def M = countV.size
  
  def pvzBeta(v:Int,z:Int) = {
    (countV(v).getOrElse(z, 0.0)+INIT_V_VALUE)/(countT(z)+countV.size*INIT_V_VALUE)
  }
  
  def logPvzBeta(v:Int,z:Int) = {
    log(countV(v).getOrElse(z, 0.0)+INIT_V_VALUE)-log(countT(z)+countV.size*INIT_V_VALUE)
  }  
  
  def INIT_T_VALUE = initT
  def INIT_V_VALUE = initV
}


// For Estimate
class Model2(
    val countT:Array[Double],
    val countV:Array[Array[Double]],
    val initT:Double,
    val initV:Double
    )  extends Model {
  
  def K = countT.size
  val M = countV.size
  
  def pvzBeta(v:Int,z:Int) = {
    (countV(v)(z)+INIT_V_VALUE)/(countT(z)+countV.size*INIT_V_VALUE)
    //countV(v)(z)
  }  
 
  def logPvzBeta(v:Int,z:Int) = {
    log(countV(v)(z)+INIT_V_VALUE) - log(countT(z)+countV.size*INIT_V_VALUE)
    //log(countV(v)(z))
  } 
  
  def INIT_T_VALUE = initT
  def INIT_V_VALUE = initV
  
  def getCountT(z:Int) = countT(z)
  def getCountV(v:Int,z:Int) = countV(v)(z)
  
  def setCountT(z:Int,c:Double) {
    countT(z) = c
  } 
  def setCountV(v:Int,z:Int,c:Double) {
    countV(v)(z) = c
  }  
  
  def sumCountV() = {
    def _sum(A1:Array[Double], A2:Array[Double]) = {
      (0 until K).map( k => A1(k)+A2(k)).toArray
    }
    countV.reduce(_sum)
  }
}

object PercyModel {
  val INIT_T_VALUE = 0.01
  val INIT_V_VALUE = 1e-30
  
  
  def initModel(K:Int, M:Int) = {
    println("Init CountT ...")
    val countT = new Array[Double](K)
    for (z <- 0 until K){
      countT(z) = 0.0
    }
    println("Init CountT Done")
    println("Init CountV ...")
    val countV = new Array[Array[Double]](M)
    for (v <- 0 until M){
      countV(v) = new Array[Double](K)
      for (z <- 0 until K){
        countV(v)(z) = 1.0/M+random+INIT_V_VALUE
        countT(z) += countV(v)(z)
      }
    }
//    for (v <- 0 until M){
//      for (z <- 0 until K){
//        countV(v)(z) = countV(v)(z)/countT(z)
//      }
//    }
    println("Init CountV Done")
    
    new Model2(countT,countV,INIT_T_VALUE/K,INIT_V_VALUE)
  }
 
  def saveCountT(countT:Array[Double],location:String) {
    val out = new PrintWriter(location)
    for (i <- 0 until countT.size){
      out.write(countT(i)+"\n")
    }
    out.close 
  }
  
  def saveCountV(countT:Array[Double],countV:Array[Array[Double]],location:String) {
    val out = new PrintWriter(location)
    for (i <- 0 until countV.size){
      val vs = (0 until countV(i).size).par.
      			map( z => {(z,countV(i)(z))}).
      			//filter( zv => (zv._2/countT(zv._1)) > 1e-4)
      			filter( zv => (zv._2) > 1)
      val line = 
        if (vs.size > 0) 
        	vs.map( v => {v._1.toString+":"+v._2.toString}).reduce(_+" "+_)
        else
        	""
      out.write(line+"\n")
    }
    out.close
  }  
  
}
