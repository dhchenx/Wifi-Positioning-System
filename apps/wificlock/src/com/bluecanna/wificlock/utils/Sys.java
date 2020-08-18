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
		builder.setPositiveButton("ȷ��", null);
		AlertDialog ad = builder.create();
		ad.show();
	}

	public static String getTimeStamp() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// �������ڸ�ʽ
		return df.format(new Date());
	}

	public static String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ
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
		 * ��com.android.settings.AccessibilitySettings ������������
		 * ����com.android.settings.ActivityPicker ѡ��
		 * ����com.android.settings.ApnSettings APN����
		 * ����com.android.settings.ApplicationSettings Ӧ�ó�������
		 * ����com.android.settings.BandMode ����GSM/UMTS����
		 * ����com.android.settings.BatteryInfo �����Ϣ
		 * ����com.android.settings.DateTimeSettings ���ںͰ���������ʱ������
		 * ����com.android.settings.DateTimeSettingsSetupWizard ���ں�ʱ������
		 * ����com.android.settings.DevelopmentSettings Ӧ�ó�������=����������
		 * ����com.android.settings.DeviceAdminSettings �豸������
		 * ����com.android.settings.DeviceInfoSettings �����ֻ�
		 * ����com.android.settings.Display ��ʾ����������ʾ�����С��Ԥ��
		 * ����com.android.settings.DisplaySettings ��ʾ����
		 * ����com.android.settings.DockSettings ��������
		 * ����com.android.settings.IccLockSettings SIM����������
		 * ����com.android.settings.InstalledAppDetails ���Ժͼ�������
		 * ����com.android.settings.LanguageSettings ���Ժͼ�������
		 * ����com.android.settings.LocalePicker ѡ���ֻ�����
		 * ����com.android.settings.LocalePickerInSetupWizard ѡ���ֻ�����
		 * ����com.android.settings.ManageApplications �����أ���װ������б�
		 * ����com.android.settings.MasterClear �ָ���������
		 * ����com.android.settings.MediaFormat ��ʽ���ֻ�����
		 * ����com.android.settings.PhysicalKeyboardSettings ���ü���
		 * ����com.android.settings.PrivacySettings ��˽����
		 * ����com.android.settings.ProxySelector ��������
		 * ����com.android.settings.RadioInfo �ֻ���Ϣ
		 * ����com.android.settings.RunningServices �������еĳ��򣨷���
		 * ����com.android.settings.SecuritySettings λ�úͰ�ȫ����
		 * ����com.android.settings.Settings ϵͳ����
		 * ����com.android.settings.SettingsSafetyLegalActivity ��ȫ��Ϣ
		 * ����com.android.settings.SoundSettings ��������
		 * ����com.android.settings.TestingSettings ���ԡ�����ʾ�ֻ���Ϣ�������Ϣ��ʹ�����ͳ�ơ�Wifi
		 * information��������Ϣ ����com.android.settings.TetherSettings �����Яʽ�ȵ�
		 * ����com.android.settings.TextToSpeechSettings ����ת��������
		 * ����com.android.settings.UsageStats ʹ�����ͳ��
		 * ����com.android.settings.UserDictionarySettings �û��ʵ�
		 * ����com.android.settings.VoiceInputOutputSettings �����������������
		 * ����com.android.settings.WirelessSettings ���ߺ���������
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
			// ��Ļ�����л�ʱ��÷���
			if (myactivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// myactivity.setTitle("landscape");
				this.orientation = this.LANDSCAPE;
			}
			if (myactivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				// myactivity.setTitle("portrait");
				this.orientation = this.PORTRAIT;
			}
			// �����Ļ��С1
			WindowManager manager = myactivity.getWindowManager();
			int width = manager.getDefaultDisplay().getWidth();
			int height = manager.getDefaultDisplay().getHeight();
			// �����Ļ��С2
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
