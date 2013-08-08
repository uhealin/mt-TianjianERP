Ext.namespace("Ext.matech.jpdl");

var defaultClass = "com.matech.audit.work.process.Assignment" ;
var defaultHandlerClass = "com.matech.audit.service.process.impl.base.BaseNodeHandler" ;

 
Jpdl={
	svgns:"http://www.w3.org/2000/svg",
	linkns:"http://www.w3.org/1999/xlink",
	vmlns:"urn:schemas-microsoft-com:vml",
	officens:"urn:schemas-microsoft-com:office:office",
	
	installVml:function () {
		if(Jpdl.isVml) {
			document.attachEvent("onreadystatechange",function () {
				var $=document;
				if($.readyState=="complete") {
					if(!$.namespaces["v"])$.namespaces.add("v",Jpdl.vmlns);
					if(!$.namespaces["o"])$.namespaces.add("o",Jpdl.officens) ;
				}
			});
			var $=document.createStyleSheet();
			$.cssText="v\\:*{behavior:url(#default#VML)}"+"o\\:*{behavior:url(#default#VML)}" ;
		}
	},
	
	seed:0,id:function () {
		return "_jpdl_"+this.seed++ ;
	},
	
	onReady:function ($) {
		window.onload=function () {
			$() ;
		} ;
	},
	
	debug:function () {
		if(!Jpdl.debugDiv) {
			Jpdl.debugDiv=document.createElement("textarea");
			Jpdl.debugDiv.rows=10;
			Jpdl.debugDiv.cols=40;
			document.body.appendChild(Jpdl.debugDiv) ;
		}var _="";
		for(var $=0;$<arguments.length;$++)_+=","+arguments[$];Jpdl.debugDiv.value+="\n"+_ ;
	},
	
	getInt:function (_) {
		_+="";
		_=_.replace(/px/,"");
		var $=parseInt(_,10);
		return isNaN($)?0:$ ;
	},
	
	extend:function () {
		var _=function ($) {
			for(var _ in $)this[_]=$[_] ;
		},$=Object.prototype.constructor ;return function (F,C,A) {
			if(typeof(C)=="object") {
				A=C;
				C=F;
				F=A.constructor !=$?A.constructor :function () {
					C.apply(this,arguments) ;
				} ;
			}var D=function () {
			},E,B=C.prototype;
			D.prototype=B;
			E=F.prototype=new D();
			E.constructor =F;
			F.superclass=B;
			if(B.constructor ==$)B.constructor =C;
			E.override=_;
			Jpdl.override(F,A);
			return F ;
		} ;
	}(),
	
	override:function (A,$) {
		if($) {
			var _=A.prototype;
			for(var B in $)_[B]=$[B];if(Jpdl.isIE&&$.toString !=A.toString )_.toString =$.toString ;
		}
	},
	
	apply:function ($,_,B) {
		if(B)Jpdl.apply($,B);
		if($&&_&&typeof(_)=="object")for(var A in _)$[A]=_[A];return $ ;
	},
	
	applyIf:function ($,_) {
		if($&&_)for(var A in _)if(typeof($[A])=="undefined")$[A]=_[A];return $ ;
	},
	
	join:function (_) {
		var A="";
		for(var $=0;$<_.length;$++)A+=_[$];return A ;
	}
};


(function () {
	var C=navigator.userAgent.toLowerCase(),$=C.indexOf("opera")>-1,G=(/webkit|khtml/).test(C),E=!$&&C.indexOf("msie")>-1,_=!$&&C.indexOf("msie 7")>-1,D=!$&&C.indexOf("msie 8")>-1,F=!G&&C.indexOf("gecko")>-1,B=E||_||D,A=!B;
	Jpdl.isSafari=G;
	Jpdl.isIE=E;
	Jpdl.isIE7=_;
	Jpdl.isGecko=F;
	Jpdl.isVml=B;
	Jpdl.isSvg=A;
	if(B)Jpdl.installVml();
	Jpdl.applyIf(Array.prototype,{
		indexOf:function (_) {
			for(var A=0,$=this.length;A<$;A++)if(this[A]===_)return A;return -1 ;
		},
		
		remove:function (_) {
			var $=this.indexOf(_);
			if($!=-1)this.splice($,1);
			return this ;
		}
	});
	String.prototype.trim=function () {
		var $=/^\s+|\s+$/g;
		return function () {
			return this.replace($,"") ;
		} ;
	}() ;
})();
Jpdl.cmd={
};
Jpdl.listeners={
};
Jpdl.xml={
};
Jpdl.geom={
};
Jpdl.geom.Point=function (_,$) {
	this.x=_;
	this.y=$ ;
};
Jpdl.geom.Line=function (A,B,$,_) {
	this.x1=A;
	this.y1=B;
	this.x2=$;
	this.y2=_ ;
};
Jpdl.geom.Line.prototype.getX1=function () {
	return this.x1 ;
};
Jpdl.geom.Line.prototype.getX2=function () {
	return this.x2 ;
};
Jpdl.geom.Line.prototype.getY1=function () {
	return this.y1 ;
};
Jpdl.geom.Line.prototype.getY2=function () {
	return this.y2 ;
};
Jpdl.geom.Line.prototype.getK=function () {
	return (this.y2-this.y1)/(this.x2-this.x1) ;
};
Jpdl.geom.Line.prototype.getD=function () {
	return this.y1-this.getK()*this.x1 ;
};
Jpdl.geom.Line.prototype.isParallel=function (A) {
	var _=this.x1,$=this.x2;
	if((Math.abs(_-$)<0.01)&&(Math.abs(A.getX1()-A.getX2())<0.01))return true;
	else if((Math.abs(_-$)<0.01)&&(Math.abs(A.getX1()-A.getX2())>0.01))return false;
	else if((Math.abs(_-$)>0.01)&&(Math.abs(A.getX1()-A.getX2())<0.01))return false;
	else return Math.abs(this.getK()-A.getK())<0.01 ;
};
Jpdl.geom.Line.prototype.isSameLine=function (A) {
	if(this.isParallel(A)) {
		var _=A.getK(),$=A.getD();
		if(Math.abs(this.x1*_+$-this.y1)<0.01)return true;
		else return false ;
	}else return false ;
};
Jpdl.geom.Line.prototype.contains=function (D) {
	var B=this.x1,F=this.y1,$=this.x2,A=this.y2,H=D.x,C=D.y,G=(B-$)*(B-$)+(F-A)*(F-A),E=(H-B)*(H-B)+(C-F)*(C-F),_=(H-$)*(H-$)+(C-A)*(C-A);
	return G>E&&G>_ ;
};
Jpdl.geom.Line.prototype.getCrossPoint=function (B) {
	if(this.isParallel(B))return null;
	var F,A;
	if(Math.abs(this.x1-this.x2)<0.01) {
		F=this.x1;
		A=B.getK()*F+B.getD() ;
	}else if(Math.abs(B.getX1()-B.getX2())<0.01) {
		F=B.getX1();
		A=this.getD() ;
	}else {
		var _=this.getK(),E=B.getK(),$=this.getD(),D=B.getD();
		F=(D-$)/(_-E);
		A=_*F+$ ;
	}var C=new Jpdl.geom.Point(F,A);
	if(B.contains(C)&&this.contains(C))return C;
	else return null ;
};
Jpdl.geom.Rect=function (B,A,$,_) {
	this.x=B;
	this.y=A;
	this.w=$;
	this.h=_ ;
};
Jpdl.geom.Rect.prototype.getCrossPoint=function (C) {
	var D=null,_=new Jpdl.geom.Line(this.x,this.y,this.x+this.w,this.y);
	D=_.getCrossPoint(C);
	if(D!=null)return D;
	var B=new Jpdl.geom.Line(this.x,this.y+this.h,this.x+this.w,this.y+this.h);
	D=B.getCrossPoint(C);
	if(D!=null)return D;
	var A=new Jpdl.geom.Line(this.x,this.y,this.x,this.y+this.h);
	D=A.getCrossPoint(C);
	if(D!=null)return D;
	var $=new Jpdl.geom.Line(this.x+this.w,this.y,this.x+this.w,this.y+this.h);
	D=$.getCrossPoint(C);
	return D ;
};
Jpdl.cmd.AddEdgeCmd=function (_,$,showName) {
	this.startNode=_;
	this.endNode=$ ;
	this.showName = showName ;
};
Jpdl.cmd.AddEdgeCmd.prototype.execute=function ($) {
	if(this.endNode&&this.endNode!=this.startNode) {
		this.edge=new Jpdl.Edge(this.startNode,this.endNode,this.showName);
		$.add(this.edge) ;
	}
};
Jpdl.cmd.AddNodeCmd=function (_,$) {
	this.x=_;
	this.y=$ ;
};
Jpdl.cmd.AddNodeCmd.prototype.execute=function (_) {
	
	if(_.isInside(this.x,this.y)) {
		var $=Jpdl.ActivityMap.getConfig(_.activePalette);
		$.x=this.x-_.tempRect.w/2;
		$.y=this.y-_.tempRect.h/2;
		if($.url==null)_.add(new Jpdl.Node($));
		else _.add(new Jpdl.ImageNode($)) ;
	}
	
};

Jpdl.cmd.EditNodeCmd=function (editNode) {
	this.editNode = editNode ;
};

Jpdl.cmd.EditNodeCmd.prototype.execute=function (_) {
	
	if(this.editNode.url==null)
		_.add(new Jpdl.Node(this.editNode));
	else
		_.add(new Jpdl.ImageNode(this.editNode)) ;
	
};

