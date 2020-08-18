package com.bluecanna.wifimapbuilder;  
  
import com.bluecanna.appstat.Stat;
import com.bluecanna.appstat.StatDB;
import com.bluecanna.appstat.StatModel;
import com.bluecanna.appstat.StatUtils;

import android.app.Activity;  
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;  
import android.graphics.Color;  
import android.graphics.drawable.GradientDrawable;  
import android.graphics.drawable.GradientDrawable.Orientation;  
import android.os.Bundle;  
import android.util.Log;  
import android.view.GestureDetector;  
import android.view.GestureDetector.OnGestureListener;  
import android.view.KeyEvent;
import android.view.LayoutInflater;  
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;  
import android.view.View;  
import android.view.Window;
import android.view.animation.AnimationUtils;  
import android.widget.ViewFlipper;  
  
public class MainActivity extends Activity implements OnGestureListener{  
      
    private GestureDetector detector;  
    private ViewFlipper flipper;  
    private final int HELPFILP_RESULT = 106;  
    Intent getMainActivity = null;  
    int count = 1;  
    
    public void CheckDead(boolean tt){
    	   try{
        	   String time=Tools.getTimeStamp();
        	   long t=Long.valueOf(time);
        	   if(t>Config.deadline){
        		  Alerts.showAlert("即将关闭", "您好，你当前的版本属于测试免费版本，已过期，请访问www.bluecanna.com获取新的版本。", this);
        			if(tt==true){
        		  android.os.Process.killProcess(android.os.Process
    						.myPid());
    				System.exit(0); 
        			}
        	   }
           }catch(Exception e){
        	   e.printStackTrace();
           }
            
    }
    
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        
        this.CheckDead(false);
    
        
     WifiDB wdb=new WifiDB(this);
     Stat.wdb=wdb;
        drawBackground();  
        LayoutInflater inflater = LayoutInflater.from(this);  
        final View layout = inflater.inflate(R.layout.view_flipper, null);  
        setContentView(layout);  
        flipper = (ViewFlipper) findViewById(R.id.view_flipper);  
        detector = new GestureDetector(this);  
        setTitle("WLAN定位 - 功能展示("+(1)+"/4) - 左滑查看");
        Stat.ActionStat(this, "Login");
        Stat.TryUploadData();
    }  
    public void Stat(){

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){ 
    	super.onCreateOptionsMenu(menu);
 	   	MenuInflater inflater = getMenuInflater();
 	  inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){ 
    	this.CheckDead(true);
    	if(item.getItemId()==R.id.menu_action_icon1){
    	   	Alerts.showAlert(
    				"关于WLAN定位HD 1.0",
    				Config.getAppDesc(),
    				this);
    	}
  
    	return super.onOptionsItemSelected(item);
    }
    
    
    public void drawBackground()  
    {  
        GradientDrawable grad = new GradientDrawable(   
                   Orientation.TL_BR,  
                   new int[] {Color.rgb(105, 0, 127),  
                           Color.rgb(0,100, 255),  
                           Color.rgb(127, 0, 255),  
                           Color.rgb(127, 127, 2),  
                           Color.rgb(100, 255, 255),  
                           Color.rgb(255, 255, 15)}   
        );   
  
        this.getWindow().setBackgroundDrawable(grad);  
    }  
      
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
    	this.CheckDead(true);
        return this.detector.onTouchEvent(event);  
    }  
  
    public boolean onDown(MotionEvent arg0) {  
        // TODO Auto-generated method stub  
        return false;  
    }  
  
    public boolean onFling(MotionEvent e1, MotionEvent e2, float arg2,  
            float arg3) {  
    	this.CheckDead(true);
        if (e1.getX() - e2.getX() > 5) {  
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,  
                    R.anim.push_left_in));  
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,  
                    R.anim.push_left_out));  
            if (count < 4) {  
                this.flipper.showNext(); 
                setTitle("WLAN定位 - 功能展示("+(count+1)+"/4) - 左滑查看");
                count++;  
            }else{
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setTitle("开始使用");
    			builder.setMessage("展示完毕，请问是否立即使用?");
    		 builder.setIcon(R.drawable.home);
    			builder.setPositiveButton("嗯",
    					new DialogInterface.OnClickListener() {
    						@Override
    						public void onClick(DialogInterface dialog, int which) {
    							Intent intent=new Intent(MainActivity.this,WifiListActivity.class);
    			    			 startActivity(intent);
    			    			 finish();
    						}
    					});
    			builder.setNegativeButton("稍后再说",
    					new DialogInterface.OnClickListener() {
    						@Override
    						public void onClick(DialogInterface dialog, int which) {

    						}
    					});
    			builder.show();
            	 
            }
  
            return true;  
        } else if (e1.getX() - e2.getX() < -5) {  
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,  
                    R.anim.push_right_in));  
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,  
                    R.anim.push_right_out));  
            if (count > 1) {  
            	setTitle("WLAN定位 - 功能展示("+(count-1)+"/4) - 左滑查看");
                this.flipper.showPrevious();  
                count--;  
            } else{
            	/*
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setTitle("关闭WiFi定位");
    			builder.setMessage("确定关闭本程序?");
    			builder.setIcon(R.drawable.home);
    			builder.setPositiveButton("关闭",
    					new DialogInterface.OnClickListener() {
    						@Override
    						public void onClick(DialogInterface dialog, int which) {

    							Intent startMain = new Intent(Intent.ACTION_MAIN);
    							startMain.addCategory(Intent.CATEGORY_HOME);
    							startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    							startActivity(startMain);
    							finish();
    							android.os.Process.killProcess(android.os.Process
    									.myPid());
    							System.exit(0);

    						}
    					});

    			builder.setNegativeButton("取消",
    					new DialogInterface.OnClickListener() {
    						@Override
    						public void onClick(DialogInterface dialog, int which) {

    						}
    					});
    			builder.show();
*/
            	Intent intent=new Intent(MainActivity.this,WifiListActivity.class);
   			 startActivity(intent);
   			 finish();
            }
            return true;  
        }  
        return true;  
    }  
  
    public void onLongPress(MotionEvent arg0) {  
        // TODO Auto-generated method stub  
    	Alerts.showAlert(
				"关于WLAN定位HD 1.0",
				Config.getAppDesc(),
				this);
    }  
  
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,  
            float arg3) {  
        // TODO Auto-generated method stub  
        return false;  
    }  
  
    public void onShowPress(MotionEvent arg0) {  
        // TODO Auto-generated method stub  
          
    }  
  
    public boolean onSingleTapUp(MotionEvent arg0) {  
        // TODO Auto-generated method stub  
        return false;  
    } 
    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			this.CheckDead(true);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("关闭WLAN定位");
			builder.setMessage("确定关闭本程序?");
			builder.setIcon(R.drawable.home);
			builder.setPositiveButton("关闭",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Stat.ActionStat(MainActivity.this, "Logout");
/*
							Intent startMain = new Intent(Intent.ACTION_MAIN);
							startMain.addCategory(Intent.CATEGORY_HOME);
							startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(startMain);
							finish();
							*/
							android.os.Process.killProcess(android.os.Process
									.myPid());
							System.exit(0);

						}
					});

			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			builder.show();

		}
		return super.onKeyDown(keyCode, event);
	}
}  