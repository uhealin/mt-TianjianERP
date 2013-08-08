Ext.namespace("Ext.matech.jpdl");  
var attrWin ;
var init = function() {
    Ext.QuickTips.init();

    var attrDiv = document.getElementById("attrDiv") ;
    attrDiv.style.display = "" ;
    var jpdl = createJpdl();
    
    attrPanel = new Ext.Panel({
		region:'west',
		id:'west-panel',
		split:true,
		width: 280,
		margins:'0 0 0 0',
		layout:'accordion',
		autoScroll:true,
		title:"属性面板",
		lines:false,
		collapsible :true,
		contentEl:'attrDiv', 
		bodyStyle : 'background:#dfe8f6',
		layoutConfig:{
			animate:false
		},
		buttons:[
			      /*       
			      {
		            text:'测试xml还原图', 
		          	handler:function() {
		          	 
		          	 var xml = "<?xml version='1.0' encoding='UTF-8'?>"	
		          		 	 + "<process name='test' key='testKey' xmlns='http://jbpm.org/4.0/jpdl'>"
		          		 	 + "<start name='start1' g='654,39,48,48'>"
		          		 	 + "	 <transition name='to task1' to='task1'/>"
		          		 	 + " </start>"
		          		 	 + "<task name='task1' g='630,100,80,40'>"
		          		 	 + " <assignment-handler class='com.matech.audit.service.process.assignImpl.TaskAuditSyAssign'> "
		          		 	 + " 	<field name='department'><string value='12'/></field> "
		          		 	 + " 	<field name='role'><string value='12'/></field>  "
		          		 	 + " 	<field name='user'><string value='12'/></field> "
		          		 	 + "  </assignment-handler>  "
		          		 	 + " <transition name='to task2' to='task2'/>"
		          		 	 + "</task> "
		          		 	 + " <task name='task2' g='629,175,80,40' candidate-users='${afsdf}' >"
		          		 	 + " <transition name='to end1' to='end1'/>"
		          		 	 + " </task>"
		          		 	 + " <end name='end1' g='656,260,48,48'/>"
		          		 	 + "</process>"
		          		
			          this.parse = new Jpdl.xml.JpdlParse(xml) ;
			          this.parse.parse(Jpdl.model) ;
		          		
		          	}
		        },
		         */
		        {
		            text:'保存属性',
		          	handler:function() {
		          		
		          		
		          		var nodeName = document.getElementById("nodeName").value ;
		          		var form = document.getElementById("form").value ;
		          		var handlerClass = document.getElementById("handlerClass").value ;
		          		var rightType = document.getElementById("rightType").value ;
		          		var decisionType = document.getElementById("decisionType").value ;
		          		var decisionExp = document.getElementById("decisionExp").value ;
		          		var decisionClass = document.getElementById("decisionClass").value ;
		          		var subProcessKey = document.getElementById("sub-process-key").value ;
		          		
		          		//信息提醒
		          		var sletter = document.getElementById("sletter") ;
		          		var sMsg = document.getElementById("sMsg") ;
		          		var dLetter = document.getElementById("dLetter") ;
		          		var dMsg = document.getElementById("dMsg") ;
		          		
		          		var selectUserChecked = document.getElementById("isSelectUser").checked ;
		          		
		          		
		          		//获得当前选中节点
		          		var select = Jpdl.model.selections[0]; 
		          		select.name = nodeName ; 
		          		if(select.nodeName != "decision") {
		          			Jpdl.cmd.CommandService.execute(new Jpdl.cmd.UpdateNodeNameCmd(select,nodeName));
		          		}
		          		select.form = form ; 
		          		select.handlerClass = handlerClass ; 
		          		select.rightType = rightType ; 
		          		select.decisionExp = decisionExp ;
		          		select.decisionClass = decisionClass ;
		          		select.subProcessKey = subProcessKey ;
		          		select.decisionType = decisionType ; 
		          		
		          		clearOther() ;
		          		var department = document.getElementById("department").value ;
		          		var role = document.getElementById("role").value ;
		          		var user = document.getElementById("user").value ;
		          		var candidateExp = document.getElementById("candidateExp").value ;
		          		var assignee = document.getElementById("assignee").value ;
		          		var rightClass = document.getElementById("rightClass").value ;
		          		
		          		select.department = department ;
		          		select.role = role ;
		          		select.user = user ; 
		          		select.candidateExp = candidateExp ; 
		          		select.assignee = assignee ; 
		          		select.rightClass = rightClass ; 
		          		//attrWin.hide();
		          		
		          		if(selectUserChecked){
		          			select.selectUser = "是" ;
		          		}else {
		          			select.selectUser = "否" ;
		          		}
		          		
		          		//保存信息提醒设置
		          		select.sletter = sletter.checked ;
		          		select.sMsg = sMsg.checked ;
		          		select.dLetter = dLetter.checked ;
		          		select.dMsg = dMsg.checked ;
		          	}
		        }]
	}) ;
    
    
    var viewPort = new Ext.Viewport({
        layout: 'border',
        items: [attrPanel,jpdl]
    });

} ;

var createJpdl = function() {
    Jpdl.ActivityMap.activityBasePath = MATECH_SYSTEM_WEB_ROOT + '/images/activities/48/';

    var p = new Ext.Panel({
        id: 'jpdl',
        region:'center',
        tbar: new Ext.Toolbar([
            {
                text: '暂存',
                icon:MATECH_SYSTEM_WEB_ROOT + '/img/save.gif',
                handler: function() {
                	save("");
                }
            },'-',
            {
                text: '保存并发布',
                icon:MATECH_SYSTEM_WEB_ROOT + '/img/start.png',
                handler: function() {
                	save(true) ;
                }
            },'-',{
                text: '关闭',
                icon:MATECH_SYSTEM_WEB_ROOT + '/img/close.gif',
                handler: function() {
                	closeTab(parent.mainTab); 
                }
            },'->'
        ]),
        layout: 'border',
        items: [{
            region: 'center',
            xtype: 'jpdlpanel'
        }]
    });
    return p;
};

Ext.onReady(init);
