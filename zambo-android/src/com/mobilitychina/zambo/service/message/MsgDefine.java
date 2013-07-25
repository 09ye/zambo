package com.mobilitychina.zambo.service.message;

public class MsgDefine {
	
//0000 0000   00   000000	
//head module type content
//head
 public static final int HEAD_SYS = 2;
 public static final int HEAD_BUSS = 1;
 
 public static final int TYPE_GENERAL = 1;
 public static final int TYPE_SUBSCRIBE = 2;
 public static final int TYPE_UNSUBSCRIBE = 3;
 public static final int TYPE_RES_SUBSCRIBE = 4;//订阅回推
 //module
 public static final int MODULE_MESSAGE = 1;
 public static final int MODULE_VERSION = 2;
 public static final int MODULE_CUSTEMER =3;
 public static final int MODULE_SYS = 4;
 //MODULE_MESSAGE
 public static final int CONTENT_UPDATE = 1;
 
 //MODULE_CUSTEMER
 public static final int CUSTEMER_UPDATE = 1;
 //MODULE_SYS
 public static final int HEART = 1;
 public static final int SENDED_OK = 2;
//content
 public static final int MSG_MESSAGE_UPDATE = (HEAD_BUSS<<24) + (MODULE_MESSAGE<<16) + (TYPE_GENERAL<<8 )+ CONTENT_UPDATE;
 //升级订阅
 public static final int MSG_CONFIG_UPDATE = (HEAD_BUSS<<24) + (MODULE_MESSAGE<<16) + (TYPE_SUBSCRIBE<<8) + CONTENT_UPDATE;
 public static final int MSG_CONFIG_UPDATE_RES_SUBSCRIB = (HEAD_BUSS<<24) + (MODULE_MESSAGE<<16) + (TYPE_RES_SUBSCRIBE<<8) + CONTENT_UPDATE;
 //用户更新
 public static final int MSG_CUSTEMER_UPDATE = (HEAD_BUSS<<24) + (MODULE_CUSTEMER<<16) + (TYPE_SUBSCRIBE<<8 )+ CUSTEMER_UPDATE;
 public static final int MSG_CUSTEMER_UPDATE_RES_SUBSCRIB = (HEAD_BUSS<<24) + (MODULE_CUSTEMER<<16) + (TYPE_RES_SUBSCRIBE<<8 )+ CUSTEMER_UPDATE;

 //心跳包
 public static final int MSG_MESSAGE_HEART = (HEAD_SYS<<24) + (MODULE_SYS<<16) + (TYPE_GENERAL<<8) + HEART;//心跳包
 public static final int MSG_MESSAGE_SENDED = (HEAD_SYS<<24) + (MODULE_SYS<<16) + (TYPE_GENERAL<<8 )+ SENDED_OK;//收到消息


}

