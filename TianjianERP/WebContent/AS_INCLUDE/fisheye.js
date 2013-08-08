/**
 * Interface Elements for jQuery
 * Fisheye menu
 * 
 * http://interface.eyecon.ro
 * 
 * Copyright (c) 2006 Stefan Petre
 * Dual licensed under the MIT (MIT-LICENSE.txt) 
 * and GPL (GPL-LICENSE.txt) licenses.
 *
 */

/**
 * Build a Fisheye menu from a list of links
 *
 * @name Fisheye
 * @description Build a Fisheye menu from a list of links
 * @param Hash hash A hash of parameters
 * @option String items items selection
 * @option String container container element
 * @option Integer itemWidth the minimum width for each item
 * @option Integer maxWidth the maximum width for each item
 * @option String itemsText selection of element that contains the text for each item
 * @option Integer proximity the distance from element that make item to interact
 * @option String valign vertical alignment
 * @option String halign horizontal alignment
 *
 * @type jQuery
 * @cat Plugins/Interface
 * @author Stefan Petre
 */
jQuery.iFisheye = {
	build : function(options) {	
		return this.each(function() { 
			var el = this;
			el.fisheyeCfg = {
				items : jQuery(options.items, this),
				container: jQuery(options.container, this),
				pos : {x:$(this).position().left,y:$(this).position().top},
				itemWidth: options.itemWidth,
				itemsText: options.itemsText,
				proximity: options.proximity,
				valign: options.valign,
				halign: options.halign,
				maxWidth : options.maxWidth
			};
			jQuery.iFisheye.positionContainer(el, 0);
			jQuery(window).bind('resize', function() {
				el.fisheyeCfg.pos = $(el).position();
				jQuery.iFisheye.positionContainer(el, 0);
				jQuery.iFisheye.positionItems(el);
			});
			// position: fixed; scroll-Fix: start
			var newposition = function() {				
          		var getoffset = function() {
					var yoffset = 0, xoffset = 0;       
					if (typeof(window.pageYOffset) == 'number') {
						yoffset = window.pageYOffset;
						xoffset = window.pageXOffset;
					} else if (document.body && (document.body.scrollLeft || document.body.scrollTop)) {
						yoffset = document.body.scrollTop;
						xoffset = document.body.scrollLeft;				
					} else if (document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {				
						yoffset = document.documentElement.scrollTop;
						xoffset = document.documentElement.scrollLeft;				
					}
					return [xoffset, yoffset];
				}
				var oldoffset = getoffset();
				if (oldoffset[0] > 0)
						el.fisheyeCfg.pos.x = oldoffset[0];
				if (oldoffset[1] > 0)
						el.fisheyeCfg.pos.y = oldoffset[1];			
				jQuery(window).bind('scroll', function() {
					var offset = getoffset();
					if (oldoffset[1] < offset[1]) {		
						el.fisheyeCfg.pos.y = el.fisheyeCfg.pos.y - oldoffset[1] + offset[1];						
					} else {		
						el.fisheyeCfg.pos.y = el.fisheyeCfg.pos.y - oldoffset[1] + offset[1];
					}
					if (oldoffset[0] < offset[0]) {				
						el.fisheyeCfg.pos.x = el.fisheyeCfg.pos.x - oldoffset[0] + offset[0];						
					} else {					
						el.fisheyeCfg.pos.x = el.fisheyeCfg.pos.x - oldoffset[0] + offset[0];
					}
					oldoffset[1] = offset[1];
					oldoffset[0] = offset[0];
					jQuery.iFisheye.positionContainer(el, 0);
					jQuery.iFisheye.positionItems(el);
				});
			}			
			if (jQuery(el.fisheyeCfg.container).css('position') == 'fixed') {
				newposition();
			} else {			
				jQuery(el.fisheyeCfg.container).parents().each(function() {
					if (jQuery(this).css('position') == 'fixed') {
						newposition();
						return false;
					} 
				});	
			}
			// position: fixed; scroll-Fix: end
			jQuery.iFisheye.positionItems(el);
			el.fisheyeCfg.items.bind('mouseover', function() {
				jQuery(el.fisheyeCfg.itemsText, this).get(0).style.display = 'block';
			}).bind('mouseout', function() {
				jQuery(el.fisheyeCfg.itemsText, this).get(0).style.display = 'none';
			});
			jQuery(document).bind('mousemove', function(e) { 
			//	var pointer = jQuery.iUtil.getPointer(e);
				var toAdd = 0;
				if (el.fisheyeCfg.halign && el.fisheyeCfg.halign == 'center')
					var posx = e.pageX- el.fisheyeCfg.pos.x - (el.offsetWidth - el.fisheyeCfg.itemWidth * el.fisheyeCfg.items.size())/2 - el.fisheyeCfg.itemWidth/2;
				else if (el.fisheyeCfg.halign && el.fisheyeCfg.halign == 'right')
					var posx = e.pageX - el.fisheyeCfg.pos.x - el.offsetWidth + el.fisheyeCfg.itemWidth * el.fisheyeCfg.items.size();
				else 
					var posx = e.pageX - el.fisheyeCfg.pos.x;
				var posy = Math.pow(e.pageY- el.fisheyeCfg.pos.y - el.offsetHeight/2,2);
				el.fisheyeCfg.items.each(function(nr) {
					distance = Math.sqrt(Math.pow(posx - nr*el.fisheyeCfg.itemWidth, 2) + posy);
					distance -= el.fisheyeCfg.itemWidth/2;							
					distance = distance < 0 ? 0 : distance;
					distance = distance > el.fisheyeCfg.proximity ? el.fisheyeCfg.proximity : distance;
					distance = el.fisheyeCfg.proximity - distance;							
					extraWidth = el.fisheyeCfg.maxWidth * distance/el.fisheyeCfg.proximity;	
					
					try {
						this.style.width = el.fisheyeCfg.itemWidth + extraWidth ;
						this.style.left = el.fisheyeCfg.itemWidth * nr + toAdd ;
						toAdd += extraWidth;
					}catch(e){}
				});
				jQuery.iFisheye.positionContainer(el, toAdd);
			});
		})
	},	
	positionContainer : function(el, toAdd) {
		if (el.fisheyeCfg.halign)
			if (el.fisheyeCfg.halign == 'center')
				el.fisheyeCfg.container.get(0).style.left = (el.offsetWidth - el.fisheyeCfg.itemWidth * el.fisheyeCfg.items.size())/2 - toAdd/2 + 'px';
			else if (el.fisheyeCfg.halign == 'left')
				el.fisheyeCfg.container.get(0).style.left =  - toAdd/el.fisheyeCfg.items.size() + 'px';
			else if (el.fisheyeCfg.halign == 'right')
				el.fisheyeCfg.container.get(0).style.left =  (el.offsetWidth - el.fisheyeCfg.itemWidth * el.fisheyeCfg.items.size()) - toAdd/2 + 'px';
		el.fisheyeCfg.container.get(0).style.width = el.fisheyeCfg.itemWidth * el.fisheyeCfg.items.size() + toAdd + 'px';
	},	
	positionItems : function(el) {
		el.fisheyeCfg.items.each(function(nr) {
			this.style.width = el.fisheyeCfg.itemWidth + 'px';
			this.style.left = el.fisheyeCfg.itemWidth * nr + 'px';
		});
	}
};
jQuery.fn.Fisheye = jQuery.iFisheye.build;