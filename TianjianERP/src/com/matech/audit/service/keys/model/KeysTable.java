package com.matech.audit.service.keys.model;

public class KeysTable {
  private String autoid;
  private String key1;
  private String key2;
  public KeysTable() {
  }
  public String getKey1(){
    return this.key1;
  }
  public void setKey1(String key1){
    this.key1 = key1;
  }
  public String getKey2(){
    return this.key2;
  }
  public void setKey2(String key2){
    this.key2 = key2;
  }
  public String getAutoid(){
    return this.autoid;
  }
  public void setAutoid(String autoid){
    this.autoid = autoid;
  }

}