Jpdl.cmd.CommandService={
	commands:[],
	undoCommands:[],
	
	execute:function ($) {
			$.execute(Jpdl.model);  
			this.commands.push($);
		
	},
	
	clear:function () {
		this.commands=[]; 
		this.undoCommands=[];
	}
	
};
Jpdl.cmd.LinkTransitionEndCmd=function ($,_) {
	this.transition=$;
	this.newNode=_;
};
Jpdl.cmd.LinkTransitionEndCmd.prototype.execute=function ($) {
	this.oldNode=this.transition.to;
	this.oldNode.incomes.remove(this.transition);
	this.transition.to=this.newNode;
	this.newNode.incomes.push(this.transition);
};
Jpdl.cmd.LinkTransitionStartCmd=function ($,_) {
	this.transition=$;
	this.newNode=_;
};
Jpdl.cmd.LinkTransitionStartCmd.prototype.execute=function ($) {
	this.oldNode=this.transition.from;
	this.oldNode.outputs.remove(this.transition);
	this.transition.from=this.newNode;
	this.newNode.outputs.push(this.transition);
};
Jpdl.cmd.MoveNodeCmd=function (A,B,$,_) {
	this.x1=A;
	this.y1=B;
	this.x2=$;
	this.y2=_ ;
};
Jpdl.cmd.MoveNodeCmd.prototype.execute=function (_) {
	if(_.isInside(this.x2,this.y2)) {
		var A=this.x2-this.x1,C=this.y2-this.y1;
		for(var $=0;$<_.selections.length;$++) {
			var B=_.selections[$];
			B.moveEnd(A,C);
		}
	}
};
Jpdl.cmd.RemoveSelectionsCmd=function (_,$) {
	this.startNode=_;
	this.endNode=$;
};
Jpdl.cmd.RemoveSelectionsCmd.prototype.execute=function ($) {
	var A=$.selections;
	for(i=A.length-1;i>=0;i--) {
		var _=A[i];
		_.remove();
		delete $.nodes[_.getId()];
	}$.selections=[];
};
Jpdl.cmd.ResizeNodeCmd=function ($,_,A) {
	this.direction=$;
	this.dx=_;
	this.dy=A;
};
Jpdl.cmd.ResizeNodeCmd.prototype.execute=function ($) {
	var _=$.selections[0];
	_.resizeEnd(this.direction,this.dx,this.dy);
};
Jpdl.cmd.UpdateNodeNameCmd=function (_,$) {
	this.node=_;
	this.newName=$;
};
Jpdl.cmd.UpdateNodeNameCmd.prototype.execute=function ($) {
	this.oldName=this.node.name;
	this.node.updateAndShowText(this.newName);
};
Jpdl.Shape=function ($) {
	$=$?$:{
	};
	Jpdl.applyIf($,{
		x:0,y:0,w:48,h:48,x1:0,y1:0,x2:10,y2:10,points:[[0,20],[50,0],[100,20]],selected:false
	});
	Jpdl.applyIf(this,$) ;
	
};
Jpdl.extend(Jpdl.Shape,Object,{
	render:function () {
		if(!this.el)if(Jpdl.isVml) {
			this.renderVml();
			this.onRenderVml();
		}else {
			this.renderSvg();
			this.onRenderSvg();
		}
	},onRenderVml:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.style.position="absolute";
		this.el.style.cursor="pointer";
		Jpdl.model.root.appendChild(this.el);
	},onRenderSvg:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.setAttribute("fill","white");
		this.el.setAttribute("stroke","black");
		this.el.setAttribute("stroke-width","1");
		this.el.setAttribute("cursor","pointer");
		Jpdl.model.root.appendChild(this.el);
	},getId:function () {
		return this.el.getAttribute("id");
	},remove:function () {
		Jpdl.model.root.removeChild(this.el)
	},show:function () {
		this.el.style.display="";
	},hide:function () {
		this.el.style.display="none";
	},select:function () {
		if(!Jpdl.model.multiSelection) {
			var A=Jpdl.model.selections;
			Jpdl.model.selections=[];
			for(var $=A.length-1;$>=0;$--) {
				var _=A[$];
				_.deselect();
			}
		}this.selected=true;
		Jpdl.model.selections.push(this);
		this.onSelect();
	},deselect:function () {
		this.selected=false;
		Jpdl.model.selected=null;
		this.onDeselect();
		var _=Jpdl.model.selections;
		for(i=_.length-1;i>=0;i--) {
			var $=_[i];
			if($.getId()==this.getId()) {
				_.splice(i,1);
				break;
			}
		}
	},toggle:function () {
		if(!this.selected||!Jpdl.model.multiSelection)this.select();
	},onSelect:function () {
		if(Jpdl.isVml)this.onSelectVml();
		else this.onSelectSvg();
	},onSelectVml:function () {
		this.el.setAttribute("fillcolor","red");
	},onSelectSvg:function () {
		this.el.setAttribute("fill","red");
	},onDeselect:function () {
		if(Jpdl.isVml)this.onDeselectVml();
		else this.onDeselectSvg();
	},onDeselectVml:function () {
		this.el.setAttribute("fillcolor","white");
	},onDeselectSvg:function () {
		this.el.setAttribute("fill","white");
	},move:function ($,_) {
		if(Jpdl.isVml) {
			this.moveVml($,_);
			if(Jpdl.model.dragType=="selectNode")this.el.style.cursor="move";
			else this.el.style.cursor="pointer";
		}else {
			this.moveSvg($,_);
			if(Jpdl.model.dragType=="selectNode")this.el.setAttribute("cursor","move");
			else this.el.setAttribute("cursor","pointer");
		}
	},moveVml:function ($,_) {
		this.el.style.left=this.x+$+"px";
		this.el.style.top=this.y+_+"px";
	},moveSvg:function ($,_) {
		this.el.setAttribute("x",this.x+$);
		this.el.setAttribute("y",this.y+_);
	},moveEnd:function ($,_) {
		this.x+=$;
		this.y+=_;
	}
});
Jpdl.Circle=Jpdl.extend(Jpdl.Shape,{
	renderVml:function () {
		var $=document.createElement("v:oval");
		$.style.left=this.x-this.r+"px";
		$.style.top=this.y-this.r+"px";
		$.style.width=this.r*2+"px";
		$.style.height=this.r*2+"px";
		this.el=$;
	},renderSvg:function () {
		var $=document.createElementNS(Jpdl.svgns,"circle");
		$.setAttribute("cx",this.x);
		$.setAttribute("cy",this.y);
		$.setAttribute("r",this.r);
		this.el=$;
	},moveVml:function ($,_) {
		this.el.style.left=this.x-this.r+$+"px";
		this.el.style.top=this.y-this.r+_+"px";
	},moveSvg:function ($,_) {
		this.el.setAttribute("cx",this.x+$);
		this.el.setAttribute("cy",this.y+_);
	},moveEnd:function ($,_) {
		this.move($,_);
		this.x+=$;
		this.y+=_;
	}
});
Jpdl.Image=Jpdl.extend(Jpdl.Shape,{
	renderVml:function () {
		var $=document.createElement("img");
		$.style.left=this.x+"px";
		$.style.top=this.y+"px";
		$.setAttribute("src",this.url);
		this.el=$;
	},renderSvg:function () {
		var $=document.createElementNS(Jpdl.svgns,"image");
		$.setAttribute("x",this.x+"px");
		$.setAttribute("y",this.y+"px");
		$.setAttribute("width","48px");
		$.setAttribute("height","48px");
		$.setAttributeNS(Jpdl.linkns,"xlink:href",this.url);
		this.el=$;
	},moveVml:function ($,_) {
		this.el.style.left=this.x+$+"px";
		this.el.style.top=this.y+_+"px";
	},moveSvg:function ($,_) {
		this.el.setAttribute("x",this.x+$);
		this.el.setAttribute("y",this.y+_);
	},moveEnd:function ($,_) {
		this.move($,_);
		this.x+=$;
		this.y+=_;
	},onRenderVml:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.style.position="absolute";
		this.el.style.cursor="pointer";
		Jpdl.model.root.appendChild(this.el);
	},onRenderSvg:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.setAttribute("cursor","pointer");
		Jpdl.model.root.appendChild(this.el);
	}
});
Jpdl.Line=Jpdl.extend(Jpdl.Shape,{
	renderVml:function () {
		//alert(this.x1 + " " + this.y1 + " " +this.x2 + " " +this.y2)
		var $=document.createElement("v:line");
		$.setAttribute("from",this.x1+","+this.y1);
		$.setAttribute("to",this.x2+","+this.y2);
		this.el=$
	},renderSvg:function () {
		var $=document.createElementNS(Jpdl.svgns,"line");
		$.setAttribute("x1",this.x1+"px");
		$.setAttribute("y1",this.y1+"px");
		$.setAttribute("x2",this.x2+"px");
		$.setAttribute("y2",this.y2+"px");
		this.el=$
	},onRenderVml:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.style.position="absolute";
		this.el.style.cursor="pointer";
		this.el.setAttribute("strokeweight",2);
		this.el.setAttribute("strokecolor","blue");
		Jpdl.model.root.appendChild(this.el);
	},onRenderSvg:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.setAttribute("fill","white");
		this.el.setAttribute("stroke","blue");
		this.el.setAttribute("stroke-width","2");
		this.el.setAttribute("cursor","pointer");
		Jpdl.model.root.appendChild(this.el);
	},moveVml:function (_,B) {
		var A=(this.x1+_)+","+(this.y1+B),$=(this.x2+_)+","+(this.y2+B);
		this.el.setAttribute("from",A);
		this.el.setAttribute("to",$);
	},moveSvg:function ($,_) {
		this.el.setAttribute("x1",this.x1+$+"px");
		this.el.setAttribute("y1",this.y1+_+"px");
		this.el.setAttribute("x2",this.x2+$+"px");
		this.el.setAttribute("y2",this.y2+_+"px");
	},moveEnd:function ($,_) {
		this.move($,_);
		this.x1+=$;
		this.y1+=_;
		this.x2+=$;
		this.y2+=_;
	},onSelectVml:function () {
		this.el.setAttribute("strokeweight","4");
		this.el.setAttribute("strokecolor","green");
	},onSelectSvg:function () {
		this.el.setAttribute("stroke-width","4");
		this.el.setAttribute("stroke","green");
	},onDeselectVml:function () {
		this.el.setAttribute("strokeweight","2");
		this.el.setAttribute("strokecolor","blue");
	},onDeselectSvg:function () {
		this.el.setAttribute("stroke-width","2");
		this.el.setAttribute("stroke","blue");
	}
});
Jpdl.Polyline=Jpdl.extend(Jpdl.Shape,{
	getPoint:function (_,C) {
		var A="";
		for(var $=0;$<this.points.length;$++) {
			var B=this.points[$];
			A+=(B[0]+_)+","+(B[1]+C)+" "
		}return A;
	},renderVml:function () {
		var $=document.createElement("v:polyline");
		$.setAttribute("points",this.getPoint(0,0));
		this.el=$;
	},renderSvg:function () {
		var $=document.createElementNS(Jpdl.svgns,"polyline");
		$.setAttribute("points",this.getPoint(0,0));
		this.el=$;
	},onRenderVml:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.style.position="absolute";
		this.el.style.cursor="pointer";
		this.el.setAttribute("strokeweight",2);
		this.el.setAttribute("strokecolor","blue");
		Jpdl.model.root.appendChild(this.el)
	},onRenderSvg:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.setAttribute("fill","white");
		this.el.setAttribute("stroke","blue");
		this.el.setAttribute("stroke-width","2");
		this.el.setAttribute("cursor","pointer");
		Jpdl.model.root.appendChild(this.el)
	},moveVml:function ($,_) {
		this.el.points.value=this.getPoint($,_)
	},moveSvg:function ($,_) {
		this.el.setAttribute("points",this.getPoint($,_))
	},moveEnd:function (_,B) {
		this.move(_,B);
		for(var $=0;$<this.points.length;$++) {
			var A=this.points[$];
			A[0]+=_;
			A[1]+=B
		}
	},onSelectVml:function () {
		this.el.setAttribute("strokeweight","4");
		this.el.setAttribute("strokecolor","green")
	},onSelectSvg:function () {
		this.el.setAttribute("stroke-width","4");
		this.el.setAttribute("stroke","green")
	},onDeselectVml:function () {
		this.el.setAttribute("strokeweight","2");
		this.el.setAttribute("strokecolor","blue")
	},onDeselectSvg:function () {
		this.el.setAttribute("stroke-width","2");
		this.el.setAttribute("stroke","blue")
	}
});
Jpdl.Rect=Jpdl.extend(Jpdl.Shape,{
	renderVml:function () {
		var $=document.createElement("v:rect");
		$.style.left=this.x+"px";
		$.style.top=this.y+"px";
		$.style.width=this.w+"px";
		$.style.height=this.h+"px";
		this.el=$
	},renderSvg:function () {
		var $=document.createElementNS(Jpdl.svgns,"rect");
		$.setAttribute("x",this.x+"px");
		$.setAttribute("y",this.y+"px");
		$.setAttribute("width",this.w+"px");
		$.setAttribute("height",this.h+"px");
		this.el=$
	},moveVml:function ($,_) {
		this.el.style.left=this.x+$+"px";
		this.el.style.top=this.y+_+"px"
	},moveSvg:function ($,_) {
		this.el.setAttribute("x",this.x+$);
		this.el.setAttribute("y",this.y+_)
	},moveEnd:function ($,_) {
		this.move($,_);
		this.x+=$;
		this.y+=_
	}
});
Jpdl.RoundRect=Jpdl.extend(Jpdl.Shape,{
	renderVml:function () {
		var $=document.createElement("v:roundrect");
		$.style.left=this.x+"px";
		$.style.top=this.y+"px";
		$.style.width=this.w+"px";
		$.style.height=this.h+"px";
		$.setAttribute("arcsize",0.2);
		this.el=$
	},renderSvg:function () {
		var $=document.createElementNS(Jpdl.svgns,"rect");
		$.setAttribute("x",this.x+"px");
		$.setAttribute("y",this.y+"px");
		$.setAttribute("width",this.w+"px");
		$.setAttribute("height",this.h+"px");
		$.setAttribute("rx",10);
		$.setAttribute("ry",10);
		this.el=$
	},moveVml:function ($,_) {
		this.el.style.left=this.x+$+"px";
		this.el.style.top=this.y+_+"px"
	},moveSvg:function ($,_) {
		this.el.setAttribute("x",this.x+$);
		this.el.setAttribute("y",this.y+_)
	},moveEnd:function ($,_) {
		this.x+=$;
		this.y+=_
	}
});
Jpdl.Text=Jpdl.extend(Jpdl.Shape,{
	renderVml:function () {
		var $=document.createElement("v:textbox");
		$.style.left=this.x+"px";
		$.style.top=this.y+"px";
		$.innerHTML=this.text;
		this.el=$
	},renderSvg:function () {
		var $=document.createElementNS(Jpdl.svgns,"text");
		$.setAttribute("x",this.x+"px");
		$.setAttribute("y",this.y+"px");
		$.textContent=this.text;
		this.el=$
	},moveVml:function ($,_) {
		this.el.style.left=this.x+$+"px";
		this.el.style.top=this.y+_+"px";
		if(Jpdl.model.dragType=="none") {
			this.x+=$;
			this.y+=_
		}
	},moveSvg:function ($,_) {
		this.el.setAttribute("x",this.x+$);
		this.el.setAttribute("y",this.y+_);
		if(Jpdl.model.dragType=="none") {
			this.x+=$;
			this.y+=_
		}
	}
});

