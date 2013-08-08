var MAIN_FRAME_ISCLOSE = true;
var btnJoson ;
var initData = function(){
		ButtonPanel = Ext.extend(Ext.Panel, {
		    layout:'table',
		    defaultType: 'button',
		    baseCls: 'x-plain',
		    cls: 'btn-panel',
		    renderTo : 'top_menu',
		    menu: undefined,
		    split: true,
		    
		    layoutConfig: {
		        columns:6
		    },
	
		    constructor: function(desc, buttons){
		        // apply test configs	
		        for(var i = 0, b; b = buttons[i]; i++){
		            b.menu = this.menu;
		            b.enableToggle = this.enableToggle;
		            b.split = this.split;
		            b.arrowAlign = this.arrowAlign;
		        }
		        var items = [{
		            xtype: 'box',
		            autoEl: {tag: 'h3', html: desc, style:"padding:15px 0 3px;"},
		            colspan: 6
		        }].concat(buttons);
		
		        ButtonPanel.superclass.constructor.call(this, {
		            items: items
		        });
		    }
		});
		
		btnJoson = "[" ;
		btnJoson += "{text: '通 讯 录',iconCls: 'txl',id:'txl', scale: 'large',iconAlign: 'top'}," ;
	
		btnJoson += "{text: '电子邮箱',iconCls: 'email',id:'email', scale: 'large',iconAlign: 'top'}," ;

		
		if(centerId == "1" || centerId == '7' || !centerId) {
			btnJoson += "{text: '修改密码',iconCls: 'unionReport',id:'unionReport', scale: 'large',iconAlign: 'top'}," ;
		}
		
		//btnJoson += "{text: '在线客服',iconCls: 'service',id:'service', scale: 'large',iconAlign: 'top'}," ;
		
		if(bbsUrl) {
			btnJoson += "{text: '登陆论坛',iconCls: 'bbs',id:'bbs', scale: 'large',iconAlign: 'top'}," ;
		}
		
		if(centerId == '6') {
			btnJoson += "{text: '个人信息维护',iconCls: 'task',id:'myInfoEdit', scale: 'large',iconAlign: 'top'}," ;
		}
		
		if(fourCenter) {
			//如果是四个中心这种模式，这里就是切换系统，否则是退出系统
			btnJoson += "{text: '切换系统',iconCls: 'switch',id:'chgSystem', scale: 'large',iconAlign: 'top'}" ;
		}else {
			btnJoson += "{text: '退出系统',iconCls: 'exit',id:'logout', scale: 'large',iconAlign: 'top'}" ;
		}
		
		btnJoson += "]" ;
		
		if(menuType != "下拉浮出") {
			var btnPanel =  new ButtonPanel(
		         '',eval(btnJoson)
	        );   
		}
};

