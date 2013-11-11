package com.putprize.percy

import scala.io.Source

import net.liftweb.json.JsonAST
import net.liftweb.json.Extraction
import net.liftweb.json.Printer
import net.liftweb.json.parse

object Count {
  
  implicit val FORMATS = net.liftweb.json.DefaultFormats
  
  def initCountT(location:String) = {
    val source = Source.fromFile(location,"UTF-8")
    val lineIterator = source.getLines
    lineIterator.map ( line => {
      val x = parse(line)
      val name = x(0).extract[String]
      val count = x(1).extract[Double]    
      (name,count)
    }).toMap[String,Double]
  }
  
  def initCountV(location:String) = { 
    val source = Source.fromFile(location,"UTF-8")
    val lineIterator = source.getLines
    lineIterator.map( line => {
      val x= parse(line)
      val name = x(0).extract[String]
      val m = x(1).extract[Map[String,Double]]
      (name,m)
    } ).toMap[String,Map[String,Double]] 
  }

  def sumCounT(countT:Map[String,Int]) = {
    countT.map(_._2).reduce(_+_).toDouble
  }
  
  def sumCountV(countV:Map[String,Map[String,Int]]) = {
    countV.map( vm => {
      val v = vm._1
      val count = vm._2.map(_._2).reduce(_+_)
      (v,count.toDouble)
    }).toMap[String,Double]
  }
}