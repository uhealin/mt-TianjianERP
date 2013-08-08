/**
 * Poplayer when receive new message
 * core JS
 */

Ext.namespace("Ext.ux");
Ext.ux.ToastWindowMgr = {
    positions: [] 
};
Ext.ux.ToastWindow = Ext.extend(Ext.Window, {
    initComponent: function(){
          Ext.apply(this, {
              iconCls: this.iconCls || 'information',
            width: 250,
            height: 150,
            autoScroll: true,
            autoDestroy: true,
            plain: false,
            shadow:false
          });
        this.task = new Ext.util.DelayedTask(this.hide, this);
        Ext.ux.ToastWindow.superclass.initComponent.call(this);
    },
    setMessage: function(msg){
        this.body.update(msg);
    },
    setTitle: function(title, iconCls){
        Ext.ux.ToastWindow.superclass.setTitle.call(this, title, iconCls||this.iconCls);
    },
    onRender:function(ct, position) {
        Ext.ux.ToastWindow.superclass.onRender.call(this, ct, position);
    },
    onDestroy: function(){
        Ext.ux.ToastWindowMgr.positions.remove(this.pos);
        Ext.ux.ToastWindow.superclass.onDestroy.call(this);
    },
    afterShow: function(){
        Ext.ux.ToastWindow.superclass.afterShow.call(this);
        this.on('move', function(){
               Ext.ux.ToastWindowMgr.positions.remove(this.pos);
            this.task.cancel();}
        , this);
        this.task.delay(3000);
    },
    animShow: function(){
        this.pos = 0;
        while(Ext.ux.ToastWindowMgr.positions.indexOf(this.pos)>-1)
            this.pos++;
        Ext.ux.ToastWindowMgr.positions.push(this.pos);
        this.setSize(250,150);
        this.el.alignTo(document, "br-br", [ -0, -0-((this.getSize().height+10)*this.pos) ]);
        this.el.slideIn('b', {
            duration: 2,
            callback: this.afterShow,
            scope: this
        });    
    },
    animHide: function(){
           Ext.ux.ToastWindowMgr.positions.remove(this.pos);
        this.el.ghost("b", {
            duration: 2,
            remove: true,
         scope: this,
         callback: this.destroy
        });    
    }
});  

/*Ext.onReady(function(){
	new Ext.ux.ToastWindow({
		title: '提示窗口',
		html: '测试信息',
		iconCls: 'error'
	}).show(document);
});
*/

//右下角弹出新消息提示层
function popNewMsgLayer(senderName, messageContent){
	senderName=decodeURIComponent(senderName);
	messageContent=decodeURIComponent(messageContent);
	try{
	new Ext.ux.ToastWindow({
		title: '您有一条新消息，来自['+senderName+']',
		html: '<div style="margin:6px 0 0 5px">'+messageContent+'</div>'
	}).show(document);
	}catch(ex){alert(ex);}
}
