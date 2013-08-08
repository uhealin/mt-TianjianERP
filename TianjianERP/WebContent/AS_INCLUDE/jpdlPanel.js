
Ext.JpdlPanel = Ext.extend(Ext.Panel, {
    initComponent: function() {
    	Ext.JpdlPanel.superclass.initComponent.call(this);
        this.on('bodyresize', function(p, w, h) {
            var b = p.getBox();
            this.jpdlCanvas.setX(b.x);
            this.jpdlCanvas.setWidth(b.width - 150);
            this.jpdlCanvas.setHeight(b.height);
            this.jpdlPalette.setX(b.x + this.jpdlCanvas.getWidth());
            this.jpdlPalette.setHeight(b.height);
            this.jpdlModel.resize(b.x, b.y, b.width - 150, b.height);
        });
    },

    afterRender: function() {
        Ext.JpdlPanel.superclass.afterRender.call(this);
        var box = this.getBox();

        Ext.DomHelper.append(this.body, [{
            id: '_jpdl_palette',
            tag: 'div',
            cls: 'jpdlpalette',
            html: this.createPalette()
        },{
            id: '_jpdl_canvas',
            tag: 'div',
            cls: 'jpdlcanvas'
        }]);

        this.jpdlCanvas = Ext.get('_jpdl_canvas');
        this.jpdlPalette = Ext.get('_jpdl_palette');
        this.jpdlModel = new Jpdl.Model({
            id: '_jpdl_canvas'
        });
        
    },

    createPalette: function() {
        // header
        var html = '<div class="dragHandle move">'
            +'</div>';
        html += '<ul>' ;
        html += this.createPart('Components|流程组件',['select|选择','transition-straight|连线','start|开始','end|结束',
                                'state|状态','task|任务','subprocess|子流程','decision|分支','fork|分流','join|汇流 ']);
        html += '</ul>' ;
        return html;
    },

    createPart: function(title, items) {
        var html = '<li class="paletteBar"><div unselectable="on">' + title.split("|")[1] + '</div><ul>';
        for (var i = 0; i < items.length; i++) {
        	var temp = items[i].split("|");
            var t = temp[0];
            var text = temp[1];
            html += '<li id="' + t + '" class="paletteItem ' + t + '">'
                + '<span style="line-height:35px;" class="paletteItem-' + t + '" unselectable="on">' + text + '</span>'
                + '</li>';
        }
        html += '</ul></li>';
        return html;
    }
});
Ext.reg('jpdlpanel', Ext.JpdlPanel);

