package com.matech.framework.pub.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
  
	public abstract String name();
	public abstract String pk(); 
	public abstract boolean insertPk() default true;
	public abstract String[] excludeColumns() default {}; 
}
