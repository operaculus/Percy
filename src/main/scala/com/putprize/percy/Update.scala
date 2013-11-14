package com.putprize.percy

import scala.math.{log,abs,exp,pow}

import scala.collection.mutable.{HashMap => HMap}

import org.apache.log4j.Logger

// 0: One
// 1: Stream
class PercyUpdater(m:Model2, N:Int, M:Int) {
  
  val model = m
  
  var count = 0

  var N1 = 0.0
  var N2 = 0.0
  
  def rate = pow((8+count),-0.25)

  
  val _log = Logger.getLogger(this.getClass.getName)
  
  def extractVs(xs:Map[String,Document]) = {
    xs.values.par.flatMap(x => x.vs).toSet.toArray
  }
  
  def run_em(xs:Map[String,Document],U:Int = 1) = {
    
    count += 1
    _log.info("RunEM "+count)
    _log.info("Rate "+rate)
    
    val K = model.K
    
    val Vs = extractVs(xs)
    
    _log.info("Vs Size "+Vs.size)
    
    val countT = new Array[Double](K)
    
    val countV = Vs.map(v => {(v,new Array[Double](K))}).toMap
    _log.info("countV size "+countV.size)
    _log.info("Count Init Done")
    
    // 1.
    _log.info("Inferecne ...")
    val rs = xs.par.map( x => {
      val n = x._1
      val m = x._2
      (n,PercyInfer.run_vt(model,m))
    })
    _log.info("Inference Done")
    
    //var likelihood = 0.0
    //var S = 0.0
    // 2.
    _log.info("Count ... ")
    rs.foreach(r => {
      val n = r._1
      val phi = r._2._2
      // Count
      val doc = xs(n)
      (0 until doc.n).foreach( i => {
        val v = doc.vs(i)
        val c = doc.cs(i)
        (0 until K).foreach(z => {
          countT(z) += c*phi(i)(z)
          countV(v)(z) += c*phi(i)(z)
        })
      })
    })
    _log.info("Count Done")
    
    val n1 = rs.map(r => r._2._3).reduce(_+_)
    
    _log.info("Likelihood For "+count+" "+n1)

    val n2 = xs.par.map( x => {
      x._2.cs.reduce(_+_)
    }).reduce(_+_)
    
    _log.info("Perplexity 1 For "+count+" "+exp(-n1/n2))
    N1 += n1
    N2 += n2
    _log.info("Perplexity 2 For "+count+" "+exp(-N1/N2))
    
   _log.info("Update Model ...")
    (0 until K).par.foreach( z => {
      if (U == 1){
        val c1 = model.getCountT(z)
        //val c2 = countT(z)*N/xs.size.toDouble+M*model.INIT_V_VALUE
        val c2 = countT(z)
        //model.setCountT(z, (1-rate)*c1+rate*c2)
        model.setCountT(z,c1+c2);
      }
      else {
        model.setCountT(z, countT(z));
      }
    })
    
    (0 until M).par.foreach( v => {
      (0 until K).foreach( z => {
        if (U == 1){
          val c1 = model.getCountV(v, z)
          val c2 = 
            if (countV.contains(v)) 
              //countV(v)(z)*N/xs.size.toDouble+model.INIT_V_VALUE 
              countV(v)(z)
            else 
              //model.INIT_V_VALUE
              0.0
            //model.setCountV(v, z, (1-rate)*c1+rate*c2)
          model.setCountV(v, z, c1+c2)
        }
        else {
          model.setCountV(v,z,countV(v)(z))
        }
      })
    })
    
    _log.info("Update Model Done")
    
    exp(-N1/N2)
  }

}