var initEvent = function () {
	
	//在线客服
   // Ext.get('service').on('click',function(){
    //   var bf = document.getElementById("bottomFrame").contentWindow.goQQ();
    //});
	
	  Ext.get('email').on('click',function(){
		  var url = "pccpa_email.jsp";
	       window.open(url) ;
		  //var bf = document.getElementById("bottomFrame").contentWindow.goQQ();
    });
	  
	  Ext.get('txl').on('click',function(){
		  var url = "pccpa_txl.jsp";
	       openTab("txlTab","通讯录",url) ;
		  //var bf = document.getElementById("bottomFrame").contentWindow.goQQ();
    });
    
    if(bbsUrl) {
    	 Ext.get('bbs').on('click',function(){
      		window.open(bbsUrl);
    	});
    }  
    
    if(centerId == "1"  || centerId == '7' || !centerId) { //只有审计作业中心或非四个中心模式时才有底稿编制及选择项目
	    //底稿编制
     	
	   //修改密码
	   Ext.get('unionReport').on('click',function(){
	   
	   var url = "/user.do?method=changePassword";
	       openTab("projectLoginTab","修改密码",url) ;
			//openUR();
	 	});
    }
    
    if(centerId == '6') {
    	Ext.get('myInfoEdit').on('click',function(){
    		 var url = "user.do?method=Edit&UserOpt=3&close=1" ;
  	       	 openTab("myInfoEditTab","个人信息维护",url) ;
	 	});
	}
    
     //切换系统
     if(fourCenter) { 
	    Ext.get('chgSystem').on('click',function(){
	    	
	    	MAIN_FRAME_ISCLOSE = false;
	    	if(isZh4Center == "true") {
	    		window.location = MATECH_SYSTEM_WEB_ROOT+"info.do?method=index" ;
	    	}else {
	        	window.location = MATECH_SYSTEM_WEB_ROOT+"4center.jsp" ;
	    	}
	    });
     }else {
    	//退出系统
	    Ext.get('logout').on('click',function(){
			
			if (window.confirm("是否退出系统？")){
				//不提示退出
				exitSystemAsk=0;
				window.location=MATECH_SYSTEM_WEB_ROOT+"system.do?method=exitSystem";
			}
			
	    });
    }
}
	
		

		
var flow1='<iframe id="flexIframe" scrolling="no" frameborder="0" width="100%" height="100%" src="'+MATECH_SYSTEM_WEB_ROOT+'/flex/basic.jsp"></iframe>' ;
var flow2='<iframe id="flexIframe" scrolling="no" frameborder="0" width="100%" height="100%" src="'+MATECH_SYSTEM_WEB_ROOT+'/flex/projectManager.jsp"></iframe>' ;
var flow3='<iframe id="flexIframe" frameborder="0" width="100%" height="100%" src="'+MATECH_SYSTEM_WEB_ROOT+'/auditPlatform.do?method=goFlexCtrl"></iframe>' ;
var flow4='<iframe id="flexIframe" scrolling="no" frameborder="0" width="100%" height="100%" src="'+MATECH_SYSTEM_WEB_ROOT+'/flex/crm.jsp"></iframe>' ;
var flow6='<iframe id="flexIframe" scrolling="no" frameborder="0" width="100%" height="100%" src="'+MATECH_SYSTEM_WEB_ROOT+'/info.do?method=fileCenter"></iframe>' ;
var flow7='<iframe id="mainIndex"  scrolling="no" frameborder="0" width="100%" height="100%" src="'+MATECH_SYSTEM_WEB_ROOT+'/info.do?method=mainIndex"></iframe>' ;
//等待我办理的工作
var flow8='<iframe id="myDealIndex"  scrolling="no" frameborder="0" width="100%" height="100%" src="'+MATECH_SYSTEM_WEB_ROOT+'/info.do?method=myDealList"></iframe>' ;
//我发起的工资
var flow9='<iframe id="myApplyIndex"  scrolling="no" frameborder="0" width="100%" height="100%" src="'+MATECH_SYSTEM_WEB_ROOT+'/info.do?method=myApplyList"></iframe>' ;

var flow='';
var bShowProject=true;  
var tab ;