Jpdl.listeners.Observable=function () {
	this.events={
	}
};
Jpdl.listeners.Observable.prototype={
	addListener:function (_,A) {
		_=_.toLowerCase();
		var $=this.events[_]||true;
		if(typeof($)=="boolean") {
			$=new Jpdl.listeners.Event(this,_);
			this.events[_]=$
		}$.addListener(A)
	},removeListener:function (_,A) {
		var $=this.events[_.toLowerCase()];
		if(typeof($)=="object")$.removeListener(rn)
	},clearListeners:function () {
		for(var $ in this.events)if(typeof(this.events[$])=="object")this.events[$].clearListeners()
	},fireEvent:function () {
		var $=this.events[arguments[0].toLowerCase()];
		if(typeof($)=="object")return $.fire.apply($,Array.prototype.slice.call(arguments,1));
		return true
	}
};
Jpdl.listeners.Event=function ($,_) {
	this.obj=$;
	this.name=_;
	this.listeners=[]
};
Jpdl.listeners.Event.prototype={
	indexOf:function (_) {
		for(var $=0;$<this.listeners.length;$++)if(this.listeners[$]==_)return $;return -1
	},addListener:function ($) {
		if(this.indexOf($)==-1)if(this.firing) {
			this.listeners=this.listeners.splice(0);
			this.listeners.push($)
		}else this.listeners.push($)
	},removeListener:function (_) {
		var $=this.indexOf(_);
		if($!=-1)if(this.firing) {
			this.listeners=this.listeners.splice(0);
			this.listeners.splice($,1)
		}else this.listeners.splice($,1)
	},clearListeners:function () {
		this.listeners=[]
	},fire:function () {
		var A=this.listeners,$=A.length;
		if($>0) {
			this.firing=true;
			for(var _=0;_<$;_++) {
				var B=A[_];
				if(B.apply(this.obj,arguments)===false) {
					this.firing=false;
					return false
				}
			}this.firing=false
		}return true
	}
};
Jpdl.listeners.BrowserListener=function ($) {
	
	this.m=$;
	var _=this;
	this.status="none";
	
	document.onmousedown=function (A) {
		var $=Jpdl.isIE?event:A;
		_.mouseDown($)
	};
	document.onmousemove=function (A) {
		var $=Jpdl.isIE?event:A;
		_.mouseMove($)
	};
	document.onmouseup=function (A) {
		var $=Jpdl.isIE?event:A;
		_.mouseUp($)
	};
	document.ondblclick=function (A) {
		var $=Jpdl.isIE?event:A;
		_.doubleClick($)
	};
	document.onkeydown=function (A) {
		var $=Jpdl.isIE?event:A;
		_.keyDown($)
	};
	$.textEditor.el.onblur=function (A) {
		var $=Jpdl.isIE?event:A;
		_.blur($)
	};
	this.events={
		"startMove":true,"moving":true,"endMove":true,"doubleClick":true,"press":true
	};
	this.addListener("startMove",Jpdl.listeners.startMoveMarquee);
	this.addListener("startMove",Jpdl.listeners.startMoveNode);
	this.addListener("startMove",Jpdl.listeners.startMovePalette);
	this.addListener("startMove",Jpdl.listeners.startMoveTransition);
	this.addListener("startMove",Jpdl.listeners.startResizeNode);
	this.addListener("startMove",Jpdl.listeners.startLinkTransition);
	this.addListener("moving",Jpdl.listeners.movingMarquee);
	this.addListener("moving",Jpdl.listeners.movingNode);
	this.addListener("moving",Jpdl.listeners.movingPalette);
	this.addListener("moving",Jpdl.listeners.movingTransition);
	this.addListener("moving",Jpdl.listeners.movingResizeNode);
	this.addListener("moving",Jpdl.listeners.movingLinkTransition);
	this.addListener("endMove",Jpdl.listeners.endMoveMarquee);
	this.addListener("endMove",Jpdl.listeners.endMoveNode);
	this.addListener("endMove",Jpdl.listeners.endMovePalette);
	this.addListener("endMove",Jpdl.listeners.endMoveTransition);
	this.addListener("endMove",Jpdl.listeners.endResizeNode);
	this.addListener("endMove",Jpdl.listeners.endLinkTransition);
	this.addListener("doubleClick",Jpdl.listeners.startEditText);
	this.addListener("press",Jpdl.listeners.removeSelections);
	this.addListener("press",Jpdl.listeners.selectAllNodes);
	this.addListener("press",Jpdl.listeners.cancelEditText);
	this.addListener("press",Jpdl.listeners.completeEditText);
	this.addListener("blur",Jpdl.listeners.blurEditText)
};
Jpdl.listeners.BrowserListener=Jpdl.extend(Jpdl.listeners.BrowserListener,Jpdl.listeners.Observable,{
	mouseDown:function (A) {
		var _=this.getXY(A),$=this.getTarget(A);
		if(!this.fireEvent("startMove",this.m,_,$))this.stopEvent(A)
	},mouseMove:function (A) {
		var _=this.getXY(A),$=this.getTarget(A);
		if(!this.fireEvent("moving",this.m,_,$))this.stopEvent(A)
	},mouseUp:function (A) {
		var _=this.getXY(A),$=this.getTarget(A);
		if(!this.fireEvent("endMove",this.m,_,$))this.stopEvent(A)
	},doubleClick:function (A) {
		var _=this.getXY(A),$=this.getTarget(A);
		if(!this.fireEvent("doubleClick",this.m,_,$))this.stopEvent(A)
	},keyDown:function ($) {
		if(!this.fireEvent("press",this.m,$.keyCode,$.ctrlKey))this.stopEvent($)
	},blur:function ($) {
		if(!this.fireEvent("blur",this.m))this.stopEvent($)
	},getXY:function (_) {
		var $={
		};
		if(typeof(window.pageYOffset)!="undefined") {
			$.x=window.pageXOffset;
			$.y=window.pageYOffset
		}else if(typeof(document.compatMode)!="undefined"&&document.compatMode!="BackCompat") {
			$.x=document.documentElement.scrollLeft;
			$.y=document.documentElement.scrollTop
		}else if(typeof(document.body)!="undefined") {
			$.x=document.body.scrollLeft;
			$.y=document.body.scrollTop
		}return {
			x:_.clientX+$.x-this.m.x,y:_.clientY+$.y-this.m.y
		}
	},getTarget:function ($) {
		return Jpdl.isIE?$.srcElement:$.target
	},stopEvent:function ($) {
		if(Jpdl.isIE)$.returnValue=false;
		else $.preventDefault()
	}
});
Jpdl.listeners.blurEditText=function ($) {
	if(this.status=="editingText") {
		var A=$.selections[0],_=$.textEditor.el.value;
		Jpdl.cmd.CommandService.execute(new Jpdl.cmd.UpdateNodeNameCmd(A,_));
		$.textEditor.hide();
		this.status="none";
		return false
	}return true
};
Jpdl.listeners.cancelEditText=function (_,A,$) {
	if(this.status=="editingText"&&A==27) {
		_.selections[0].cancelEditText();
		_.textEditor.hide();
		this.status="none";
		return false
	}return true
};
Jpdl.listeners.completeEditText=function (_,C,$) {
	if(this.status=="editingText")if(C==10||C==13) {
		var B=_.selections[0],A=_.textEditor.el.value;
		Jpdl.cmd.CommandService.execute(new Jpdl.cmd.UpdateNodeNameCmd(B,A));
		_.textEditor.hide();
		this.status="none";
		return false
	}return true
};
Jpdl.listeners.endLinkTransition=function (A,E,D) {
	if(this.status=="linkTransition"&&A.isInside(E.x,E.y)) {
		A.tempLine.hide();
		var C=A.getTargetNode(D);
		if(C!=null) {
			if(this.type=="start") {
				if(C.nodeName=="end"||C.nodeName=="end-cancel"||C.nodeName=="end-error") {
					alert("结束节点不允许有连线出去");
					this.status="none";
					this.type=null;
					this.transition=null;
					return false
				}var F=C,$=this.transition.to;
				for(var _=0;_<F.outputs.length;_++) {
					var B=F.outputs[_];
					if(B.to.getId()==$.getId()) {
						alert("已经有一条连线了");
						this.status="none";
						return false
					}
				}Jpdl.cmd.CommandService.execute(new Jpdl.cmd.LinkTransitionStartCmd(this.transition,C))
			}else {
				if(C.nodeName=="start") {
					alert("开始节点不允许有连线进来");
					this.status="none";
					this.type=null;
					this.transition=null;
					return false
				}F=this.transition.from,$=C;
				for(_=0;_<F.outputs.length;_++) {
					B=F.outputs[_];
					if(B.to.getId()==$.getId()) {
						alert("已经有一条连线了");
						this.status="none";
						return false
					}
				}Jpdl.cmd.CommandService.execute(new Jpdl.cmd.LinkTransitionEndCmd(this.transition,C))
			}this.transition.refresh();
			this.status="none";
			this.type=null;
			this.transition=null;
			return false
		}return false
	}return true
};
Jpdl.listeners.endMoveMarquee=function (A,H,F) {
	if(this.status=="marquee") {
		for(var _=A.selections.length-1;_>=0;_--)A.selections[_].deselect();A.selections=[];var I=A.tempRect.x,G=A.tempRect.y,B=A.tempRect.w,C=A.tempRect.h;A.multiSelection=true;for(_ in A.nodes) {
			var D=A.nodes[_];
			if(typeof(D.nodeName)!="undefined") {
				var E=D.x+D.w/2,$=D.y+D.h/2;
				if(E>I&&E<I+B&&$>G&&$<G+C)D.select()
			}
		}A.multiSelection=false;
		A.tempRect.hide();
		this.status="none";
		return false
	}return true
};
Jpdl.listeners.endMoveNode=function ($,A,_) {
	if(this.status=="selectNode") {
		Jpdl.cmd.CommandService.execute(new Jpdl.cmd.MoveNodeCmd($.x1,$.y1,A.x,A.y));
		this.status="none";
		return false
	}return true
};
Jpdl.listeners.endMovePalette=function (_,B,A) {
	if(this.status=="palette") {
		if(_.activePalette=="start")for(var $ in _.nodes)if(_.nodes[$].nodeName=="start") {
			alert("只允许有一个开始节点");
			_.tempRect.hide();
			this.status="none";
			return false
		}
		Jpdl.cmd.CommandService.execute(new Jpdl.cmd.AddNodeCmd(B.x,B.y));
		_.tempRect.hide();
		this.status="none";
		return false
	}return true
};
Jpdl.listeners.endMoveTransition=function (A,D,C) {
	if(this.status=="transition") {
		if(A.activePalette=="transition-straight")A.tempLine.hide();
		else A.tempPolyline.hide();
		var E=A.selections[0],$=A.getTargetNode(C);
		if($==null) {
			this.status="none";
			return false
		}
		for(var _=0;_<E.outputs.length;_++) {
			var B=E.outputs[_];
			if(B.to.getId()==$.getId()) {
				alert("已经有一条连线了");
				this.status="none";
				return false
			}
		}
		
		if($.nodeName=="start") {
			alert("开始节点不允许有连线进来");
			this.status="none";
			return false
		}
		Jpdl.cmd.CommandService.execute(new Jpdl.cmd.AddEdgeCmd(E,$));
		this.status="none";
		return false
	}
	
	return true
};
Jpdl.listeners.endResizeNode=function ($,C,B) {
	if(this.status.indexOf("-resize")!=-1&&$.isInside(C.x,C.y)) {
		var _=this.status.split("-")[0],A=C.x-$.x1,D=C.y-$.y1;
		Jpdl.cmd.CommandService.execute(new Jpdl.cmd.ResizeNodeCmd(_,A,D));
		this.status="none";
		return false
	}return true
};
Jpdl.listeners.movingLinkTransition=function ($,A,_) {
	if(this.status=="linkTransition"&&$.isInside(A.x,A.y)) {
		if(this.type=="start")$.tempLine.update(A.x,A.y,$.x1,$.y1);
		else $.tempLine.update($.x1,$.y1,A.x,A.y);
		return false
	}return true
};
Jpdl.listeners.movingMarquee=function ($,A,_) {
	if(this.status=="marquee"&&$.isInside(A.x,A.y)) {
		$.tempRect.update($.x1,$.y1,A.x-$.x1,A.y-$.y1);
		return false
	}return true
};
Jpdl.listeners.movingNode=function (_,D,C) {
	if(this.status=="selectNode"&&_.isInside(D.x,D.y)) {
		var A=D.x-_.x1,E=D.y-_.y1;
		for(var $=0;$<_.selections.length;$++) {
			var B=_.selections[$];
			B.move(A,E)
		}return false
	}return true
};
Jpdl.listeners.movingPalette=function ($,A,_) {
	if(this.status=="palette"&&$.isInside(A.x,A.y)) {
		$.tempRect.update(A.x-$.tempRect.w/2,A.y-$.tempRect.h/2,$.tempRect.w,$.tempRect.h);
		return false
	}return true
};
Jpdl.listeners.movingResizeNode=function ($,D,C) {
	if(this.status.indexOf("-resize")!=-1&&$.isInside(D.x,D.y)) {
		var B=$.selections[0],_=this.status.split("-")[0],A=D.x-$.x1,E=D.y-$.y1;
		B.resize(_,A,E);
		return false
	}return true
};
Jpdl.listeners.movingTransition=function ($,A,_) {
	if(this.status=="transition"&&$.isInside(A.x,A.y)) {
		if($.activePalette=="transition-straight")$.tempLine.update($.tempLine.x1,$.tempLine.y1,A.x,A.y);
		else $.tempPolyline.update($.tempPolyline.points[0][0],$.tempPolyline.points[0][1],A.x,A.y);
		return false
	}return true
};
Jpdl.listeners.removeSelections=function (_,A,$) {
	if(this.status=="none"&&A==46) {
		Jpdl.cmd.CommandService.execute(new Jpdl.cmd.RemoveSelectionsCmd(_));
		this.status="none";
		return false
	}return true
};
Jpdl.listeners.selectAllNodes=function (_,A,$) {
	if(this.status=="none"&&$&&A==65) {
		_.multiSelection=true;
		_.selectAll();
		return false
	}return true
};
Jpdl.listeners.startEditText=function ($,C,B) {
	if((Jpdl.isVml&&B.tagName=="textbox")||(Jpdl.isSvg&&B.tagName=="text")) {
		this.status="editingText";
		var D=$.getTargetNode(B);
		if(D!=null) {
			D.hideText();
			$.textEditor.showForNode(D);
			$.selections=[D]
		}else {
			var A=B.getAttribute("edgeId"),_=$.nodes[A];
			_.hideText();
			$.textEditor.showForEdge(_);
			$.selections=[_]
		}return false
	}return true
};
Jpdl.listeners.startLinkTransition=function (A,E,D) {
	if(this.status=="none"&&A.isInside(E.x,E.y)) {
		var G=D.tagName,F=Jpdl.isVml?D.id:D.getAttribute("id");
		if(F!=null&&F.indexOf(":")!=-1) {
			var $=F.indexOf(":"),_=F.substring(0,$),B=F.substring($+1);
			if(B=="start"||B=="end")if(G=="rect") {
				var C=A.nodes[_];
				if(B=="start") {
					A.x1=C.x2;
					A.y1=C.y2
				}else {
					A.x1=C.x1;
					A.y1=C.y1
				}A.tempLine.update(A.x1,A.y1,E.x,E.y);
				A.tempLine.show();
				this.status="linkTransition";
				this.type=B;
				this.transition=C;
				return false
			}
		}
	}
	return true
};
Jpdl.listeners.startMoveMarquee=function ($,A,_) {
	if(this.status=="none"&&$.isInside(A.x,A.y))if((Jpdl.isVml&&_.tagName=="HTML")||(Jpdl.isSvg&&_.tagName=="svg")) {
		
		$.x1=A.x;
		$.y1=A.y;
		this.status="marquee";
		$.tempRect.update(0,0,0,0);
		$.tempRect.show();
		return false
	}return true
};
Jpdl.listeners.startMoveNode=function (_,C,B) {
	if(this.status=="none"&&_.isInside(C.x,C.y)) {
		var D=_.getTargetNode(B);
		if(D) {
			_.x1=C.x;
			_.y1=C.y;
			D.toggle();
			for(var $=0;$<_.selections.length;$++) {
				var A=_.selections[$];
				if(A.getId()==D.getId()&&A.el.tagName!="line"&&A.el.tagName!="polyline")if(_.activePalette!="transition-straight"&&_.activePalette!="transition-broken") {
					this.status="selectNode";
					
					/*
					if(attrWin.rendered && (D.nodeName == "start" || D.nodeName == "end")){   
						attrWin.hide();
					} 
					
					if(D.nodeName != "start" && D.nodeName != "end") {
						attrWin.x = D.x + 250;
						attrWin.show(); 
					}*/
					
					var rightDiv = document.getElementById("rightDiv") ;
					var decisionTr = document.getElementById("decisionTr") ;   
					var decisionTr1 = document.getElementById("decisionTr1") ;
					var decisionTr2 = document.getElementById("decisionTr2") ;
					var form = document.getElementById("fromTr") ;
					var handlerTr = document.getElementById("handlerTr") ;
					var subProcessTr = document.getElementById("subProcessTr") ;
					var msgDiv = document.getElementById("msgDiv") ;
					
					if(D.name) {
						document.getElementById("nodeName").value = D.name ;
					}
					
					if(D.nodeName == "task") {
						rightDiv.style.display = "" ;
						decisionTr.style.display = "none" ;
						decisionTr1.style.display = "none" ;
						decisionTr2.style.display = "none" ;
						subProcessTr.style.display = "none" ;
						form.style.display = "" ;
						handlerTr.style.display = "" ;
						msgDiv.style.display = "" ;
						
						var rightType = D.rightType ;
						
						if(!rightType) rightType = "自定义权限" ;
						
						var rightTypeObj = document.getElementById("rightType") ;
						rightTypeObj.value = rightType ;
						changeRightType(rightTypeObj) ;
						
						var selectUser = D.selectUser ;
						if(selectUser == "是"){
							document.getElementById("isSelectUser").checked  = true;
						}else {
							document.getElementById("isSelectUser").checked  = false;
						}
						
						
						if(D.form) {
							//document.getElementById("form").value = D.form ;
							Ext.getCmp("form").setRealValue(D.form);
						}else {
							//document.getElementById("form").value = "" ;
							Ext.getCmp("form").clear();
						}
						
						if(D.handlerClass) {
							document.getElementById("handlerClass").value = D.handlerClass ;
						}else {
							document.getElementById("handlerClass").value = "" ;
						}
						
						//信息提醒设置
						document.getElementById("sletter").checked = D.sletter ;
						document.getElementById("sMsg").checked = D.sMsg ;
						document.getElementById("dLetter").checked = D.dLetter ;
						document.getElementById("dMsg").checked = D.dMsg ;
						
						if(rightType == "自定义权限") {
							var role = D.role ;
							var department = D.department ;
							var user = D.user ;
							
							//alert(role + "," + department + "," + user);
							
							if(department) {
								//document.getElementById("department").value = department;
								Ext.getCmp("department").setRealValue(department);
							}else {
								//document.getElementById("department").value = "";
								Ext.getCmp("department").clear();
							}
								
							
							if(role) {
								//document.getElementById("role").value = role;
								Ext.getCmp("role").setRealValue(role);
							}else {
								//document.getElementById("role").value = "";
								Ext.getCmp("role").clear();
							}
							
							if(user) {
								//document.getElementById("user").value = user;
								Ext.getCmp("user").setRealValue(user);
							}else {
								//document.getElementById("user").value = "";
								Ext.getCmp("user").clear();	
							}
							
						}else if(rightType == "候选人") {
							var candidateExp = D.candidateExp ;
							
							if(candidateExp) {
								document.getElementById("candidateExp").value = candidateExp  ;
							}else {
								document.getElementById("candidateExp").value = ""  ;
							}
						}else if(rightType == "待办人") {
							
							var assignee = D.assignee ;
							
							if(assignee) {
								document.getElementById("assignee").value = assignee  ;
							}else {
								document.getElementById("assignee").value = ""  ;
							}
						}else if(rightType == "扩展类") {
							var rightClass = D.rightClass ;
							
							if(rightClass) {
								document.getElementById("rightClass").value = rightClass  ;
							}else {
								document.getElementById("rightClass").value = ""  ;
							}
						}
					}else if(D.nodeName == "transition") {
						
					}else if(D.nodeName == "decision") { 
						rightDiv.style.display = "none" ;
						msgDiv.style.display = "none" ;
						decisionTr.style.display = "" ;
						decisionTr1.style.display = "" ;
						form.style.display = "none" ;
						handlerTr.style.display = "none" ;
						subProcessTr.style.display = "none" ;
						
						var decisionType = D.decisionType ;
						
						if(!decisionType) decisionType = "表达式" ;
						
						var decisionTypeObj = document.getElementById("decisionType") ;
						decisionTypeObj.value = decisionType ;
						changeDecisionType(decisionTypeObj) ;
						
						
						if(decisionType == "表达式") {
							if(D.decisionExp) {
								document.getElementById("decisionExp").value = D.decisionExp  ;
							}else {
								document.getElementById("decisionExp").value = ""  ;
							}
						}else if(decisionType == "调用类") {
							if(D.decisionClass) {
								document.getElementById("decisionClass").value = D.decisionClass  ;
							}else {
								document.getElementById("decisionClass").value = ""  ;
							}
						}
						
					}else if(D.nodeName == "sub-process") {
						rightDiv.style.display = "none" ;
						msgDiv.style.display = "none" ;
						decisionTr.style.display = "none" ;
						decisionTr1.style.display = "none" ;
						decisionTr2.style.display = "none" ;
						form.style.display = "none" ;
						handlerTr.style.display = "none" ;
						subProcessTr.style.display = "" ;
						if(D.subProcessKey) {
							//document.getElementById("sub-process-key").value = D.subProcessKey ;
							Ext.getCmp("sub-process-key").setRealValue(D.subProcessKey);
						}
					}
	          		  
					return false
				}
			}
		}
	}return true
};
                
