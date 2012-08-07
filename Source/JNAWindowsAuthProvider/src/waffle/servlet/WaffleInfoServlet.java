/*******************************************************************************
* Waffle (http://waffle.codeplex.com)
* 
* Copyright (c) 2010 Application Security, Inc.
* 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Application Security, Inc.
*******************************************************************************/
package waffle.servlet;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import waffle.util.WaffleInfo;

public class WaffleInfoServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException 
  {
    WaffleInfo info = new WaffleInfo();
    try {
      Document doc = info.getSystemInfo();
      Element root = doc.getDocumentElement();
      
      // Add the Request Information Here
      Element http = getRequestInfo(doc,request);
      root.insertBefore(http, root.getFirstChild());
      
      //Write the XML Response
      TransformerFactory transfac = TransformerFactory.newInstance();
      Transformer trans = transfac.newTransformer();
      trans.setOutputProperty(OutputKeys.INDENT, "yes");

      StreamResult result = new StreamResult(response.getWriter());
      DOMSource source = new DOMSource(doc);
      trans.transform(source, result);
      response.setContentType("application/xml");
    } 
    catch (Exception e) {
      throw new ServletException(e);
    }
  }

  /**
   * Delegate POST to GET
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException 
   {
    doGet(request,response);
  }
  
  public Element getRequestInfo(Document doc, HttpServletRequest request) {
    Element node = doc.createElement("request");
    Principal p = request.getUserPrincipal();
    if(p!=null) {
      Element child = doc.createElement("principal");
      child.setAttribute("class", p.getClass().getName());

      Element value = doc.createElement("name");
      value.setTextContent(p.getName());
      child.appendChild(value);
      
      value = doc.createElement("string");
      value.setTextContent(p.toString());
      child.appendChild(value);
      
      node.appendChild(child);
    }
    
    Enumeration<String> headers = request.getHeaderNames();
    if(headers.hasMoreElements()) {
      Element child = doc.createElement("headers");
      while(headers.hasMoreElements()) {
        String name = headers.nextElement();
        
        Element value = doc.createElement(name);
        value.setTextContent(request.getHeader(name));
        child.appendChild(value);
      }
      node.appendChild(child);
    }   
    return node;
  }
}

