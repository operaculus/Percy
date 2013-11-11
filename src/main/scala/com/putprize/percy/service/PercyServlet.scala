package com.putprize.percy.service

import javax.servlet.http.{HttpServlet,HttpServletRequest,HttpServletResponse}

import net.liftweb.json.JsonAST
import net.liftweb.json.Extraction
import net.liftweb.json.Printer
import net.liftweb.json.parse

import com.putprize.percy.PercyInfer

class PercyServlet(s:PercyService) extends HttpServlet {
  
  private
  val service = s
  
  implicit val FORMATS = net.liftweb.json.DefaultFormats
  
  def process(rq:HttpServletRequest, rp:HttpServletResponse):Unit = {
    
    rp.setContentType("text/html")
    rp.setCharacterEncoding("utf-8")
    rq.setCharacterEncoding("utf-8")
    
    val line = rq.getParameter("text").toString
    
    val text = line.split(",").toArray
    
    val doc = service.parseDocument(text)
    
    val res = PercyInfer.run_vt(service.model, doc)
    
    val zs = service.formatTopic(res._1)
    
    val data = Printer.compact(JsonAST.render(Extraction.decompose(zs)))
    
    rp.getWriter.print(data)
  }

  override 
  def doGet(rq:HttpServletRequest, rp:HttpServletResponse):Unit = {
    process(rq,rp)
  }
  
  override 
  def doPost(rq:HttpServletRequest, rp:HttpServletResponse):Unit = {
    process(rq,rp)
  }  
  
}