Jpdl.listeners.startMovePalette=function ($,A,_) {
	if(this.status=="none"&&!$.isInside(A.x,A.y))if(_.tagName=="SPAN"&&/^paletteItem\-.+$/.test(_.className)) {
		var B=_.className.replace("paletteItem-","");
		$.changePalette(B);
		if(B!="select"&&B!="marquee"&&B!="transition-straight"&&B!="transition-broken") {
			$.x1=A.x;
			$.x2=A.y;
			this.status="palette";
			if(B=="start"||B=="end"||B=="cancel"||B=="error"||B=="decision"||B=="fork"||B=="join")$.tempRect.update(-90,-90,48,48);
			else $.tempRect.update(-90,-90,90,50);
			$.tempRect.show();
			return false
		}
	}return true
};

Jpdl.listeners.startMoveTransition=function ($,A,_) {
	if(this.status=="none"&&$.isInside(A.x,A.y)) {
		var B=$.getTargetNode(_);
		if(B) {
			$.x1=A.x;
			$.y1=A.y;
			B.toggle();
			if(B.nodeName=="start"&&B.outputs.length>0) {
				alert("开始节点只允许有一根连线");
				return false
			}
			
			if($.activePalette=="transition-straight"||$.activePalette=="transition-broken") {
				if(B.nodeName=="end"||B.nodeName=="end-cancel"||B.nodeName=="end-error") {
					alert("结束节点不允许有连线出去");
					return false
				}
				this.status="transition";
				if($.activePalette=="transition-straight") {
					$.tempLine.update(B.x+B.w/2,B.y+B.h/2,A.x,A.y);
					$.tempLine.show()
				}else {
					$.tempPolyline.update(B.x+B.w/2,B.y+B.h/2,A.x,A.y);
					$.tempPolyline.show()
				}
				return false
			}
		}
	}return true
};
Jpdl.listeners.startResizeNode=function (A,E,D) {
	if(this.status=="none"&&A.isInside(E.x,E.y)) {
		var G=D.tagName,F=Jpdl.isVml?D.id:D.getAttribute("id");
		if(F!=null&&F.indexOf(":")!=-1) {
			var _=F.substring(0,F.indexOf(":")),C=A.nodes[_],B=C.nodeName;
			if(B=="start"||B=="end"||B=="end-cancel"||B=="decision"||B=="fork"||B=="join")return false
		}if(Jpdl.isVml) {
			var $=D.style.cursor;
			if(G=="rect"&&$!=null&&$.indexOf("-resize")!=-1) {
				this.status=$;
				A.x1=E.x;
				A.y1=E.y;
				return false
			}
		}else {
			$=D.getAttribute("cursor");
			if(G=="rect"&&$!=null&&$.indexOf("-resize")!=-1) {
				this.status=$;
				A.x1=E.x;
				A.y1=E.y;
				return false
			}
		}
	}
	return true
};
Jpdl.ActivityMap={
	activityBasePath:"jpdl/images/activities/48/",
	
	getConfig:function (A) {
		var $=Jpdl.ActivityMap[A],_={};
		Jpdl.apply(_,$);
		$.num++;
		_.name=_.nodeName+_.num;
		if(_.url!=null)_.url= Jpdl.ActivityMap.activityBasePath + _.url;
		return _
	},
	
	start:{
		nodeName:"start",url:"start_event_empty.png",num:1
	},
	
	end:{
		nodeName:"end",url:"end_event_terminate.png",num:1
	},cancel:{
		nodeName:"end-cancel",url:"end_event_cancel.png",num:1
	},
	
	error:{
		nodeName:"end-error",url:"end_event_error.png",num:1
	},
	
	state:{
		nodeName:"state",url:null,num:1
	},
	
	hql:{
		nodeName:"hql",url:null,num:1
	},
	
	sql:{
		nodeName:"sql",url:null,num:1
	},
	
	java:{
		nodeName:"java",url:null,num:1
	},
	
	script:{
		nodeName:"script",url:null,num:1
	},
	
	esb:{
		nodeName:"esb",url:null,num:1
	},
	
	task:{
		nodeName:"task",url:null,num:1
	},
	
	subprocess:{
		nodeName:"sub-process",url:null,num:1
	},
	
	decision:{
		nodeName:"decision",url:"gateway_exclusive.png",num:1
	},
	
	fork:{
		nodeName:"fork",url:"gateway_parallel.png",num:1
	},
	
	join:{
		nodeName:"join",url:"gateway_parallel.png",num:1
	}
};
Jpdl.Edge=Jpdl.extend(Jpdl.Line,{
	constructor :function (_,$,showName) {
		this.from=_;
		this.to=$;
		if(showName || showName == "") {
			this.name=showName;
		}else {
			this.name="to "+$.name;
		}
		this.from.outputs.push(this);
		this.to.incomes.push(this);
		this.alive=true;
		this.calculate();
		Jpdl.Edge.superclass.constructor.call(this,{});
		this.edgeResizer=new Jpdl.EdgeResizer(this)
	},render:function () {
		Jpdl.Edge.superclass.render.call(this);
		this.edgeResizer.render(); 
		this.edgeResizer.hide()
	},onRenderVml:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.style.position="absolute";
		this.el.style.cursor="pointer";
		this.el.setAttribute("strokeweight",2);
		this.el.setAttribute("strokecolor","#909090");
		Jpdl.model.root.appendChild(this.el);
		this.stroke=document.createElement("v:stroke");
		this.el.appendChild(this.stroke);
		this.stroke.setAttribute("endArrow","Classic");
		var $=document.createElement("textbox");
		$.setAttribute("inset","5pt,5pt,5pt,5pt");
		$.style.position="absolute";
		$.style.left=(this.x1+this.x2)/2-this.name.length*3;
		$.style.top=(this.y1+this.y2)/2-10;
		$.style.textAlign="center";
		$.innerHTML=this.name;
		$.setAttribute("edgeId",this.getId());
		Jpdl.model.root.appendChild($);
		this.textEl=$
	},onRenderSvg:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.setAttribute("fill","none");
		this.el.setAttribute("stroke","#909090");
		this.el.setAttribute("stroke-width","2");
		this.el.setAttribute("cursor","pointer");
		this.el.setAttribute("marker-end","url(#markerArrow)");
		Jpdl.model.root.appendChild(this.el);
		var $=document.createElementNS(Jpdl.svgns,"text");
		$.setAttribute("x",(this.x1+this.x2)/2);
		$.setAttribute("y",(this.y1+this.y2)/2);
		$.setAttribute("text-anchor","middle");
		$.textContent=this.name;
		$.setAttribute("edgeId",this.getId());
		Jpdl.model.root.appendChild($);
		this.textEl=$
	},
	
	calculate:function () {
		//alert("from:"+this.from.x + " " + this.from.y + " " +this.from.w + " " + this.from.h);
		//alert("to:"+this.to.x + " " + this.to.y + " " +this.to.w + " " + this.to.h);
		var B=new Jpdl.geom.Line(this.from.x+this.from.w/2,this.from.y+this.from.h/2,this.to.x+this.to.w/2,this.to.y+this.to.h/2),
			A=new Jpdl.geom.Rect(this.from.x,this.from.y,this.from.w,this.from.h),
			_=new Jpdl.geom.Rect(this.to.x,this.to.y,this.to.w,this.to.h),
			$=A.getCrossPoint(B),
			C=_.getCrossPoint(B);
		//alert("from:"+this.from.x + " " + this.from.y + " " +this.from.w + " " + this.from.h); 
		//alert("x1:"+ this.from.x+this.from.w/2 + " y1:" + this.from.y+this.from.h/2 + " x2" +this.to.x+this.to.w/2 + " y2:" + this.to.y+this.to.h/2) ;
		if($==null||C==null) {
			this.x1=0;
			this.y1=0;
			this.x2=0;
			this.y2=0
		}else {
			this.x1=$.x;
			this.y1=$.y;
			this.x2=C.x;
			this.y2=C.y
		}
	},
	
	update:function (A,B,$,_) {
		this.x1=A;
		this.y1=B;
		this.x2=$;
		this.y2=_;
		if(Jpdl.isVml)this.updateVml();
		else this.updateSvg();
		this.edgeResizer.update()
	},updateVml:function () {
		this.el.setAttribute("from",this.x1+","+this.y1);
		this.el.setAttribute("to",this.x2+","+this.y2);
		this.textEl.style.left=(this.x1+this.x2)/2-this.name.length*3;
		this.textEl.style.top=(this.y1+this.y2)/2-10
	},updateSvg:function () {
		this.el.setAttribute("x1",this.x1+"px");
		this.el.setAttribute("y1",this.y1+"px");
		this.el.setAttribute("x2",this.x2+"px");
		this.el.setAttribute("y2",this.y2+"px");
		this.textEl.setAttribute("x",(this.x1+this.x2)/2);
		this.textEl.setAttribute("y",(this.y1+this.y2)/2)
	},refresh:function () {
		this.calculate();
		this.update(this.x1,this.y1,this.x2,this.y2)
	},remove:function () {
		if(this.alive) {
			var $=this.from.outputs.indexOf(this);
			this.from.outputs.splice($,1);
			var _=this.to.incomes.indexOf(this);
			this.to.incomes.splice(_,1);
			Jpdl.model.root.removeChild(this.el);
			Jpdl.model.root.removeChild(this.textEl);
			this.edgeResizer.remove();
			this.alive=false
		}
	},onSelect:function () {
		this.edgeResizer.show()
	},onDeselect:function () {
		this.edgeResizer.hide()
	},hideText:function () {
		this.textEl.style.display="none"
	},updateAndShowText:function ($) {
		this.name=$;
		if(Jpdl.isVml) {
			this.textEl.innerHTML=$;
			this.textEl.style.left=(this.x1+this.x2)/2-this.name.length*11;
			this.textEl.style.top=(this.y1+this.y2)/2-10
		}else this.textEl.textContent=$;
		this.textEl.style.display=""
	},cancelEditText:function () {
		this.textEl.style.display=""
	}
});
Jpdl.EdgeResizer=function ($) {
	this.edge=$
};
Jpdl.extend(Jpdl.EdgeResizer,Jpdl.Shape,{
	renderVml:function () {
		var C=this.edge.x1,D=this.edge.y1,_=this.edge.x2,B=this.edge.y2,A=document.createElement("v:group");
		A.style.width=Jpdl.model.w;
		A.style.height=Jpdl.model.h;
		A.setAttribute("coordsize",Jpdl.model.w+","+Jpdl.model.h);
		Jpdl.model.root.appendChild(A);
		this.el=A;
		var $=document.createElement("v:line");
		$.from=C+","+D;
		$.to=_+","+B;
		$.strokeweight="1";
		$.strokecolor="black";
		$.style.position="absolute";
		A.appendChild($);
		this.lineEl=$;
		this.startEl=this.createItemVml(C,D,"start");
		this.endEl=this.createItemVml(_,B,"end")
	},createItemVml:function (B,_,A) {
		var $=document.createElement("v:rect");
		$.id=this.edge.getId()+":"+A;
		$.fillcolor="black";
		$.style.left=(B-2)+"px";
		$.style.top=(_-2)+"px";
		$.style.width="4px";
		$.style.height="4px";
		this.el.appendChild($);
		return $
	},renderSvg:function () {
		var C=this.edge.x1,D=this.edge.y1,_=this.edge.x2,B=this.edge.y2,A=document.createElementNS(Jpdl.svgns,"g");
		Jpdl.model.root.appendChild(A);
		this.el=A;
		var $=document.createElementNS(Jpdl.svgns,"line");
		$.setAttribute("x1",C);
		$.setAttribute("y1",D);
		$.setAttribute("x2",_);
		$.setAttribute("y2",B);
		$.setAttribute("stroke","black");
		A.appendChild($);
		this.lineEl=$;
		this.startEl=this.createItemSvg(C,D,"start");
		this.endEl=this.createItemSvg(_,B,"end")
	},createItemSvg:function (B,_,A) {
		var $=document.createElementNS(Jpdl.svgns,"rect");
		$.setAttribute("id",this.edge.getId()+":"+A);
		$.setAttribute("x",B-2);
		$.setAttribute("y",_-2);
		$.setAttribute("width",4);
		$.setAttribute("height",4);
		$.setAttribute("stroke","none");
		$.setAttribute("fill","black");
		this.el.appendChild($);
		return $
	},update:function () {
		if(Jpdl.isVml)this.updateVml();
		else this.updateSvg()
	},updateVml:function () {
		var A=this.edge.x1,B=this.edge.y1,$=this.edge.x2,_=this.edge.y2;
		this.lineEl.from=A+","+B;
		this.lineEl.to=$+","+_;
		this.startEl.style.left=(A-2)+"px";
		this.startEl.style.top=(B-2)+"px";
		this.endEl.style.left=($-2)+"px";
		this.endEl.style.top=(_-2)+"px"
	},updateSvg:function () {
		var A=this.edge.x1,B=this.edge.y1,$=this.edge.x2,_=this.edge.y2;
		this.lineEl.setAttribute("x1",A);
		this.lineEl.setAttribute("y1",B);
		this.lineEl.setAttribute("x2",$);
		this.lineEl.setAttribute("y2",_);
		this.startEl.setAttribute("x",A-2);
		this.startEl.setAttribute("y",B-2);
		this.endEl.setAttribute("x",$-2);
		this.endEl.setAttribute("y",_-2)
	}
});
Jpdl.ImageNode=Jpdl.extend(Jpdl.Image,{
	constructor :function ($) {
		this.outputs=[];
		this.incomes=[];
		Jpdl.ImageNode.superclass.constructor .call(this,$);
		this.nodeResizer=new Jpdl.NodeResizer(this)
	},render:function () {
		Jpdl.ImageNode.superclass.render.call(this);
		this.nodeResizer.render();
		this.nodeResizer.hide()
	},move:function (A,B) {
		Jpdl.ImageNode.superclass.move.call(this,A,B);
		this.nodeResizer.move(A,B);
		for(var $=0;$<this.incomes.length;$++) {
			var _=this.incomes[$];
			_.refresh()
		}for($=0;$<this.outputs.length;$++) {
			_=this.outputs[$];
			_.refresh()
		}
	},moveEnd:function (A,B) {
		Jpdl.ImageNode.superclass.moveEnd.call(this,A,B);
		this.nodeResizer.moveEnd(A,B);
		for(var $=0;$<this.incomes.length;$++) {
			var _=this.incomes[$];
			_.refresh()
		}for($=0;$<this.outputs.length;$++) {
			_=this.outputs[$];
			_.refresh()
		}
	},remove:function () {
		for(var $=this.outputs.length-1;$>=0;$--) {
			var _=this.outputs[$];
			_.remove()
		}for($=this.incomes.length-1;$>=0;$--) {
			_=this.incomes[$];
			_.remove()
		}Jpdl.model.root.removeChild(this.el);
		this.nodeResizer.remove()
	},onSelect:function () {
		this.nodeResizer.show()
	},onDeselect:function () {
		this.nodeResizer.hide()
	}
});
Jpdl.Model=function ($) {
	$=$?$:{
	};
	this.el=$.id?document.getElementById($.id):document.body;
	this.x=Jpdl.getInt(this.el.style.left);
	this.y=Jpdl.getInt(this.el.style.top);
	this.w=Jpdl.getInt(this.el.style.width);
	this.h=Jpdl.getInt(this.el.style.height);
	this.name="untitled";
	this.nodes={
	};
	this.selections=[];
	this.multiSelection=false;
	this.x1=0;
	this.y1=0;
	this.x2=0;
	this.y2=0;
	this.activePalette="select";
	this.changePalette(this.activePalette);
	Jpdl.model=this;
	
	Jpdl.ModelDecorator.render(this);
	this.browserListener=new Jpdl.listeners.BrowserListener(this) ;
};
Jpdl.Model.prototype={
	resize:function (B,A,$,_) {
		this.x=B;
		this.y=A;
		this.w=$;
		this.h=_;
		Jpdl.ModelDecorator.resize(this)
	},
	
	add:function ($) { 
		$.model=this;
		$.render();
		this.nodes[$.getId()]=$
	},
	
	remove:function ($) {
		$.remove();
		delete this.nodes[$.getId()]
	},
	selectAll:function () {
		if(this.multiSelection)for(var $ in this.nodes) {
			var _=this.nodes[$];
			if(!_.selected)_.select()
		}
	},
	
	changePalette:function ($) {
		var _=document.getElementById(this.activePalette);
		_.style.background="white";
		this.activePalette=$;
		_=document.getElementById(this.activePalette);
		_.style.background="#CCCCCC"
	},
	
	isInside:function (_,$) {
		//alert("_:"+_+" $:"+$+" w:"+this.w+" h:"+this.h);
		return _>0&&_<this.w&&$>0&&$<this.h
	},
	
	getTargetNode:function (_) {
		var $=_.getAttribute("id");
		if(!/^jpdl_\d+$/.test($))if(Jpdl.isIE) {
			if(_.tagName=="roundrect"&&_.parentNode.tagName=="group")$=_.parentNode.getAttribute("id");
			else if(_.tagName=="textbox"&&_.parentNode.parentNode.tagName=="group")$=_.parentNode.parentNode.getAttribute("id")
		}else if((_.tagName=="rect"||_.tagName=="text")&&_.parentNode.tagName=="g")$=_.parentNode.getAttribute("id");
		return this.nodes[$]
	},
	
	serial:function () {
		return new Jpdl.xml.JpdlSerializer(this).serialize()
	},
	
	clear:function () {
		for(var $ in this.nodes) {
			var _=this.nodes[$];
			if(typeof(_.nodeName)!="undefined")_.remove()
		}this.nodes={
		}
	},
	
	reset:function () {
		this.clear();
		Jpdl.cmd.CommandService.clear();
		for(var _ in Jpdl.ActivityMap) {
			var $=Jpdl.ActivityMap[_];
			if(typeof($.num)!="undefined"&&$.num>1)$.num=1
		}
	}
};
Jpdl.ModelDecorator={
	render:function ($) {
		$.el.onselectstart=function () {
			return false
		};
		if(Jpdl.isVml) {
			Jpdl.installVml();
			this.renderVml($)
		}else this.renderSvg($);
		$.tempRect=new Jpdl.TempRect();
		$.tempRect.render();
		$.tempRect.hide();
		$.tempLine=new Jpdl.TempLine();
		$.tempLine.render();
		$.tempLine.hide();
		$.tempPolyline=new Jpdl.TempPolyline();
		$.tempPolyline.render();
		$.tempPolyline.hide();
		$.textEditor=new Jpdl.TextEditor()
	},renderVml:function ($) {
		$.root=$.el
	},renderSvg:function (C) {
		var B=C.el.ownerDocument.createElementNS(Jpdl.svgns,"svg");
		B.setAttribute("id",Jpdl.id());
		B.setAttribute("width",C.w);
		B.setAttribute("height",C.h);
		C.root=B;
		C.el.appendChild(B);
		var _=B.ownerDocument.createElementNS(Jpdl.svgns,"defs");
		B.appendChild(_);
		var A=B.ownerDocument.createElementNS(Jpdl.svgns,"marker");
		A.setAttribute("id","markerArrow");
		A.setAttribute("markerWidth",4);
		A.setAttribute("markerHeight",4);
		A.setAttribute("refX",4);
		A.setAttribute("refY",2);
		A.setAttribute("orient","auto");
		var $=B.ownerDocument.createElementNS(Jpdl.svgns,"path");
		$.setAttribute("d","M 0 0 L 4 2 L 0 4 z");
		$.setAttribute("stroke","#909090");
		$.setAttribute("fill","#909090");
		A.appendChild($);
		_.appendChild(A)
	},resize:function ($) {
		if(Jpdl.isVml)this.resizeVml($);
		else this.resizeSvg($)
	},resizeVml:function ($) {
	},resizeSvg:function ($) {
		$.root.setAttribute("width",$.w);
		$.root.setAttribute("height",$.h)
	}
};
Jpdl.Node=Jpdl.extend(Jpdl.RoundRect,{
	right : {}, 
	constructor :function ($) {
		this.outputs=[];
		this.incomes=[];
		var w = $.w ;
		var h = $.h ;
		Jpdl.Node.superclass.constructor .call(this,$);
		if($.x) this.x = $.x ;
		if($.y) this.y = $.y ; 
		
		this.w= w ? w : 120; 
		this.h= h ? h : 50; 
		this.nodeResizer=new Jpdl.NodeResizer(this)
	},
	render:function () {
		Jpdl.Node.superclass.render.call(this);
		this.nodeResizer.render();
		this.nodeResizer.hide()
	},renderVml:function () {
		var A=document.createElement("v:group");
		A.style.left=this.x;
		A.style.top=this.y;
		A.style.width=this.w;
		A.style.height=this.h;
		A.setAttribute("coordsize",this.w+","+this.h);
		this.el=A;
		var _=document.createElement("v:roundrect");
		_.style.left="5px";
		_.style.top="5px";
		_.style.width=(this.w-10)+"px";
		_.style.height=(this.h-10)+"px";
		_.setAttribute("arcsize",0.2);
		_.setAttribute("fillcolor","#F6F7FF");
		_.setAttribute("strokecolor","#03689A");
		_.setAttribute("strokeweight","2");
		A.appendChild(_);
		this.rectEl=_;
		var $=document.createElement("v:textbox");
		$.style.left=(this.w/2-this.name.length*4)+"px";
		$.style.top=(this.h/2-10)+"px";
		$.style.textAlign="center";
		$.innerHTML=this.name;
		_.appendChild($);
		this.textEl=$
	},renderSvg:function () {
		var A=document.createElementNS(Jpdl.svgns,"g");
		A.setAttribute("transform","translate("+this.x+","+this.y+")");
		this.el=A;
		var _=document.createElementNS(Jpdl.svgns,"rect");
		_.setAttribute("x",5);
		_.setAttribute("y",5);
		_.setAttribute("width",(this.w-10)+"px");
		_.setAttribute("height",(this.h-10)+"px");
		_.setAttribute("rx",10);
		_.setAttribute("ry",10);
		_.setAttribute("fill","#F6F7FF");
		_.setAttribute("stroke","#03689A");
		_.setAttribute("stroke-width","2");
		A.appendChild(_);
		this.rectEl=_;
		var $=document.createElementNS(Jpdl.svgns,"text");
		$.setAttribute("x",this.w/2);
		$.setAttribute("y",this.h/2);
		$.setAttribute("text-anchor","middle");
		$.textContent=this.name;
		A.appendChild($);
		this.textEl=$
	},onRenderVml:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.style.position="absolute";
		this.el.style.cursor="pointer";
		Jpdl.model.root.appendChild(this.el)
	},onRenderSvg:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.setAttribute("cursor","pointer");
		Jpdl.model.root.appendChild(this.el)
	},move:function (A,B) {
		Jpdl.Node.superclass.move.call(this,A,B);
		
		this.nodeResizer.move(A,B);
		
		for(var $=0;$<this.incomes.length;$++) {
			var _=this.incomes[$];
			_.refresh()
		}
		
		for($=0;$<this.outputs.length;$++) {
			_=this.outputs[$];
			_.refresh()
		}
	},moveVml:function ($,_) {
		this.el.style.left=(this.x+$)+"px";
		this.el.style.top=(this.y+_)+"px"
	},moveSvg:function ($,_) {
		this.el.setAttribute("transform","translate("+(this.x+$)+","+(this.y+_)+")")
	},moveEnd:function (A,B) {
		Jpdl.Node.superclass.moveEnd.call(this,A,B);
		this.nodeResizer.moveEnd(A,B);
		for(var $=0;$<this.incomes.length;$++) {
			var _=this.incomes[$];
			_.refresh()
		}for($=0;$<this.outputs.length;$++) {
			_=this.outputs[$];
			_.refresh()
		}
	},remove:function () {
		for(var $=this.outputs.length-1;$>=0;$--) {
			var _=this.outputs[$];
			_.remove()
		}for($=this.incomes.length-1;$>=0;$--) {
			_=this.incomes[$];
			_.remove()
		}Jpdl.model.root.removeChild(this.el);
		this.nodeResizer.remove()
	},onSelect:function () {
		this.nodeResizer.show()
	},onDeselect:function () {
		this.nodeResizer.hide()
	},hideText:function () {
		this.textEl.style.display="none"
	},updateAndShowText:function ($) {
		this.name=$;
		if(Jpdl.isVml)this.textEl.innerHTML=$;
		else this.textEl.textContent=$;
		this.textEl.style.display=""
	},cancelEditText:function () {
		this.textEl.style.display=""
	},resize:function (_,A,D) {
		var E=this.x,C=this.y,$=this.w,B=this.h;
		if(_=="n") {
			C=C+D;
			B=B-D
		}else if(_=="s")B=B+D;
		else if(_=="w") {
			E=E+A;
			$=$-A
		}else if(_=="e")$=$+A;
		else if(_=="nw") {
			E=E+A;
			$=$-A;
			C=C+D;
			B=B-D
		}else if(_=="ne") {
			$=$+A;
			C=C+D;
			B=B-D
		}else if(_=="sw") {
			E=E+A;
			$=$-A;
			B=B+D
		}else if(_=="se") {
			$=$+A;
			B=B+D
		}if(Jpdl.isVml)this.resizeVml(E,C,$,B);
		else this.resizeSvg(E,C,$,B);
		return {
			x:E,y:C,w:$,h:B
		}
	},resizeVml:function (B,A,$,_) {
		this.el.style.left=B+"px";
		this.el.style.top=A+"px";
		this.el.style.width=$+"px";
		this.el.style.height=_+"px";
		this.el.coordsize=$+","+_;
		this.rectEl.style.width=($-10)+"px";
		this.rectEl.style.height=(_-10)+"px";
		this.textEl.style.left=($/2-this.name.length*4)+"px";
		this.textEl.style.top=(_/2-10)+"px";
		this.nodeResizer.resize(B,A,$,_)
	},resizeSvg:function (B,A,$,_) {
		this.el.setAttribute("transform","translate("+B+","+A+")");
		this.rectEl.setAttribute("width",($-10)+"px");
		this.rectEl.setAttribute("height",(_-10)+"px");
		this.textEl.setAttribute("x",$/2);
		this.textEl.setAttribute("y",_/2);
		this.nodeResizer.resize(B,A,$,_)
	},resizeEnd:function (A,C,D) {
		var $=this.resize(A,C,D);
		this.x=$.x;
		this.y=$.y;
		this.w=$.w;
		this.h=$.h;
		for(var _=0;_<this.incomes.length;_++) {
			var B=this.incomes[_];
			B.refresh()
		}for(_=0;_<this.outputs.length;_++) {
			B=this.outputs[_];
			B.refresh()
		}
	}
});
Jpdl.NodeResizer=function ($) {
	this.node=$
};
Jpdl.extend(Jpdl.NodeResizer,Jpdl.Shape,{
	renderVml:function () {
		var F=this.node.x,E=this.node.y,_=this.node.w,C=this.node.h,D=_/2,$=C/2,B=document.createElement("v:group");
		B.style.left=F;
		B.style.top=E;
		B.style.width=_;
		B.style.height=C;
		B.setAttribute("coordsize",_+","+C);
		this.el=B;
		var A=document.createElement("v:rect");
		A.filled="f";
		A.strokecolor="black";
		A.style.left="0px";
		A.style.top="0px";
		A.style.width=_+"px";
		A.style.height=C+"px";
		B.appendChild(A);
		this.rectEl=A;
		this.nEl=this.createItemVml(D,0,"n");
		this.sEl=this.createItemVml(D,C,"s");
		this.wEl=this.createItemVml(0,$,"w");
		this.eEl=this.createItemVml(_,$,"e");
		this.nwEl=this.createItemVml(0,0,"nw");
		this.neEl=this.createItemVml(_,0,"ne");
		this.swEl=this.createItemVml(0,C,"sw");
		this.seEl=this.createItemVml(_,C,"se")
	},createItemVml:function (B,A,_) {
		var $=document.createElement("v:rect");
		$.id=this.node.getId()+":"+_;
		$.fillcolor="black";
		$.style.cursor=_+"-resize";
		$.style.left=(B-2)+"px";
		$.style.top=(A-2)+"px";
		$.style.width="4px";
		$.style.height="4px";
		this.el.appendChild($);
		return $
	},renderSvg:function () {
		var F=this.node.x,E=this.node.y,_=this.node.w,C=this.node.h,D=_/2,$=C/2,B=document.createElementNS(Jpdl.svgns,"g");
		B.setAttribute("transform","translate("+F+","+E+")");
		this.el=B;
		var A=document.createElementNS(Jpdl.svgns,"rect");
		A.setAttribute("x",0);
		A.setAttribute("y",0);
		A.setAttribute("width",_);
		A.setAttribute("height",C);
		A.setAttribute("fill","none");
		A.setAttribute("stroke","black");
		this.rectEl=A;
		this.el.appendChild(A);
		this.nEl=this.createItemSvg(D,0,"n");
		this.sEl=this.createItemSvg(D,C,"s");
		this.wEl=this.createItemSvg(0,$,"w");
		this.eEl=this.createItemSvg(_,$,"e");
		this.nwEl=this.createItemSvg(0,0,"nw");
		this.neEl=this.createItemSvg(_,0,"ne");
		this.swEl=this.createItemSvg(0,C,"sw");
		this.seEl=this.createItemSvg(_,C,"se")
	},createItemSvg:function (B,A,_) {
		var $=document.createElementNS(Jpdl.svgns,"rect");
		$.setAttribute("id",this.node.getId()+":"+_);
		$.setAttribute("cursor",_+"-resize");
		$.setAttribute("x",B-2);
		$.setAttribute("y",A-2);
		$.setAttribute("width","4");
		$.setAttribute("height","4");
		$.setAttribute("fill","black");
		$.setAttribute("stroke","none");
		this.el.appendChild($);
		return $
	},moveVml:function ($,_) {
		this.el.style.left=this.node.x+$;
		this.el.style.top=this.node.y+_
	},moveSvg:function ($,_) {
		this.el.setAttribute("transform","translate("+(this.node.x+$)+","+(this.node.y+_)+")")
	},moveEnd:function ($,_) {
	},resize:function (B,A,$,_) {
		if(Jpdl.isVml)this.resizeVml(B,A,$,_);
		else this.resizeSvg(B,A,$,_)
	},resizeVml:function (B,A,$,_) {
		this.el.style.left=B+"px";
		this.el.style.top=A+"px";
		this.el.style.width=$+"px";
		this.el.style.height=_+"px";
		this.el.coordsize=$+","+_;
		this.rectEl.style.width=$+"px";
		this.rectEl.style.height=_+"px";
		this.nEl.style.left=($/2-2)+"px";
		this.sEl.style.left=($/2-2)+"px";
		this.sEl.style.top=(_-2)+"px";
		this.wEl.style.top=(_/2-2)+"px";
		this.eEl.style.left=($-2)+"px";
		this.eEl.style.top=(_/2-2)+"px";
		this.neEl.style.left=($-2)+"px";
		this.swEl.style.top=(_-2)+"px";
		this.seEl.style.left=($-2)+"px";
		this.seEl.style.top=(_-2)+"px"
	},resizeSvg:function (B,A,$,_) {
		this.el.setAttribute("transform","translate("+B+","+A+")");
		this.rectEl.setAttribute("width",$);
		this.rectEl.setAttribute("height",_);
		this.nEl.setAttribute("x",$/2-2);
		this.sEl.setAttribute("x",$/2-2);
		this.sEl.setAttribute("y",_-2);
		this.wEl.setAttribute("y",_/2-2);
		this.eEl.setAttribute("x",$-2);
		this.eEl.setAttribute("y",_/2-2);
		this.neEl.setAttribute("x",$-2);
		this.swEl.setAttribute("y",_-2);
		this.seEl.setAttribute("x",$-2);
		this.seEl.setAttribute("y",_-2)
	}
});
Jpdl.TempLine=Jpdl.extend(Jpdl.Line,{
	onRenderVml:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.style.position="absolute";
		this.el.style.cursor="pointer";
		this.el.setAttribute("strokeweight",2);
		this.el.setAttribute("strokecolor","red");
		Jpdl.model.root.appendChild(this.el);
		this.stroke=document.createElement("v:stroke");
		this.el.appendChild(this.stroke);
		this.stroke.setAttribute("strokecolor","black");
		this.stroke.setAttribute("dashstyle","dot");
		this.stroke.setAttribute("endArrow","Classic")
	},onRenderSvg:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.setAttribute("fill","white");
		this.el.setAttribute("stroke","red");
		this.el.setAttribute("stroke-width","2");
		this.el.setAttribute("cursor","pointer");
		this.el.setAttribute("stroke-dasharray","2");
		this.el.setAttribute("marker-end","url(#markerArrow)");
		Jpdl.model.root.appendChild(this.el)
	},update:function (A,B,$,_) {
		this.x1=A;
		this.y1=B;
		this.x2=$;
		this.y2=_;
		if(Jpdl.isVml)this.updateVml();
		else this.updateSvg()
	},updateVml:function () {
		this.el.setAttribute("from",this.x1+","+this.y1);
		this.el.setAttribute("to",this.x2+","+this.y2)
	},updateSvg:function () {
		this.el.setAttribute("x1",this.x1+"px");
		this.el.setAttribute("y1",this.y1+"px");
		this.el.setAttribute("x2",this.x2+"px");
		this.el.setAttribute("y2",this.y2+"px")
	}
});
Jpdl.TempPolyline=Jpdl.extend(Jpdl.Polyline,{
	onRenderVml:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.style.position="absolute";
		this.el.style.cursor="pointer";
		this.el.setAttribute("strokeweight",2);
		this.el.setAttribute("strokecolor","red");
		Jpdl.model.root.appendChild(this.el);
		this.stroke=document.createElement("v:stroke");
		this.el.appendChild(this.stroke);
		this.stroke.setAttribute("strokecolor","black");
		this.stroke.setAttribute("dashstyle","Dash");
		this.stroke.setAttribute("endArrow","Classic")
	},onRenderSvg:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.setAttribute("fill","white");
		this.el.setAttribute("stroke","red");
		this.el.setAttribute("stroke-width","2");
		this.el.setAttribute("cursor","pointer");
		this.el.setAttribute("stroke-dasharray","2");
		this.el.setAttribute("marker-end","url(#markerArrow)");
		Jpdl.model.root.appendChild(this.el)
	},update:function (A,B,$,_) {
		if(A==$)this.points=[[A,B],[A+50,B],[$+50,_],[$,_]];
		else if(B==_)this.points=[[A,B],[A,B+50],[$,_+50],[$,_]];
		else this.points=[[A,B],[A,_],[$,_]];
		if(Jpdl.isVml)this.updateVml();
		else this.updateSvg()
	},updateVml:function () {
		this.el.points.value=this.getPoint(0,0)
	},updateSvg:function () {
		this.el.setAttribute("points",this.getPoint(0,0))
	}
});
Jpdl.TempRect=Jpdl.extend(Jpdl.Rect,{
	onRenderVml:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.style.position="absolute";
		this.el.style.cursor="normal";
		this.el.setAttribute("fillcolor","#F6F6F6");
		Jpdl.model.root.appendChild(this.el);
		this.stroke=document.createElement("v:stroke");
		this.el.appendChild(this.stroke);
		this.stroke.setAttribute("strokecolor","black");
		this.stroke.setAttribute("dashstyle","dot")
	},onRenderSvg:function () {
		this.el.setAttribute("id",Jpdl.id());
		this.el.setAttribute("fill","#F6F6F6");
		this.el.setAttribute("stroke","black");
		this.el.setAttribute("stroke-width","1");
		this.el.setAttribute("cursor","normal");
		this.el.setAttribute("stroke-dasharray","2");
		Jpdl.model.root.appendChild(this.el)
	},update:function (B,A,$,_) {
		if($<0) {
			this.x=B+$;
			this.w=-$
		}else {
			this.x=B;
			this.w=$
		}if(_<0) {
			this.y=A+_;
			this.h=-_
		}else {
			this.y=A;
			this.h=_
		}if(Jpdl.isVml)this.updateVml();
		else this.updateSvg()
	},updateVml:function () {
		this.el.style.left=this.x+"px";
		this.el.style.top=this.y+"px";
		this.el.style.width=this.w+"px";
		this.el.style.height=this.h+"px"
	},updateSvg:function () {
		this.el.setAttribute("x",this.x+"px");
		this.el.setAttribute("y",this.y+"px");
		this.el.setAttribute("width",this.w+"px");
		this.el.setAttribute("height",this.h+"px")
	}
});
Jpdl.TextEditor=function () {
	var $=document.createElement("input");
	$.setAttribute("type","text");
	$.value="";
	$.style.position="absolute";
	$.style.left="0px";
	$.style.top="0px";
	$.style.width="0px";
	$.style.border="gray dotted 1px";
	$.style.background="white";
	$.style.display="none";
	$.style.zIndex=1000;
	document.body.appendChild($);
	this.el=$
};
Jpdl.TextEditor.prototype={
	showForNode:function ($) {
		this.el.style.left=(Jpdl.model.x+$.x+5)+"px";
		this.el.style.top=(Jpdl.model.y+$.y+$.h/2-10)+"px";
		this.el.style.width=($.w-10)+"px";
		this.el.value=$.name;
		this.el.style.display="";
		this.el.focus()
	},showForEdge:function ($) {
		var A=Jpdl.model.x+($.x1+$.x2)/2-$.name.length*4,_=Jpdl.model.y+($.y1+$.y2)/2-10;
		this.el.style.left=A+"px";
		this.el.style.top=_+"px";
		this.el.style.width=($.name.length*8)+"px";
		this.el.value=$.name;
		this.el.style.display="";
		this.el.focus()
	},hide:function () {
		this.el.style.display="none"
	}
};
Jpdl.xml.AbstractWrapper=function ($) {
	this.node=$
};
Jpdl.xml.AbstractWrapper.prototype={
	appendBuffer:function ($) {
		this.appendHeader($);
		this.appendAttributes($);
		var _=[];
		this.appendBody(_);
		this.appendFooter($,_)
	},
	
	appendHeader:function ($) {
		$.push("    <",this.node.nodeName)
	},
	
	appendAttributes:function ($) {
		var _=this.node;
		
		
		$.push(" name='",_.name,"' g='");
		$.push(_.x,",",_.y,",",_.w,",",_.h,"'");
	//	if(_.nodeName=="start"||_.nodeName=="end"||_.nodeName=="end-cancel"||_.nodeName=="end-error"||_.nodeName=="decision"||_.nodeName=="fork"||_.nodeName=="join")$.push(_.x,",",_.y,",",_.w,",",_.h,"'");
	//	else $.push((_.x-5),",",(_.y-5),",",(_.w-10),",",(_.h-10),"'")
		
		if(_.candidateExp)
			$.push(" candidate-users='",_.candidateExp,"' ");
		
		if(_.form)
			$.push(" form='",_.form,"' ");
		
		if(_.assignee)
			$.push(" assignee='",_.assignee,"' "); 
		
		if(_.nodeName == "decision") {
			if(_.decisionExp)
				$.push(" expr='",_.decisionExp,"' ");
		}
		
		if(_.nodeName == "sub-process") {
			if(_.subProcessKey)
				$.push(" sub-process-key='",_.subProcessKey,"' ");
		}
		
	},
	
	appendBody:function (_) {
		
		var node = this.node ;
		if(node.department || node.role || node.user) {
			_.push("        <assignment-handler class='"+defaultClass+"'> \n") ;
			
			if(node.department)
				_.push("       <field name='department'><string value='" + node.department + "'/></field> \n") ;
			
			if(node.role)
				_.push("       <field name='role'><string value='" + node.role + "'/></field> \n") ;
			
			if(node.user)
				_.push("       <field name='user'><string value='" + node.user + "'/></field> \n") ;
			
			_.push("        </assignment-handler> \n") ;
		}
		
		var msg = ""
		if(node.sletter) msg += "sLetter,"  ;
		if(node.sMsg)  msg += "sMsg," ;
		if(node.dLetter)  msg += "dLetter," ;
		if(node.dMsg)  msg += "dMsg," ;
		
		var handlerClass = defaultHandlerClass ;
		if(node.handlerClass)  handlerClass = node.handlerClass ;
		
		_.push("       <on event='start'> \n") ;
		_.push("       	<event-listener class='"+handlerClass+"'> \n") ;
		_.push("      	 <field name='eventName'> \n") ;
		_.push("       		<string value='start'/>   \n") ;
		_.push("       	 </field> \n") ;
		_.push("      	 <field name='msg'> \n") ;
		_.push("       		<string value='"+msg+"'/>   \n") ;
		_.push("       	 </field> \n") ;
		_.push("       	 </event-listener>   \n") ; 
		_.push("       </on>  \n") ;
		
		_.push("       <on event='end'> \n") ;
		_.push("       	<event-listener class='"+handlerClass+"'> \n") ;
		_.push("      	 <field name='eventName'> \n") ;
		_.push("       		<string value='end'/>   \n") ;
		_.push("       	 </field> \n") ;
		_.push("      	 <field name='msg'> \n") ;
		_.push("       		<string value='"+msg+"'/>   \n") ;
		_.push("       	 </field> \n") ;
		_.push("       	 </event-listener>   \n") ; 
		_.push("       </on>  \n") ;
		 
		if(node.rightClass) {
			_.push("        <assignment-handler class='"+node.rightClass+"'> \n") ;
			_.push("        </assignment-handler> \n") ;
		}
		
		if(node.decisionClass) {
			_.push("<handler class='"+node.decisionClass+"' />");
		}
		
		for(var $=0;$<this.node.outputs.length;$++) {
			var A=this.node.outputs[$];
			_.push("        <transition name='"+A.name+"' to='"+A.to.name+"'/>\n")
		}
	},
	
	appendFooter:function ($,_) {
		if(_.length==0)$.push("/>\n");
		else $.push(">\n",Jpdl.join(_),"    </",this.node.nodeName,">\n")
	}
};

