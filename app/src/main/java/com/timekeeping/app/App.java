/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG
*/
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱。


package com.timekeeping.app;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;

import com.chopping.net.TaskHelper;
import com.timekeeping.app.services.TimekeepingService;

/**
 * This application.
 *
 * @author Xinyue Zhao
 */
public final class App extends Application{
	/**
	 * Instance of this application.
	 */
	private static App sIns;


	/**
	 * To get application's instance.
	 *
	 * @return The instance of the application.
	 */
	public static App getInstance() {
		return sIns;
	}

	@Override
	public void onCreate() {
		sIns = this;
		super.onCreate();
		TaskHelper.init(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		stopService(new Intent(this, TimekeepingService.class));
		startService(new Intent(this, TimekeepingService.class));
	}
}
