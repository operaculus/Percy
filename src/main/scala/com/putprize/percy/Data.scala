package com.putprize.percy

import scala.io.Source

import scala.collection.mutable.ArrayBuffer

class Document (
  val vs:Array[Int],
  val cs:Array[Float],
  val n:Int
    ) {
}

object Data {

  def initSize(location:String) = {
    val source = Source.fromFile(location,"UTF-8")
    val line = source.mkString.trim()
    val vs = line.split(",")
    println("line "+line)
    val N = vs(0).toInt
    val M = vs(1).toInt
    (N,M)      
  }
  
  def initData(location:String) = {
    val source = Source.fromFile(location,"UTF-8")
    val lineIterator = source.getLines
    val ms = new ArrayBuffer[(String,Document)]
    for (line <- lineIterator) {
      val us = line.split("\t")
      val name = us(0)
      val vs = new ArrayBuffer[Int]
      val cs = new ArrayBuffer[Float]
      
      val xs = us(1).split(",")
      for (x <- xs){
        val vc = x.split(":")
        val v = vc(0).toInt
        val c = vc(1).toFloat
        vs.append(v)
        cs.append(c)
      }
      
      ms.append((name,new Document(vs.toArray,cs.toArray,vs.size)))
    }
    ms
  }
}