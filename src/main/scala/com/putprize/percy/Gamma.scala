package com.putprize.percy

import scala.math.log

object Gamma {
  
  // 导数 DiGamma Function
  def iGamma(u: Double) = {
    val z = u
    var x = z
    var r = 0.0
    
    while (x <= 5){
      r -= 1/x
      x += 1
    }
    
    var p = 1.0/(x*x)
    
    var t = p *
    	(-0.0833333333333333333333333333333 + p*
    	    (0.00833333333333333333333333333333 + p*
    	        (-0.00396825396825396825 + p*
    	            (0.0041666666666666666666666667 + p*
    	                (-0.00757575757575757575757575757576 + p*
    	                    (0.0210927960928 + p*
    	                        (-0.0833333333333333333333333333333 + p*
    	                            (0.44325980392157))))))))
    r + log(x) - 0.5/x +t    
  }
  
  // 对数 Log Gamma Function
  def logGamma(u: Double) = {
    val z = u
    var x = z+6
    var p = 1/(x*x)
    
    p = (((-0.000595238095238*p + 0.000793650793651)*p - 0.002777777777778)*p + 0.083333333333333)
    p /= x
    p = (x-0.5)*log(x)-x+0.918938533204673+
    	p-log(x-1)-log(x-2)-log(x-3)-log(x-4)-log(x-5)-log(x-6)
    p
  }
  
  // 导数的导数 TriGamma Function
  def tGamma(u: Double) = {
	val z= u
    var x = z+6
    var p = 1/(x*x)
    
    p = (((((0.075757575757576*p-0.033333333333333)*p+0.0238095238095238)*p-0.033333333333333)*p+0.166666666666667)*p+1)/x+0.5*p;
    for (i <- 0 until 6){
      x -= 1
      p = 1/(x*x)+p
    }
    p    
  }

}