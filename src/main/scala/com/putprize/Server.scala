package com.putprize

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler,ServletHolder}

import com.putprize.percy.Count
import com.putprize.percy.service.{PercyService,PercyServlet}

import org.apache.log4j.Logger

object PercyServer {
  
  val _log = Logger.getLogger(this.getClass.getName)
	
  def run(LOCATION_COUNT_T:String, LOCATION_COUNT_V:String, port:Int) = {
    
    _log.info("Service Init ...")
    val cT = Count.initCountT(LOCATION_COUNT_T)
    val cV = Count.initCountV(LOCATION_COUNT_V)
    
    val service = new PercyService(cT,cV)
    _log.info("Service Init Done")
    
    val server = new Server(port)
    
    val root = new ServletContextHandler(server,"")
    
    val s = new PercyServlet(service)
    root.addServlet(new ServletHolder(s), "/test")
    
    
    try {
      server.start
    }
    catch {
      case e:Exception => {
        e.printStackTrace
        sys.exit(1)
      }
    }
    
  }
}