var buildLayout = function(){
	   Ext.QuickTips.init();
	   
		var center ;
		  
		 Ext.ux.TabCloseMenu = function(){
		        var tabs, menu, ctxItem;
		        this.init = function(tp){
		            tabs = tp;
		            tabs.on('contextmenu', onContextMenu);
		        }

		        function onContextMenu(ts, item, e){
		            if(!menu){ // create context menu on first right click
		                menu = new Ext.menu.Menu([{
		                    id: tabs.id + '-close',
		                    text: '关闭标签',
		                    iconCls : '1-close-others',
		                    icon:MATECH_SYSTEM_WEB_ROOT+'img/taskState_1.gif',
		                    handler : function(){
		                    	if(ctxItem.id !="flexTab" && ctxItem.id !="knowledgeTab"&&ctxItem.id!="msgTab"){
		                    		tabs.remove(ctxItem.id);
		                    	}
		                    }
		                },{
		                    id: tabs.id + '4-close-others',
		                    text: '关闭其他标签',
		                    iconCls : 'btnno',
		                    icon:MATECH_SYSTEM_WEB_ROOT+'img/taskState_1.gif',
		                    handler : function(){
		                        tabs.items.each(function(item){
			                    	if(item.id !="flexTab" && item.id !="knowledgeTab"&&item.id!="msgTab"){
			                    		if(item != ctxItem){
			                    			tabs.remove(item);
			                    		}
			                    	}
		                        });
		                    }
		                },{
		                    id: tabs.id + '5-close-all',
		                    text: '关闭所有标签',
		                    iconCls : 'btnno',
		                    icon:MATECH_SYSTEM_WEB_ROOT+'img/taskState_1.gif',
		                    handler : function(){
		                        tabs.items.each(function(item){
			                    	if(item.id !="flexTab" && item.id !="knowledgeTab"&&item.id!="msgTab"){
		                               tabs.remove(item.id);
			                    	}
		                        });
		                    }
		                }]);
		            }
		            ctxItem = item;
		            var items = menu.items;
		            //items.get(tabs.id + '-close').setDisabled(!item.closable);
		            var disableOthers = true;
		            tabs.items.each(function(){
		                if(this != item && this.closable){
		                    disableOthers = false;
		                    return false;
		                }
		            });
		            //items.get(tabs.id + '-close-others').setDisabled(disableOthers);
		            menu.showAt(e.getPoint());
		            
		            
		            var disableAll = true;
		             tabs.items.each(function(){
		                 if(this.closable){
		                     disableAll = false;
		                     return false;
		                 }
		            });
		            //items.get(tabs.id + '-close-all').setDisabled(disableAll);
		            menu.showAt(e.getPoint());
		        }
		   };
		
		if(menuType == "下拉浮出") {
			
			tab = new Ext.TabPanel({
				id:'mainFrameTab',
				region:'center',
				renderTo:'tabDiv',
				deferredRender:false,
				activeTab:0,
				resizeTabs:true, // turn on tab resizing
				minTabWidth: 115,
				tabWidth:135,
				height: document.body.clientHeight-Ext.get('tabDiv').getTop()+27,
				enableTabScroll:true
				
			}); 
			
			center = new Ext.Panel({
				region:'center',
				contentEl:'center',
				bodyBorder : false
			});
			
		}else {
			
			tab = new Ext.TabPanel({
				id:'mainFrameTab',
				region:'center',
				deferredRender:false,
				activeTab:0,
				resizeTabs:true, 
				minTabWidth: 115,
				tabWidth:135,
				enableTabScroll:true,
				plugins: new Ext.ux.TabCloseMenu()  
			});
			
			center = tab ;
		}
		
		//tab.getEl().setStyle('z-index','1') ;  
		
		tab.on('beforeremove',function (tab,cmp) {
				
			var iframe = document.getElementById("frame"+cmp.id) ;
			
			if(iframe) {
				iframe.src = "javascript:false" ;
				try {
					CollectGarbage();
				}catch(e){}
			}
			 
		}); 
	   
	   var title ;
	   switch(centerId) {
	    	case '1':
        	  title = "审计作业中心" ;
        	  flow=flow1;
        	  bShowProject=true;
        	  break ;
          	case '2':
        	  title = "项目管理中心" ;
        	  flow=flow2;
        	  bShowProject=false;
        	  break ;
          	case '3':
        	  title = "质量管理中心" ;
        	  flow=flow3;
        	  bShowProject=true;
        	  break ;
          	case '4':
        	  title = "客户管理中心" ;
        	  flow=flow4;
        	  bShowProject=false;
        	  break ;
          	case '6':
          	  title = "档案管理系统" ;
          	  flow=flow6;
          	  bShowProject=false;
          	  break ;
         	case '7':
        	  title = "ERP中心" ;
        	  flow=flow7;
        	  bShowProject=false;
        	  break ;
          	default :
        	  title = "审计作业中心" ;
        	  bShowProject=true;
        	  flow=flow1;
        	  break ;
	   }
	   
	   var layout = new Ext.Viewport({
		layout:'border',
		items:[new Ext.Panel({
				region:'north',
				contentEl: 'north',
				id:'north-panel',
				margins:'0 0 0 0',
				split:true,
				collapsible :true, 
				hideCollapseTool : true,
				cmargins:'0 0 0 0',
		        lines:false,
		        collapseMode:'mini',
				height:69
			}),new Ext.BoxComponent({
				region:'south',
				el: 'south',
				height:20
			}),
			center,{
	    		region:'west',
	    		id:'west-panel',
	    		split:true,
	    		width: 200,
	    		minSize: 175,
	    		maxSize: 400,
	    		margins:'0 0 0 0',
	    		layout:'accordion',
	    		title:title,
	    		lines:false,
	    		collapsible :true,
	    		layoutConfig:{
	    			animate:false
	    		}
	    	}
		 ]
	});
	
	   
	if(menuType == "下拉浮出") {
		var menuHtml = "<div id='wrap-nav'><div class='muen_box'><ul class='menu'>" ;
		
		new Ext.data.Store({
	        proxy:new Ext.data.HttpProxy({url:MATECH_SYSTEM_WEB_ROOT+'extMenu?op=1&centerId='+centerId}),
	        reader:new Ext.data.JsonReader({},['id','title','act']),
	        autoLoad:true
	    }).on('load',function(store,records){
	    	
	        for(var i=0; i<records.length; i++){
	        	
	        	var id = records[i].data.id ;
	        	var title = records[i].data.title ;
	        	var act = records[i].data.act ;
	        	
	        	
	        	menuHtml +="<li class='sprite'>"
	        			 + "<h3>"
	        			 + "<a href='javascript:;' class='depth1'>"
	        			 + "<span>"+title+"</span>"
	        			 + "</a>"
	        			 + "</h3>" ; 
	        	
	        	var childHtml = "" ;
	        	var noChildHtml = "" ;
	        	
	        	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
				var url=MATECH_SYSTEM_WEB_ROOT+'extMenu?op=2&centerId='+centerId+'&menuid='+id ;
				oBao.open("POST",url,false);
				oBao.send();
				var resText = oBao.responseText ;
				
				var childRecords = Ext.util.JSON.decode(resText);
				
				if(childRecords.length == 0) continue ;
				
				menuHtml += "<div class='parent'><div class='children'>"
				
				noChildHtml += "<ul>";  // 没有儿子的二级菜单
	   	    	for (var k=0; k<childRecords.length; k++){
	   	    		
	   	    		childHtml += "<ul>" ; 
	   	    		var id = childRecords[k].id ;
	   	        	var text = childRecords[k].text ;
	   	        	var act = childRecords[k].href ;
	   	        	var children = childRecords[k].children ;
	   	        	var dogid = childRecords[k].dogid ;
	   	        	
	   	        	if(children == null || children.length == 0) {
	   	        		
	   	        		noChildHtml += "<li><h3><a href='#' onclick=openTab('"+id+"','"+text+"','"+act+"','"+dogid+"');><img src='images/menu-parent.gif'><span>"+text+"</span></a></h3></li>" ;
	   	        		
	   	        		if(act.indexOf("matechSpilt=true") > -1) {
   	        				//如果链接包含这个参数 就在下面增加一个分隔符
	   	        			noChildHtml += "<li class=\"x-menu-list-item  x-menu-sep-li\"><h3><span class=\"x-menu-sep\">&nbsp;</span></h3></li>" ;
   	        			}
	   	        	}else {
	   	        		childHtml += "<li class='count'><div><strong>"+text+"</strong></div></li>" ;
	   	        		
	   	        		//遍历下级
	   	        		for(var j=0;j<children.length;j++) {
	   	        			
	   	        			childHtml += "<li><a href='javascript:;' onclick=openTab('"+children[j].id+"','"+children[j].text+"','"+children[j].href+"','"+dogid+"');><img src='images/menu-parent.gif'><span>"+children[j].text+"</span></a></li>" ;
	   	        			
	   	        			if(children[j].href.indexOf("matechSpilt=true") > -1) {
	   	        				//如果链接包含这个参数 就在下面增加一个分隔符
	   	        				childHtml += "<li><span class=\"x-menu-sep\">&nbsp;</span></li>" ;  
	   	        			}
	   	        			
	   	        		}  
	   	        	}  
	   	        	childHtml += "</ul>";
	   	    	} 
	   	    	noChildHtml += "</ul>"; 
	   	    	
	   	    	menuHtml += noChildHtml + childHtml ;
		        menuHtml += "</div></div></li>" ;
	        	
	        }//一级菜单循环完
	        menuHtml += "</ul></div></div>"
	      //  console.log('menuHtml：',menuHtml);    
	        document.getElementById("menu").innerHTML = menuHtml ;
	        
	        $('.depth1').click(function(event) { 
	        	
	        	var $parent = $(this).parent().parent() ;
	        	
	        	event.stopPropagation(); 
	        	$('.children').stop(true,true).hide();
	    		$('.depth1').removeClass('navhover');   
	    		hideIframe() ;
	    		
	        	var $menu = $parent.find('.children') ;
	        	$parent.find('.children').animate({opacity:'0.9',height:'show'},300,function(){
		        	createIframe({top:$menu.offset().top,left:$menu.offset().left,width:$menu.width(),height:$menu.height()})  
	    		});
	        	$parent.find('.depth1').addClass('navhover'); 
	    	}).slice(-3,-1).find('.children').addClass('sleft');
	        
	        $('body').click(function() {
	        	$('.children').stop(true,true).hide();
	    		$('.depth1').removeClass('navhover');
	    		hideIframe() ;
	        });
	        
	        //构造菜单旁边的小图标
	        var btnArr = eval(btnJoson) ;
	        
	        
	        var btnHtml = "<div class='dock' id='btndDock'>" 
	        			+ "	<div class='dock-container'>" ;
	        for(var i=0;i<btnArr.length;i++) {
	        	
	        	var btnText = btnArr[i].text ;
	        	var btnCls = btnArr[i].iconCls ;
	        	var id = btnArr[i].id ; //加上一个字符串避免id重复了
	        	btnHtml += "<a class='dock-item' href='javascript:;' id='" + id + "'>" 
	        			+  "<img src='"+MATECH_SYSTEM_WEB_ROOT+"img/menu/"+btnCls+".png'/><span>"+btnText+"</span></a>" ;
              
	        }
	        btnHtml += "</div></div>" ;
	        document.getElementById("button").innerHTML = btnHtml ;
	         
	        //初始化按钮
	        $('#btndDock').Fisheye(
				{
					maxWidth: 20,
					items: 'a',
					itemsText: 'span', 
					container: '.dock-container',
					itemWidth: 45, 
					proximity: 30, 
					halign : 'right'
				}
			)
			initEvent();
	    });
		
		//隐藏西边和北边
		layout.items.get(3).hide() ;  
		layout.items.get(0).hide() ; 
	//	Ext.getCmp('north-panel').collapse(true) ; //收起北边的logo
		layout.doLayout();
		
	}else if(menuType == "不显示") {
		layout.items.get(3).hide() ;
		layout.doLayout();
		
		initEvent();
	}else {
		new Ext.data.Store({
	        proxy:new Ext.data.HttpProxy({url:MATECH_SYSTEM_WEB_ROOT+'extMenu?op=1&centerId='+centerId}),
	        reader:new Ext.data.JsonReader({},['id','title','act']),
	        autoLoad:true
	    }).on('load',function(store,records){
	        var nav = layout.items.get(3);
	        
	        for (var i=0; i<records.length; i++){
	        	
	            var menu = new Ext.Panel({
	                id:records[i].data.id,
	                title:records[i].data.title,
	                act:records[i].data.act,
	                autoScroll:true,
	                iconCls:'nav'
	            });
	            
	           menu.on('expand',function(menuObj){
	           		var n = tab.getComponent("");    
			            var menuIframe = document.getElementById("flexIframe");
		             	if(menuIframe && menuObj.act != "") {
		             		menuIframe.src = MATECH_SYSTEM_WEB_ROOT + menuObj.act;
		             	}
	           });

	            var root = new Ext.tree.AsyncTreeNode({
	                id:records[i].data.id,
	                text:records[i].data.title
	            });
	            var tree = new Ext.tree.TreePanel({
	                loader:new Ext.tree.TreeLoader({dataUrl:MATECH_SYSTEM_WEB_ROOT+'extMenu?op=3&centerId='+centerId+'&menuid='+records[i].data.id}),
	                root:root,
	                border:false,
	                rootVisible:false
	            });
	            tree.on('click',function(node,event){
	            	 if (node.isLeaf()){
			            event.stopEvent();
			            
			            var activeXMethod = node.attributes.activeXMethod ;
			            //项目管控的菜单特殊对待
			            if (activeXMethod){
			            	openSchedule(node.id);
			            	return;	
			            }
			            
			            //其他中心
			            var n = tab.getComponent(node.id); 
			            var href = node.attributes.href ;
			           
			            var dogid = node.attributes.dogid ;
			            
			            if(dogid) {
			            	if(dogid.indexOf(clientDogSysUi) == -1) {
			            		alert("您无权访问此功能,请联系管理员授权") ;
			            		return ;
			            	}
			            }
			            
			            var scrolling = 'scrolling="auto"' ;
			            if(href.indexOf("http://") == -1) {
			            	href = MATECH_SYSTEM_WEB_ROOT+node.attributes.href ;
			            	//scrolling = 'scrolling="no"' ;
			            }
			           // alert(scrolling);
			            if (!n) { //判断是否已经打开该面板    
			                 n = tab.add({    
			                    'id':node.id,    
			                    'title':node.text,  
			                     closable:true,  //通过html载入目标页    
			                     html:'<iframe id="frame'+ node.id + '" '+scrolling+' frameborder="0" width="100%" height="100%" src="'+href+'"></iframe>'   
			                 });    
			             }    
			             tab.setActiveTab(n); 
	                }else {
	                	node.toggle();
	                }
	            });
	          //  root.expand(true);

	            menu.add(tree);
	            nav.add(menu);
	        }
	        layout.doLayout();
			try{
				var width=screen.width; 
				if(width <= 1024){
					 //1024分辨率下默认收起菜单
					Ext.getCmp('west-panel').collapse(true) ;
				}
			} catch(e){}
	        Ext.getCmp('west-panel').on('collapse',function(){
	          	 window.frames["mainIndex"].location.reload();  
	        }) ;
	         Ext.getCmp('west-panel').on('expand',function(){
	          	 window.frames["mainIndex"].location.reload();  
	        }) ;
	        initEvent();
	    });
	}
	
	var pccpa=window.location.host;
	 
	var isChatSysReload=true; 
	 
    var n = tab.add({    
		id:"flexTab",   
		title:"办公首页",
		activeTab:0,
		closable:false,  //通过html载入目标页    
		listeners:{// 添加监听器，点击此页面的tab时候要重新加载（刷新功能）
             activate:function(){
            	//refreshIndex();
            	 //getUpdater().refresh();
            	 //window.frames["mainIndex"].location.reload();   //点击我的工作台，进行刷新
                  //window.frames["mainIndex"].contentWindow.goSearch();
             }
        },
		html:flow
//		html:'<iframe scrolling="auto" id="mainIndex" frameborder="0" width="100%" height="100%" src="'+MATECH_SYSTEM_WEB_ROOT+'/info.do?method=mainIndex"></iframe>'
	},{    
		id:"msgTab",   
		title:"即时通讯",
		activeTab:0,
		closable:false,  //通过html载入目标页    
		listeners:{
			'show':function(p){
				var chatIframe = Ext.get('myChatSysIndex');
				if(chatIframe!=null){
					if(isChatSysReload){
						chatIframe.dom.src=MATECH_SYSTEM_WEB_ROOT+"/relogin.jsp";
						isChatSysReload=false;
					}
				}
			}
		},
		html:'<iframe id="myChatSysIndex"  scrolling="no" frameborder="0" width="100%" height="100%" src="'+MATECH_SYSTEM_WEB_ROOT+'/relogin.jsp"></iframe>'
	}
    
    /*
    ,{    
		id:"myDealTab",   
		title:"等待我办理的工作",
		activeTab:1,
		closable:false,  //通过html载入目标页    
		listeners:{// 添加监听器，点击此页面的tab时候要重新加载（刷新功能）
             activate:function(){
            	//refreshIndex();
            	 //getUpdater().refresh();
            	 window.frames["myDealIndex"].location.reload();    
             }
        },
		html:flow8
	},{    
		id:"myApplyTab",   
		title:"我发起的工作",
		activeTab:2,
		closable:false,  //通过html载入目标页    
		listeners:{// 添加监听器，点击此页面的tab时候要重新加载（刷新功能）
             activate:function(){
            	//refreshIndex();
            	 //getUpdater().refresh();
            	 window.frames["myApplyIndex"].location.reload();    
             }
        },
		html:flow9
	}
	*/
	);
	tab.setActiveTab(0);
	//rat=setInterval(function(){resetActiveTab(0)},1000);  //延时
};
var rat;
var resetActiveTab = function(i){
    tab.setActiveTab(i);
    clearInterval(rat);
    
    window.frames["mainIndex"].location.reload();
};

