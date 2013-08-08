

var ExtButtonPanel = Ext.extend(Ext.Panel, {
	
    layout:'table',
    defaultType: 'button',
    baseCls: 'x-plain',
    cls: 'btn-panel',
    menu: undefined,
    split: true, 
    

    constructor: function(options){
    	
    	Ext.applyIf(options,{
    		columns:2,
    		colspan:3, 
    		width:250
    	})   
    	
    	var buttons = options.items ;
        for(var i = 0, b; b = buttons[i]; i++){
            b.menu = this.menu;
            b.enableToggle = this.enableToggle; 
            b.split = this.split;
            b.arrowAlign = this.arrowAlign;
            b.style = options.style || {display: 'inline', margin : '10 15 0 0px' } ;
        }
        var items = buttons;  

        ExtButtonPanel.superclass.constructor.call(this, {
	            items: items,
	            renderTo:options.renderTo,
	            region:options.region,
	            height:options.height,  
	            width:options.width,
	            layoutConfig: {
	                columns:options.columns
	            }
	        });
    	}
 });


Ext.override(Ext.Toolbar,{
	 constructButton : function(item){
	   if(typeof(btnDenyRight) != "undefined" && btnDenyRight != "") {
		   var rightStr = "," + btnDenyRight + "," ;
		   var text = "," + item.text + "," ;
		   var reg1=new RegExp(" ","g"); 
		   rightStr = rightStr.replace(reg1,""); 
		   text = text.replace(reg1,""); 
		   if(rightStr.indexOf(text) > -1) {
			   item.disabled = true;
		   }
 	   }
       var b = item.events ? item : this.createComponent(item, item.split ? 'splitbutton' : this.defaultType);
       return b;
   }
})