Jpdl.xml.JpdlSerializer=function ($) {
	this.m=$;
	this.map={
		"process":Jpdl.xml.AbstractWrapper,"start":Jpdl.xml.AbstractWrapper,"end":Jpdl.xml.AbstractWrapper,"end-cancel":Jpdl.xml.AbstractWrapper,"end-error":Jpdl.xml.AbstractWrapper,"state":Jpdl.xml.AbstractWrapper,"hql":Jpdl.xml.AbstractWrapper,"sql":Jpdl.xml.AbstractWrapper,"java":Jpdl.xml.AbstractWrapper,"script":Jpdl.xml.AbstractWrapper,"esb":Jpdl.xml.AbstractWrapper,"task":Jpdl.xml.AbstractWrapper,"sub-process":Jpdl.xml.AbstractWrapper,"decision":Jpdl.xml.AbstractWrapper,"fork":Jpdl.xml.AbstractWrapper,"join":Jpdl.xml.AbstractWrapper
	}
};
Jpdl.xml.JpdlSerializer.prototype={
	serialize:function () {
		var $=[];
		this.appendToBuffer($);
		return Jpdl.join($)
	},
	appendToBuffer:function ($) {
		$.push("<?xml version='1.0' encoding='UTF-8'?>\n");
		$.push("<process");
		if(this.m.name)$.push(" name='"+this.m.name+"'");
		if(this.m.initial)$.push(" initial='"+this.m.initial+"'");
		if(this.m.key)$.push(" key='"+this.m.key+"'");
		if(this.m.version)$.push(" version='"+this.m.version+"'");
		if(this.m.description)$.push(" description='"+this.m.description+"'");
		$.push(" xmlns='http://jbpm.org/4.4/jpdl'>\n");
		this.appendBody($);
		$.push("</process>")
	},
	appendBody:function (_) {
		for(var $ in this.m.nodes) {
			var B=this.m.nodes[$],A=this.getWrapper(B);
			A.appendBuffer(_)
		}
	},
	getWrapper:function (A) {
		var _=A.nodeName,$=this.map[_];
		if(!$)return {
			appendBuffer:function () {
			}
		};
		else return new $(A)
	}
}

