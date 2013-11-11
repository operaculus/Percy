package com.putprize.percy

import org.apache.log4j.Logger

import scala.collection.mutable.ArrayBuffer

import scala.io.Source

object PercyStream {
  
  val _log = Logger.getLogger(this.getClass.getName)
  
  // Mode: stream
  def run(
		  locationSize:String, 
		  locationNs:Array[String],
		  locationCountT:String, 
		  locationCountV:String,
		  ROOT1:String,
		  ROOT2:String,
		  K:Int,
		  Bsize:Int,
		  Nsave:Int,
		  I:Int) = {
    
    _log.info("Begin ...")
    val s = Data.initSize(locationSize)
    val N = s._1
    val M = s._2
    
    val model = PercyModel.initModel(K, M)
    
    _log.info("Init Model Done")
    
    val U = new PercyUpdater(model,N,M)
    
    _log.info("Init Updater Done")
 
    (0 until I).foreach { i =>
      _log.info(i)
    
    for (location <- locationNs){
      val xs = Data.initData(ROOT1+"/"+location)      
      _log.info("Got Data "+xs.size)
      val n = xs.size/Bsize+1
      for (i <- 0 until n){
        val i1 = if (i*Bsize < xs.size) i*Bsize else xs.size
        val i2 = if (i1+Bsize < xs.size) i1+Bsize else xs.size
        val Xs = xs.slice(i1, i2).toMap
        _log.info("Run "+i+" "+Xs.size)
        if (Xs.size > 0)
          U.run_em(Xs)
        if (U.count % Nsave == 0){
    	    PercyModel.saveCountT(model.countT, ROOT2+"/"+locationCountT+"_"+U.count)
    	    PercyModel.saveCountV(model.countT, model.countV, ROOT2+"/"+locationCountV+"_"+U.count)        
        }
      }
    }
      PercyModel.saveCountT(model.countT, ROOT2+"/"+locationCountT+"_Done_"+i)
      PercyModel.saveCountV(model.countT, model.countV, ROOT2+"/"+locationCountV+"_Done_"+i)
    }
    
    PercyModel.saveCountT(model.countT,ROOT2+"/"+locationCountT)
    PercyModel.saveCountV(model.countT, model.countV,ROOT2+"/"+locationCountV)
  }
  
  // Mode: one
  def run(locationSize:String,
          locationData:String,
          locationCountT:String,
          locationCountV:String,
          ROOT:String,
          K:Int,
          I:Int) = {
    
    _log.info("Begin ...")
    val s = Data.initSize(locationSize)
    val N = s._1
    val M = s._2
    
    val model = PercyModel.initModel(K,M)
    
    _log.info("Init Model Done")
    
    val U = new PercyUpdater(model, N, M)
    
    _log.info("Init Updaer Done")
    
    (0 until I).foreach { i =>
      _log.info(i)
      
      val Xs = Data.initData(locationData).toMap
      _log.info("Got Data "+Xs.size)
      U.run_em(Xs)
      
      if (i % 20 == 0){
        PercyModel.saveCountT(model.countT, ROOT+"/"+locationCountT+"_Done_"+i)
        PercyModel.saveCountV(model.countT, model.countV, ROOT+"/"+locationCountV+"_Done_"+i)
      }
      
    }
    
    PercyModel.saveCountT(model.countT,ROOT+"/"+locationCountT)
    PercyModel.saveCountV(model.countT,model.countV,ROOT+"/"+locationCountV)
    
  }

}
