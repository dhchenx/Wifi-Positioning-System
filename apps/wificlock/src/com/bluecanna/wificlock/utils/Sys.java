package com.bluecanna.wificlock.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bluecanna.wificlock.view.R;

 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

public class Sys {
	public static void p(String s) {
		System.out.println(s);
	}

	public static void showTips(Activity a, String s) {
		Toast.makeText(a, s, Toast.LENGTH_LONG).show();
	}
	public static void showShortTips(Activity a, String s) {
		Toast.makeText(a, s, Toast.LENGTH_SHORT).show();
	}
	public static View getCustomView(Activity activity, int resId) {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(resId, null);
		return view;
	}
	public static void showInfo(Context ctx, String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.home);
		builder.setPositiveButton("确定", null);
		AlertDialog ad = builder.create();
		ad.show();
	}

	public static String getTimeStamp() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
		return df.format(new Date());
	}

	public static String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		return df.format(new Date());
	}

	public static void openWiFiSetting(Activity c) {
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings",
				"com.android.settings.WirelessSettings");
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		c.startActivityForResult(intent, 0);
		/*
		 * 　com.android.settings.AccessibilitySettings 辅助功能设置
		 * 　　com.android.settings.ActivityPicker 选择活动
		 * 　　com.android.settings.ApnSettings APN设置
		 * 　　com.android.settings.ApplicationSettings 应用程序设置
		 * 　　com.android.settings.BandMode 设置GSM/UMTS波段
		 * 　　com.android.settings.BatteryInfo 电池信息
		 * 　　com.android.settings.DateTimeSettings 日期和坝上旅游网时间设置
		 * 　　com.android.settings.DateTimeSettingsSetupWizard 日期和时间设置
		 * 　　com.android.settings.DevelopmentSettings 应用程序设置=》开发设置
		 * 　　com.android.settings.DeviceAdminSettings 设备管理器
		 * 　　com.android.settings.DeviceInfoSettings 关于手机
		 * 　　com.android.settings.Display 显示——设置显示字体大小及预览
		 * 　　com.android.settings.DisplaySettings 显示设置
		 * 　　com.android.settings.DockSettings 底座设置
		 * 　　com.android.settings.IccLockSettings SIM卡锁定设置
		 * 　　com.android.settings.InstalledAppDetails 语言和键盘设置
		 * 　　com.android.settings.LanguageSettings 语言和键盘设置
		 * 　　com.android.settings.LocalePicker 选择手机语言
		 * 　　com.android.settings.LocalePickerInSetupWizard 选择手机语言
		 * 　　com.android.settings.ManageApplications 已下载（安装）软件列表
		 * 　　com.android.settings.MasterClear 恢复出厂设置
		 * 　　com.android.settings.MediaFormat 格式化手机闪存
		 * 　　com.android.settings.PhysicalKeyboardSettings 设置键盘
		 * 　　com.android.settings.PrivacySettings 隐私设置
		 * 　　com.android.settings.ProxySelector 代理设置
		 * 　　com.android.settings.RadioInfo 手机信息
		 * 　　com.android.settings.RunningServices 正在运行的程序（服务）
		 * 　　com.android.settings.SecuritySettings 位置和安全设置
		 * 　　com.android.settings.Settings 系统设置
		 * 　　com.android.settings.SettingsSafetyLegalActivity 安全信息
		 * 　　com.android.settings.SoundSettings 声音设置
		 * 　　com.android.settings.TestingSettings 测试——显示手机信息、电池信息、使用情况统计、Wifi
		 * information、服务信息 　　com.android.settings.TetherSettings 绑定与便携式热点
		 * 　　com.android.settings.TextToSpeechSettings 文字转语音设置
		 * 　　com.android.settings.UsageStats 使用情况统计
		 * 　　com.android.settings.UserDictionarySettings 用户词典
		 * 　　com.android.settings.VoiceInputOutputSettings 语音输入与输出设置
		 * 　　com.android.settings.WirelessSettings 无线和网络设置
		 */
	}

	public String[][] toT(String[][] t) {
		if (t == null)
			return null;
		String[][] tt = new String[t.length][];
		for (int i = 0; i < t.length; i++) {
			List<String> s = new ArrayList<String>();
			for (int j = 0; j < t[i].length; j++) {
				if (t[i][j] != null && !t[i][j].equalsIgnoreCase("null")
						&& !t[i][j].equals("")) {
					s.add(t[i][j]);
				}
			}
			String[] t1 = new String[s.size()];
			for (int j = 0; j < t1.length; j++) {
				t1[j] = s.get(j);
			}
			tt[i] = t1;
		}
		return tt;

	}

	public void printArray(String[][] t) {
		for (int i = 0; i < t.length; i++) {
			String s = "";
			for (int j = 0; j < t[i].length; j++) {
				s += t[i][j] + ",";
			}
			Log.d("t_data", s);
		}
	}

	public ScreenInfo getScreenInfo(Activity act) {
		return new ScreenInfo(act);
	}

	public class ScreenInfo {
		public ScreenInfo(Activity myactivity) {
			// 屏幕方面切换时获得方向
			if (myactivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// myactivity.setTitle("landscape");
				this.orientation = this.LANDSCAPE;
			}
			if (myactivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				// myactivity.setTitle("portrait");
				this.orientation = this.PORTRAIT;
			}
			// 获得屏幕大小1
			WindowManager manager = myactivity.getWindowManager();
			int width = manager.getDefaultDisplay().getWidth();
			int height = manager.getDefaultDisplay().getHeight();
			// 获得屏幕大小2
			DisplayMetrics dMetrics = new DisplayMetrics();
			myactivity.getWindowManager().getDefaultDisplay()
					.getMetrics(dMetrics);
			int screenWidth = dMetrics.widthPixels;
			int screenHeight = dMetrics.heightPixels;
			this.width = width;
			this.height = height;
			this.widthpx = screenWidth;
			this.heightpx = screenHeight;
		}

		public String LANDSCAPE = "landscape";
		public String PORTRAIT = "portrait";
		public String orientation = "";
		public int width = 0;
		public int height = 0;
		public int widthpx = 0;
		public int heightpx = 0;
	}

	private static PendingIntent makeMoodIntent(Activity act,Class<?> target, Bundle bd) {
		// The PendingIntent to launch our activity if the user selects this
		// notification. Note the use of FLAG_UPDATE_CURRENT so that if there
		// is already an active matching pending intent, we will update its
		// extras to be the ones passed in here.
		PendingIntent contentIntent = PendingIntent.getActivity(
				act,
				0,
				new Intent(act, target).setFlags(
						Intent.FLAG_ACTIVITY_NEW_TASK).putExtras(bd), 
						PendingIntent.FLAG_UPDATE_CURRENT);
		return contentIntent;
	}
	public static void notifyView(int uid,Activity act,Class<?>target,Bundle bd,int ico,String title,String text) {
		// Instead of the normal constructor, we're going to use the one with no
		// args and fill
		// in all of the data ourselves. The normal one uses the default layout
		// for notifications.
		// You probably want that in most cases, but if you want to do something
		// custom, you
		// can set the contentView field to your own RemoteViews object.
		Notification notif = new Notification();

		// This is who should be launched if the user selects our notification.
		notif.contentIntent = makeMoodIntent(act,target,bd);

		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		
		notif.tickerText = text;

		// the icon for the status bar
		notif.icon = ico;

		// our custom view
		RemoteViews contentView = new RemoteViews(act.getPackageName(),
				R.layout.notify);
		contentView.setTextViewText(R.id.textView1, text);
		contentView.setImageViewResource(R.id.img, R.drawable.home);
		notif.contentView = contentView;
 
		// we use a string id because is a unique number. we use it later to
		// cancel the
		// notification
		NotificationManager mNotificationManager = (NotificationManager) act
				.getSystemService("notification");
		mNotificationManager.notify(uid, notif);
	}
	
	public static void cancelNotify(Activity act,int uid){
		 ((NotificationManager) act.getSystemService("notification"))
         .cancel(uid);
	}
	
	public static void Notify( int uid,Activity act, int defaults, Class<?> cl,
			int ico,String title, String text,Bundle bd) {
		NotificationManager mNotificationManager = (NotificationManager) act
				.getSystemService("notification");
		// This method sets the defaults on the notification before posting it.
		// This is who should be launched if the user selects our notification.
		PendingIntent contentIntent = PendingIntent.getActivity(act, 0,
				new Intent(act, cl).putExtras(bd), PendingIntent.FLAG_UPDATE_CURRENT);
	

		// In this sample, we'll use the same text for the ticker and the
		// expanded notification

		final Notification notification = new Notification(ico, // the
																// icon
																// for
																// the
																// status
																// bar
				text, // the text to display in the ticker
				System.currentTimeMillis()); // the timestamp for the
												// notification

		notification.setLatestEventInfo(act, // the context to use
				title,
				// the title for the notification
				text, // the details to display in the notification
				contentIntent); // the contentIntent (see above)

		notification.defaults = defaults;

		mNotificationManager.notify(uid, // we use a string id
											// because it is a
											// unique
											// number. we use it
											// later to cancel the
											// notification
				notification);
	}
}