Jpdl.xml.JpdlParse = function (xml) {
	
	var xmlDoc = null ;
	
	var reg=new RegExp("&","g"); //创建正则RegExp对象    
	var xml = xml.replace(reg,"&amp;");    
	
	if(Jpdl.isIE) {
		xmlDoc = new ActiveXObject("Microsoft.XMLDOM"); 
		xmlDoc.loadXML(xml); 

	}else {
		var oParser=new DOMParser(); 
		xmlDoc=oParser.parseFromString(xml,"text/xml"); 
	}
	this.xmlDoc = xmlDoc ;
};

Jpdl.xml.JpdlParse.prototype={
		
	parse:function($model) {
		this.parseAttribute($model) ;
		this.parseNode($model);
		this.parseTransition($model);
	},
		
	parseAttribute:function($model){
		
		var root = this.xmlDoc.getElementsByTagName("process")[0] ;
		var $ = $model ; 
		$.key = root.getAttribute("key");
		$.name = root.getAttribute("name");
		$.initial = root.getAttribute("initial");
		$.version = root.getAttribute("version");
		$.description = root.getAttribute("description");
		this.root = root ;
	},
	
	parseNode:function($model) {
		var nodes = this.root.childNodes ;
		
		for(var i=0;i<nodes.length;i++) {
			var node = nodes[i] ;
			var g = node.getAttribute("g") ;
			var name = node.getAttribute("name") ;
			var form = node.getAttribute("form") ;
			var candidateExp = node.getAttribute("candidate-users") ;
			var assignee = node.getAttribute("assignee") ;
			var decisionExp = node.getAttribute("expr") ;
			var subProcessKey = node.getAttribute("sub-process-key") ;
			
			var tagName = node.tagName ;
			
			
			if(tagName == "sub-process") tagName = "subprocess" ;
			
			var editNode = Jpdl.ActivityMap.getConfig(tagName);
			
			if(g) {
				var gArr = g.split(",") ;
				editNode.name = name ;
				editNode.x = parseInt(gArr[0]) ;
				editNode.y = parseInt(gArr[1]) ;
				editNode.w = parseInt(gArr[2]) ;
				editNode.h = parseInt(gArr[3]) ;
			}
			
			if(form) editNode.form = form ;
			if(candidateExp) {
				editNode.rightType = "候选人" ;
				editNode.candidateExp = candidateExp ;
			}
			if(assignee){
				editNode.rightType = "待办人" ;
				editNode.assignee = assignee ;
			}
			
			if(decisionExp) {
				editNode.decisionType = "表达式" ;
				editNode.decisionExp = decisionExp ;
			}
			
			if(subProcessKey) {
				editNode.subProcessKey = subProcessKey ;
			}
			
			//自定义权限信息  
			
			var childs = node.childNodes ;
			for(var j=0;j<childs.length;j++) {
				var assignment = childs[j] ;
				var tagName = assignment.tagName ;
				var className = assignment.getAttribute("class") ;
				
				if(tagName == "transition") continue ;
				
				if(tagName == "assignment-handler" && className == defaultClass) {
					editNode.rightType = "自定义权限" ;
					
					var rightNodes = assignment.childNodes ;
					for(var k=0;k<rightNodes.length;k++) {
						var rightField = rightNodes[k] ;
						var rightName = rightField.getAttribute("name") ;
						var rightValue = rightField.childNodes[0].getAttribute("value") ;
						eval("editNode."+rightName+"= '"+rightValue +"'") ;
					}
				}else if(tagName == "assignment-handler" && className != defaultClass){
					editNode.rightType = "扩展类" ;
					editNode.rightClass = className ;
				}else if(node.tagName == "decision" && tagName == "handler") {
					editNode.decisionType = "调用类" ; 
					var className = assignment.getAttribute("class") ;
					editNode.decisionClass = className ;
				}else if(tagName == "on") {
					
					var eventLisner = assignment.childNodes[0] ;
					
					var fields = eventLisner.childNodes ;
					var handlerClass = eventLisner.getAttribute("class") ;
					if(handlerClass != defaultHandlerClass) editNode.handlerClass = handlerClass ;
					for(var k=0;k<fields.length;k++) {
						var field = fields[k] ;
						var fieldName = field.getAttribute("name") ;
						
						if(fieldName == "msg") {    
							//还原是否发送短信
							var msg = field.childNodes[0].getAttribute("value") ;
							if(msg != null && msg != "") {
								if(msg.indexOf("sLetter") > -1) {
									//给发起人发送站内短信
									document.getElementById("sLetter").checked = true  ;
									editNode.sLetter = true ;
								}
								
								if(msg.indexOf("sMsg") > -1) {
									//给发起人发送手机短信
									document.getElementById("sMsg").checked = true  ;
									editNode.sMsg = true ;
								}
								if(msg.indexOf("dLetter") > -1) {
									//给办理人发送站内短信
									document.getElementById("dLetter").checked = true  ;
									editNode.dLetter = true ;
								}
								if(msg.indexOf("dMsg") > -1) {
									//给办理人发送手机短信
									document.getElementById("dMsg").checked = true  ;
									editNode.dMsg = true ;
								}
							}
						}
					}
					
				}
			}
			
			var isNotSelectUserNodes = document.getElementById("isNotSelectUserNodes").value ;
			isNotSelectUserNodes = "|" + isNotSelectUserNodes + "|" ;
			if(isNotSelectUserNodes.indexOf("|"+name+"|") > -1){
				editNode.selectUser = "是" ;
			}else {
				editNode.selectUser = "否" ;
			}
			
			Jpdl.cmd.CommandService.execute(new Jpdl.cmd.EditNodeCmd(editNode));
		}
	},
	
	parseTransition:function($model){
		var nodes = this.root.childNodes ;
		for(var i=0;i<nodes.length;i++) {
			var node = nodes[i] ;
			
			var name = node.getAttribute("name") ;
			var childs = node.childNodes ;
			for(var j=0;j<childs.length;j++) {
				var transition = childs[j] ;
				
				var g = transition.getAttribute("g") ;
				var to = transition.getAttribute("to") ;
				var tagName = transition.tagName ;
				var showName = transition.getAttribute("name") ;
				var fromNode ;
				var toNode ;
				if(tagName == "transition") {
					//要连线
					//找下个节点
					var mNodes = Jpdl.model.nodes; //已经画好的节点
					for(var k in mNodes) {
						var mNode = mNodes[k] ;
						var mNodeName = mNode.name ;
						
						if(name == mNodeName) {
							fromNode = mNode ;
						}else if(to == mNodeName) {
							toNode = mNode ; 
						}
					}
					
					if(fromNode && toNode) {

						Jpdl.cmd.CommandService.execute(new Jpdl.cmd.AddEdgeCmd(fromNode,toNode,showName));
					}
						
				} 
			}
		}
		
	}
		
}
