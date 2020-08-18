package com.lbsserver.map;

public class LBSData {
	private class Par {
		   private String id;
		   private Object[] list;
		   private String[] key;
		   private int max=20;
		   private int cur=0;
		   public Par(String id){
			   this.id=id;
			   list=new Object[max];
			   key=new String[max];
		   }
		   //public int length=getLen();
		   public int getLen()
		   {
			   return cur;
		   }
		   public int add(String key, Object o){
			   if(cur>=max)
				   return -1;
			   list[cur]=o;
			   this.key[cur]=key;
			   cur++;
			   return cur;
		   }
		   public int remove(String key){
			   int a=getIndex(key);
			   if(a==-1)return -1;
			   for(int i=a+1;i<cur;i++){
				   list[i-1]=list[i];
				   this.key[i-1]=this.key[i];
			   }
			   cur--;
			   return 0;
		   }
		   public int set(String key,Object o){
			   for(int i=0;i<cur;i++){
				   if(this.key[i].equals(key)){
					   list[i]=o;
					   return 0;
				   }
			   }
			   return -1;
		   }
		   public int set(int i,Object o){
			   if(i<max&&i>=0){
			   list[i]=o;
			   return 0;
			   }
			   else 
				   return -1;
		   }
		   
		   public Object get(String key){
			   for(int i=0;i<cur;i++){
				   if(this.key[i].equals(key)){
					   return list[i];
				   }
			   }
			   return null;
		   }
		   public int getIndex(String key){
			   for(int i=0;i<cur;i++){
				   if(this.key[i].equals(key)){
					   return i;
				   }
			   }
			   return -1;
		   }
		   public String getKey(int i){
			   if(i>=cur||i<0)return null;
			   return key[i];
		   }
		   public Object get(int i){
			   if(i>=cur||i<0)return null;
			   return list[i];
		   }
		   public boolean isEmpty(){
			   if(cur==0)return true;
			   else return false;
		   }
		   public boolean isFull(){
			   if(cur==max){
				   return true;
			   }else
				   return false;
		   }
	}
	private Par parlist=null;
	public AreaInfo areainfo=null;
	public Point mypoint=null;
	//获取服务器返回结果之后处理生成定位类
	public LBSData(String params){
		try{
			params=params.trim();
		parlist=new Par("lbsdata");
		String[] str=params.split("&");
		if(str!=null)
		for(int i=0;i<str.length;i++){
			String[] temp=str[i].split("=");
			parlist.add(temp[0], temp[1]);
		}
		areainfo=new AreaInfo();
		areainfo.id=(String)parlist.get("areaid");
		areainfo.LeftPoint=new Point("leftpoint",
				Double.valueOf((String)parlist.get("left_top_x")),
				Double.valueOf((String)parlist.get("left_top_y")),
				Double.valueOf((String)parlist.get("left_top_z")));
		areainfo.width=Double.valueOf((String)parlist.get("width"));
		areainfo.height=Double.valueOf((String)parlist.get("height"));
		mypoint=new Point((String)parlist.get("nodeid"),
				Double.valueOf((String)parlist.get("x")),
				Double.valueOf((String)parlist.get("y")),
				Double.valueOf((String)parlist.get("z"))
		);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public Point getPoint(){
		return mypoint;
	}
	public AreaInfo getArea(){
		return areainfo;
	}
	public class Point{
		public Point(String id,double x,double y,double z){
			this.id=id;
			this.x
			=x;
			this.y=y;
			this.z=z;
		}
		public String id="";
		public double x=0;
		public double y=0;
		public double z=0;
	}
	public class AreaInfo{
		public String id="";
		public Point LeftPoint=null;
		public double height=0;
		public double width=0;
	}
	private String data="";
	//服务器发送数据是将相关数据封装成可在web传输的数据
	public LBSData(String areaid,double left_top_x,double left_top_y,double left_top_z,
			double width,double height,String nodeid,double x,double y,double z
	){
		String par="";
		par+="areaid="+areaid+"&";
		par+="left_top_x="+left_top_x+"&";
		par+="left_top_y="+left_top_y+"&";
		par+="left_top_z="+left_top_z+"&";
		par+="x="+x+"&";
		par+="y="+y+"&";
		par+="z="+z+"&";
		par+="width="+width+"&";
		par+="nodeid="+nodeid+"&";
		par+="height="+height;
		data=par;
	}
	public String getData(){
		return this.data;	
	}
	
}
