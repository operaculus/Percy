package com.putprize.percy

import scala.math.{log,exp,abs}

object PercyInfer {
  
  val MAX_ITERATION = 50
  val MIN_CONVERGENCE = 0.001
  
  private
  def sumLog(x1:Double, x2:Double) = {
    if (x1 < x2)
      x2 + log(1+exp(x1-x2))
    else
      x1 + log(1+exp(x2-x1))
  }
  
  def run_vt(model:Model, doc:Document) = {
    
    val K = model.K
    
    val gamma = new Array[Double](K)
    val iamma = new Array[Double](K)
    
    //Init PHI
    val phi = new Array[Array[Double]](doc.n)
    (0 until doc.n).foreach( i => {
      phi(i) = new Array[Double](model.K)
    })
    (0 until model.K).foreach( z => {
      (0 until doc.n).foreach( i => {
        phi(i)(z) = 1.0/K.toDouble
      })
      gamma(z) = model.INIT_T_VALUE + (doc.n/K.toDouble)
      iamma(z) = Gamma.iGamma(gamma(z))
    })
    
    var iter = 0
    var convergence = 1.0
    var likelihood = -10000.0
    while ( iter < MAX_ITERATION && convergence > MIN_CONVERGENCE) {
      iter += 1
      // PHI
      (0 until doc.n).par.foreach ( i => { // !!!!!
        val v = doc.vs(i)
        val c = doc.cs(i)
        // Update PHI
        (0 until K).foreach( z => {
          phi(i)(z) = iamma(z) + model.logPvzBeta(v, z)
        })
        // Normalize PHI
        val sum = (0 until K).map(z => phi(i)(z)).reduce(sumLog)
        (0 until K).foreach(z => {
          phi(i)(z) = exp(phi(i)(z)-sum)
        })
      })
      
      // GAMMA
      (0 until K).par.foreach(z => { // !!!!!
        gamma(z) = model.INIT_T_VALUE +
        (0 until doc.n).map( i => {
          //val c = doc.cs(i)
          //gamma(z) += c*phi(i)(z)
          doc.cs(i)*phi(i)(z)
        }).reduce(_+_)
        iamma(z) = Gamma.iGamma(gamma(z))
      })
      
      // Likelihood
      val t = computeLikelihood(model,doc,phi,gamma)
      convergence = abs((likelihood-t)/likelihood)
      likelihood = t
    }
    
    (gamma,phi,likelihood)
  }
  
  def computeLikelihood(model:Model,doc:Document, 
		  				phi:Array[Array[Double]],
		  				gamma:Array[Double]) = {
    val iamma = gamma.par.map(v => Gamma.iGamma(v)) // !!!!
    val sumGamma = gamma.par.reduce(_+_) // !!!!
    val iSumGamma = Gamma.iGamma(sumGamma)
    
    var likelihood = Gamma.logGamma(model.INIT_T_VALUE*model.K)-model.K*Gamma.logGamma(model.INIT_T_VALUE)
    likelihood -= Gamma.logGamma(sumGamma)    
    val s = (0 until model.K).par.map( k => { // !!!!
      //_log.info("k "+k)
      val s1 = 
        if (gamma(k) > 0) 
          (model.INIT_T_VALUE-1)*(iamma(k)-iSumGamma) 
        else 
          0.0
      val s2 = 
        if (gamma(k) > 0 ) 
          Gamma.logGamma(gamma(k)) - (gamma(k)-1)*(iamma(k)-iSumGamma)
        else
          0.0
        
      val s3 = (0 until doc.n).map( i => {
        if (phi(i)(k) > 0.0){ // 这里很关键
          doc.cs(i)*phi(i)(k)*
    			( (iamma(k)-iSumGamma)-log(phi(i)(k)) +model.logPvzBeta(doc.vs(i), k) ) 
        }
        else {
          0.0
        }
      } ).reduce(_+_)
    
      s1+s2+s3
    })
    
    likelihood+s.reduce(_+_)
  } 

}
