<%@page import="com.matech.audit.service.analyse.model.ConditionVO"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

  
     
     
      <fieldset>
         <legend>${vo.caption }</legend>
         <table id="tableResult" class="formTable">
            ${vo.htmlstr }
          </table>
      </fieldset>
     
     