var init = function(){
	initData();
    buildLayout();
};


Ext.onReady(init);

function openSchedule(method){ 
	
	var id="_schedule_";
	var n = tab.getComponent(id);    
	if (!n) { //判断是否已经打开该面板    
		n = tab.add({    
			'id':id,    
			'title':'项目排程',  
			 closable:true,  //通过html载入目标页    
			 html:'<iframe scrolling="no" id="_schedule_iframe_" frameborder="0" width="100%" height="100%" src="'+MATECH_SYSTEM_WEB_ROOT+'/schedule/list.jsp?method=' + method + '"></iframe>'   
		});    
		tab.setActiveTab(n);
	} else{
		tab.setActiveTab(n);
		var tt=window.frames['_schedule_iframe_'];
		if (tt){
			try{
				tt.callByOther(method)
			}catch(e){alert('激活窗口方法失败：'+e)};
		}else{
			alert('无法定位窗口');
		}
	}
}

function createIframe(coordinate) {
	
	var myIframe = document.getElementById("myMenuIframe") ;
	
	if(!myIframe){
		 var myIframe = document.createElement('iframe');  
		 myIframe.id='myMenuIframe';  
		 myIframe.style.zIndex='0'; 
		 myIframe.setAttribute('frameborder','0');  
		 myIframe.setAttribute('src','javascript:false;'); 
		// alert(document.getElementById("wrap-nav"));  
		document.getElementById("center-north").appendChild(myIframe);  
		 //document.body.appendChild(myIframe);  
	}
	
    myIframe.style.position='absolute';  
    myIframe.style.top=coordinate.top;  
    myIframe.style.left=coordinate.left;     
    myIframe.style.width=coordinate.width+5;   
    myIframe.style.height=coordinate.height+15;  
    myIframe.style.filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';
    myIframe.style.display = "" ;
   
}

