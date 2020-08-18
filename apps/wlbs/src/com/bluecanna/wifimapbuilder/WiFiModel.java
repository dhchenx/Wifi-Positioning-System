/**
 * 
 */
package com.bluecanna.wifimapbuilder;
 

/**
 * @author TungWahChan
 *
 */
public class WiFiModel {
	public WiFiModel(String params){
		this.Process(params);
	}
	public WiFiModel(){}
	private String add(String param,String key,String value){
		return param+"&"+key+"="+value;
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
		m=new String[5];
		v=new double[5];
		for(int i=0;i<m.length;i++){
			m[i]="";
			v[i]=0;
		}
		for(int i=0;i<kv.length;i++){
			KeyValue temp=new KeyValue(kv[i]);
			if(temp.key.equals("areaid"))
				areaid=temp.value;
			if(temp.key.equals("apname"))
				apname=temp.value;
			if(temp.key.equals("pointid"))
				pointid=temp.value;
			if(temp.key.equals("x"))
				x=Float.valueOf(temp.value);
			if(temp.key.equals("y"))
				y=Float.valueOf(temp.value);
			if(temp.key.equals("z"))
				z=Float.valueOf(temp.value);
			if(temp.key.equals("lgt"))
				lgt=Float.valueOf(temp.value);
			if(temp.key.equals("ltt"))
				ltt=Float.valueOf(temp.value);
			if(temp.key.equals("step"))
				step=Integer.valueOf(temp.value);
			if(temp.key.equals("appid"))
				appid=Integer.valueOf(temp.value);
			if(temp.key.equals("device"))
				device=temp.value;
			if(temp.key.equals("remark"))
				remark=temp.value;
			if(temp.key.equals("extend1"))
				extend1=temp.value;
			if(temp.key.equals("extend2"))
				extend2=temp.value;
			if(temp.key.equals("addtime"))
				addtime=temp.value;
			if(temp.key.equals("m1"))
				m[0]=(temp.value);
			if(temp.key.equals("v1"))
				v[0]=Double.valueOf(temp.value);
			if(temp.key.equals("m2"))
				m[1]=(temp.value);
			if(temp.key.equals("v2"))
				v[1]=Double.valueOf(temp.value);	
			if(temp.key.equals("m3"))
				m[2]=(temp.value);
			if(temp.key.equals("v3"))
				v[2]=Double.valueOf(temp.value);
			if(temp.key.equals("m4"))
				m[3]=(temp.value);
			if(temp.key.equals("v4"))
				v[3]=Double.valueOf(temp.value);	
			if(temp.key.equals("m5"))
				m[4]=(temp.value);
			if(temp.key.equals("v5"))
				v[4]=Double.valueOf(temp.value);
		}
	}
	public void setModel(String params){
		this.Process(params);
	}
	public String areaid="0";
	public String apname="[UNKNOWN]";
	public String pointid="[UNKNOWN]";
	public String[] m;
	public double[] v;
	public float x=0;
	public float y=0;
	public float z=0;
	public float lgt=0;
	public float ltt=0;
	public int step=0;
	public int appid=0;
	public String device="";
	public String remark="";
	public String extend1="";
	public String extend2="";
	public String addtime=null;
	public String getModel(){
		String str="";
		str+="apname="+apname;
		str=add(str, "pointid", pointid);
		str=add(str, "x",String.valueOf(x));
		str=add(str, "y", String.valueOf(y));
		str=add(str, "z", String.valueOf(z));
		str=add(str,"lgt",String.valueOf(lgt));
		str=add(str, "ltt", String.valueOf(ltt));
		str=add(str,"step",String.valueOf(step));
		str=add(str, "device", device);
		str=add(str, "extend1", extend1);
		str=add(str, "extend2", extend2);
		str=add(str, "areaid", String.valueOf(areaid));    	
      
		str=add(str, "addtime",addtime);
		str=add(str, "remark", remark);
		for(int i=0;i<m.length;i++){
			if(!m[i].equals("")){
				
			str=add(str,"m"+(i+1),m[i]);
			str=add(str,"v"+(i+1),String.valueOf(v[i]));
			}
		}
		return str;
	}
}
