package com.putprize.percy

import scala.math.{log,abs,exp}

import org.apache.log4j.Logger

class Dirichlet {
  
  val _log = Logger.getLogger(this.getClass.getName)
  
  // log p(D|p) 
  private
  def pLikelihood(p:Double, s:Double, N:Int, K:Int) = { 
    N*(Gamma.logGamma(K*p)-K*Gamma.logGamma(p))+(p-1)*s
  }
  
  private
  def p1Likelihood(p:Double, s:Double, N:Int, K:Int) = {
    N*(K*Gamma.iGamma(K*p)-K*Gamma.iGamma(p))+s
  }
  
  private
  def p2Likelihood(p:Double, N:Int, K:Int) = {
    N*(K*K*Gamma.tGamma(K*p)-K*Gamma.tGamma(p))
  }
  
  def optimize(s: Double, K: Int, N: Int) = {
    _log.info("= "+s)
    val NEWTON_THRESH = 1e-5
    val MAX_ITER = 1000
    
    var init_p = 100.0
    
    var log_p = log(init_p)
    
    var x = 1.0
    var iter = 0
    
    while ( (abs(x) > NEWTON_THRESH) && (iter < MAX_ITER) ) {
      iter += 1;
      
      var p = exp(log_p)
      
      val z  = pLikelihood(p,s,N,K)
      
      
      val z1 = p1Likelihood(p,s,N,K)
      val z2 = p2Likelihood(p,N,K)
      log_p = log_p - z1/(z2*p+z1)
      _log.info("P maximization :" + z + " "+z1)
      x = z1
    }
    
    exp(log_p)
    
  }  

}