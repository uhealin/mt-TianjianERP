package com.matech.audit.service.user.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_user_fav",pk="uuid")
public class UserFavVO {

	
	
		 protected String uuid ;
		 protected String userid ;
		 protected String name ;
		 protected String fav_user_ids ;


		 public String getUuid(){ return this.uuid; }
		 public void setUuid(String uuid){ this.uuid=uuid; }
		 public String getUserid(){ return this.userid; }
		 public void setUserid(String userid){ this.userid=userid; }
		 public String getName(){ return this.name; }
		 public void setName(String name){ this.name=name; }
		 public String getFav_user_ids(){ return this.fav_user_ids; }
		 public void setFav_user_ids(String fav_user_ids){ this.fav_user_ids=fav_user_ids; }

}