function hideIframe() {
	
	var myIframe = document.getElementById("myMenuIframe") ;
	
	if(myIframe){
		 myIframe.style.display = "none" ;
	}
}  


function exitSystem() {
	if(MAIN_FRAME_ISCLOSE) {
		//killSession();
	}
}
 
function killSession(){
  var oBao = new ActiveXObject("Microsoft.XMLHTTP");
  oBao.open("POST",MATECH_SYSTEM_WEB_ROOT + "common.do?method=exitSystem&random=" + Math.random(),false);
  try {
    oBao.send();
  }catch(ex) {
  }

}

function openTab(id,name,url,dogid) {
	
    if(dogid) {
    	if(dogid.indexOf(clientDogSysUi) == -1) {
    		alert("您无权访问此功能,请联系管理员授权") ;
    		return ;
    	}
    }
	
	 //其他中心
    var n = tab.getComponent(id); 
    
    var scrolling = "scrolling='auto'" ;
    if(url.indexOf("http://") == -1) {
    	url = MATECH_SYSTEM_WEB_ROOT+url ;
    	scrolling = 'scrolling="auto"' ;
    }
    
    if (!n) { //判断是否已经打开该面板    
         n = tab.add({    
            'id':id,      
            'title':name,  
             closable:true,  //通过html载入目标页    
             html:'<iframe id="frame'+ id + '" '+scrolling+' frameborder="0" width="100%" height="100%" src=""></iframe>'   
         }).show();
         document.getElementById("frame"+ id).src = url;
     }    
     tab.setActiveTab(n);    
     
     //隐藏菜单
     try{
 	     $('.children').stop(true,true).hide();
 		 $('.depth1').removeClass('navhover');
 		 hideIframe() ;
     }catch(e){}
     
}

