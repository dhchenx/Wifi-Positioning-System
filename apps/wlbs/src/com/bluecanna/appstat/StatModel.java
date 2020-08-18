package com.bluecanna.appstat;
public class StatModel {
	public StatModel(){}
	public StatModel(String param){
		this.Process(param);
	}
	public void setModel(String param){
		this.Process(param);
	}
	private String add(String param,String key,String value){
		return param+"&"+key+"="+value;
	}
	public String getModel(){
		String str="";
		str= "IMEI="+ this.IMEI;
		str=add(str, "mobileId", this.mobileId);
		str=add(str, "action", this.action);
		str=add(str, "actionTime", actionTime);
		str=add(str, "system", system);
		str=add(str, "sourceId", this.sourceId);
		str=add(str, "appPriv", this.appPriv);
		str=add(str, "device", device);
		str=add(str, "screen",this.screen);
		str=add(str, "appId", appId);
		str=add(str, "userId", this.userId);
		str=add(str, "actionResult", this.actionResult);
		
		return str;
	}
	
	private class KeyValue{
		public KeyValue(String p){
			try{
				String[] s=p.split("=");
				if(s.length==2){
					key=s[0];
					value=p.replace( s[0]+"=","");
				}else{
					key=p;
					value="";
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		public String key="";
		public String value="";
	}

	private void Process(String params){
		String[] kv=params.split("&");
		for(int i=0;i<kv.length;i++){
			KeyValue temp=new KeyValue(kv[i]);
			if(temp.key.equals("IMEI"))
				IMEI=temp.value;
			if(temp.key.equals("mobileId"))
				mobileId=temp.value;
			if(temp.key.equals("userId"))
				userId=temp.value;
			if(temp.key.equals("action"))
				action=temp.value;
			if(temp.key.equals("actionTime"))
				actionTime=temp.value;
			if(temp.key.equals("system"))
				system=temp.value;
			if(temp.key.equals("device"))
				device=temp.value;
			if(temp.key.equals("appId"))
				appId=temp.value;
			if(temp.key.equals("appVersion"))
				appVersion=temp.value;
			if(temp.key.equals("sourceId"))
				sourceId=temp.value;
			if(temp.key.equals("appPriv"))
				appPriv=temp.value;
			if(temp.key.equals("screen"))
				screen=temp.value;
		}
	}
	
	public String IMEI="";//�ֻ�IMEI
	public String mobileId="";//�����ֻ����ռ��ֻ�
	public String userId="";//�ռ� �û�ID
	public String action="";//����
	public String actionTime="";//����ʱ��
	public String actionResult="";	//�������
	public String system="";//�û���ϵͳ
	public String device="";//�û����ƶ��豸�ͺ�
	public String appId="1.0.1";//Ӧ�õ�ID
	public String appVersion="";//Ӧ�õ�version
	public String sourceId="";//���ĸ��ط����ص�Ӧ��
	public String appPriv="WiFiMapBuilder";//Ӧ��˽����Ϣ
	public String screen="";
}
