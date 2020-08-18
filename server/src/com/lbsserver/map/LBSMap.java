package com.lbsserver.map;

import java.util.ArrayList;
import java.util.List;

import com.lbsserver.db.DBImp;

public class LBSMap {
	public class Node{
		private String NodeId="";
		private List<Node> nodes=null;
		public Node(String nodeid){
			this.NodeId=nodeid;
			this.nodes=new ArrayList<Node>();
		}
		public String getNodeId(){
			return this.NodeId;
		}
		public void addNode(Node node){
			this.nodes.add(node);
		}
		public int getNodeCount(){
			return this.nodes.size();
		}
		public List<Node> getNodes(){
			return this.nodes;
		}
		public Node getNode(int i){
			return this.nodes.get(i);
		}
	}
	
	public class Point{
		public Point(String id,double x,double y,double z){
			this.id=id;
			this.x=x;
			this.y=y;
			this.z=z;
		}
		String id="";
		public double x;
		public double y;
		public double z;
	}
	public class Size{
		public  Size(double width,double height){
			this.height=height;
			this.width=width;
		}
		public double height=0;
		public double width=0;
	}
	public abstract class Area{
		public String id="";
		public String getId(){
			return this.id;
		}
		public double getArea(){
			return 0;
		}
	}
	public class CircleArea extends Area{
		public CircleArea(Point GPoint,double R){
			this.GPoint=GPoint;
			this.R=R;
		}
		public Point GPoint=null;
		public double R=0;
		public double getArea(){
			return Math.PI*R*R;
		}
	}
	public class RectArea extends Area{
		public Size AreaSize=null;
		public Point LeftPoint=null;
		public RectArea(String id,Point LeftPoint,Size size){
			this.id=id;
			this.LeftPoint=LeftPoint;
			this.AreaSize=size;
		}
	}
	public String[][] getData(String sql){
		String dbname = "mosystem";
		String dbuser = "mosystem";
		String dbpass = "chendonghua";
		String dburl = "27.54.227.50:3306";
		DBImp db = new DBImp(dburl, dbname, dbuser, dbpass);
		return db.BinarySelect(sql, new String[]{"string","string","string"
		}, new String[]{"nodeid","leafid","distance"});
	}
	
	public class NodeConnection{
		public NodeConnection(String FromId,String ToId,double Distance){
			this.FromId=FromId;
			this.ToId=ToId;
			this.Distance=Distance;
		}
		public double Distance=0.0f;
		public String FromId=null;
		public String ToId=null;
	}
	
	//获取点之间连接的集合
	public List<NodeConnection> getChildNode(String areaid,String node){
		String select="select * from testmap where areaid='"+areaid+"' and nodeid='"+node+"' ";
		String[][] rec=this.getData(select);
		if(rec!=null){
			List<NodeConnection> nc=new ArrayList<NodeConnection>();
			for(int i=0;i<rec.length;i++){
				NodeConnection temp=new NodeConnection(node,rec[i][1],Double.valueOf(rec[i][2]));
				nc.add(temp);
			}
			return nc;
		}
		return null;
	}
	
	//获取某个区域的点集合
	public List<Point> getPoints(String areaid){
		String select="select * from testmap where areaid='"+areaid+"'";
		String dbname = "mosystem";
		String dbuser = "mosystem";
		String dbpass = "chendonghua";
		String dburl = "27.54.227.50:3306";
		DBImp db = new DBImp(dburl, dbname, dbuser, dbpass);
		String[][] data= db.BinarySelect(select, new String[]{"string","double","double","double"
		}, new String[]{"nodeid","x","y","z"});
		List<Point> points=new ArrayList<Point>();
		for(int i=0;i<data.length;i++){
			points.add(new Point(data[i][0],Double.valueOf(data[i][1]),Double.valueOf(data[i][2])                                                                                  ,Double.valueOf(data[i][3])));
		}
		return points;
	}
	//获取某一个点的坐标
	public Point getSiteLocation(String areaid,String nodeid){
		String select="select * from sites where nodeid='"+nodeid+"' and areaid='"+areaid+"' ";
		String dbname = "mosystem";
		String dbuser = "mosystem";
		String dbpass = "chendonghua";
		String dburl = "27.54.227.50:3306";
		DBImp db = new DBImp(dburl, dbname, dbuser, dbpass);
		String[][] data= db.BinarySelect(select, new String[]{"string","double","double","double"
		}, new String[]{"nodeid","x","y","z"});
		List<Point> points=new ArrayList<Point>();
		for(int i=0;i<data.length;i++){
			points.add(new Point(data[i][0],Double.valueOf(data[i][1]),Double.valueOf(data[i][2])                                                                                  ,Double.valueOf(data[i][3])));
		}
		if(points!=null&points.size()>=1){
			return points.get(0);
		}else
			return null;
	}
	
	public void test(double left_top_x,double left_top_y,
	double width,double height,double p_x,double p_y
	){
		LBSMap.RectArea ra=
			new LBSMap.RectArea("myarea",new Point("leftpoint",left_top_x,left_top_y,0.0),new Size(width,height));
	
	}
	public class InputL{
		public InputL(double left_top_x,double left_top_y,
				double width,double height,double p_x,double p_y){
			
		}
	}
	public InputL transLocation(InputL inputl){
		
		return null;
	}
	
	
	
	public static void main(String[] args){
		/*
		LBSMap lbsMap=new LBSMap();
		List<NodeConnection> list=lbsMap.getChildNode("5f", "n1");
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i).ToId);
		}
		*/
		
	}
}
