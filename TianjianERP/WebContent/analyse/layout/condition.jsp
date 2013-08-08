<%@page import="net.sf.json.JSONObject"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

    <% JSONArray jarrQuerys=(JSONArray)request.getAttribute("jarrQuerys"); %>
   <table class="formTable" style="width: 85%">
        <thead>
          <tr>
           <th colspan="6"><input id="cond_caption" value="${vo.caption }" />&nbsp;
              <button onclick="
              if($('#tbodyCondition tr').size()<1){alert('请至少添加一个条件');return;}
              try{ var url='query2d.do';
                var jarr=[];
                $('#tbodyCondition tr').each(function(i){
                  var cond_logic=$(this).find('input[name=cond_logic]').val();
                 var cond_oper=$(this).find('input[name=cond_oper]').val();
                  var cond_column=$(this).find('input[name=cond_column]').val();
                  var cond_val=$(this).find('input[name=cond_val]').val();
                  jarr.push({cond_logic:cond_logic,cond_oper:cond_oper,cond_column:cond_column,cond_val:cond_val});
                });
                var jsonstr=Ext.util.JSON.encode(jarr);
                var htmlstr=$('#tbodyCondition').html();
                var caption=$('#cond_caption').val();
                var uuid='${vo.uuid }';
                var formid='${vo.formid }';
                var param={method:'doSaveCondition',jsonstr:jsonstr,htmlstr:htmlstr
               ,caption:caption,uuid:uuid,formid:formid
                };
               $.post(url,param,function(text){alert(text);treeConRoot.reload();});
                }catch(ex){alert(ex);}
              " >保存设计</button> &nbsp;
              <button id="btnSelectCon" onclick="var url='query2d.do';
                  var method;
                  
                  if(cr=='row'){method='doSaveRow';}else if(cr=='col'){method='doSaveCol';}
                  var conid='${vo.uuid }';
                  var tableid=$('#tableid').val();
                  var uuid=selectCrUUid||'';
                  
                  var param={method:method,conid:conid,tableid:tableid,uuid:uuid};
                  $.post(url,param,function(text){alert(text);reloadTable();})
              "

              ></button></th>
          </tr>
         
          <tr>
          <th>
           <a onclick="var tr=$('<tr></tr>');
           tr.addClass('trCond');
           var r=parseInt(Math.random()*10000+<%=jarrQuerys.size() %>);
           var td_logic=$('<td></td>');
           var input_logic=$('<input  />');
           input_logic.attr('name','cond_logic').attr('id','cond_logic_'+r).attr('autoid','700|query_logic')
           .attr('refer','query_logic').attr('noinput','true').val('and');
           ;
           td_logic.append(input_logic);
           
           var td_column=$('<td></td>');
           var input_column=$('<input  />');
           input_column.attr('name','cond_column').attr('id','cond_column_'+r).attr('autoid','5012|${formVo.TABLENAME }')
           .attr('refer','${formVo.TABLENAME }').attr('noinput','true');
           ;
  
           td_column.append(input_column);
           
           var td_oper=$('<td></td>');
           var input_oper=$('<input  />');
           input_oper.attr('name','cond_oper').attr('id','cond_oper_'+r).attr('autoid','700|query_operator')
           .attr('refer','query_operator').attr('noinput','true').val('like');
           ;
  
           td_oper.append(input_oper);
  
           var td_val=$('<td></td>');
           var input_val=$('<input  />');
            input_val.attr('name','cond_val');
           td_val.append(input_val);
           
           var a=$('<a></a>').append('<img src=img/delete.gif>');
           a.click(function(){$(this).parent().parent().remove();});
           tr.append($('<td></td>').append(a));
           tr.append(td_logic); 
           tr.append(td_column);
           tr.append(td_oper);
           tr.append(td_val);
           $('#tbodyCondition').append(tr);
           initCombox('cond_logic_'+r);
           initCombox('cond_column_'+r);
           initCombox('cond_oper_'+r);
           
           "; ><img src="/AuditSystem/img/add.gif" /></a>
          </th>
          <th>逻辑</th>
          <th>字段名</th>
          <th>条件</th>
          <th>值</th>
          
          </tr>
 
          </thead>
        <tbody id="tbodyCondition">
           <% for(int i=0;i<jarrQuerys.size();i++){ 
             JSONObject json=jarrQuerys.getJSONObject(i);
             if(json==null||!json.containsKey("cond_logic"))continue;
           %>
          <tr >
           <td><a onclick="$(this).parent().parent().remove()"><img src="img/delete.gif"></a></td>
           <td><input id="cond_logic_<%=i %>" value="<%=json.get("cond_logic") %>" name="cond_logic" noinput='true' autoid='700|query_logic' refer='query_logic'  /></td>
           <td><input id="cond_column_<%=i %>" value="<%=json.get("cond_column") %>" name="cond_column" noinput='true' autoid='5012|${formVo.TABLENAME }' refer='${formVo.TABLENAME }'  /></td>
           <td><input id="cond_oper_<%=i %>" value="<%=json.get("cond_oper") %>" name="cond_oper" noinput='true' autoid='700|query_operator' refer='query_operator'  /></td>
           <td><input name="cond_val" value="<%=json.get("cond_val") %>"  /></td>
          </tr>
           <%} %>
        </tbody>
        
   </table>