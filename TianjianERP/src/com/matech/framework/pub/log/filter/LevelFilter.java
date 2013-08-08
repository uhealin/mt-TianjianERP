/**
 * 
 */
package com.matech.framework.pub.log.filter;

import org.apache.log4j.Level;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author bill
 *
 */
public class LevelFilter extends Filter {

	
	  boolean acceptOnMatch = false;

	  Level levelMin;
	  Level levelMax;

	 
	  /**
	          返回过滤器处理结果
	   */
	  public int decide(LoggingEvent event) {
		  
		  
	    if(this.levelMin != null) {
	      if (event.getLevel().isGreaterOrEqual(levelMin) == false) {
	        return Filter.DENY;
	      }
	    }

	    if(this.levelMax != null) {
	      if (event.getLevel().toInt() > levelMax.toInt()) {
	        return Filter.DENY;
	      }
	    }

	    if (acceptOnMatch) {
	      // this filter set up to bypass later filters and always return
	      // accept if level in range
	      return Filter.ACCEPT;
	    }
	    else {
	      // event is ok for this filter; allow later filters to have a look..
	      return Filter.NEUTRAL;
	    }
	  }

	
	  public Level getLevelMax() {
	    return levelMax;
	  }
	
	  public Level getLevelMin() {
	    return levelMin;
	  }
	
	  public boolean getAcceptOnMatch() {
	    return acceptOnMatch;
	  }

	  public void setLevelMax(Level levelMax) {
	    this.levelMax =  levelMax;
	  }


	  public void setLevelMin(Level levelMin) {
	    this.levelMin =  levelMin;
	  }

	  public void setAcceptOnMatch(boolean acceptOnMatch) {
	    this.acceptOnMatch = acceptOnMatch;
	  }

}
