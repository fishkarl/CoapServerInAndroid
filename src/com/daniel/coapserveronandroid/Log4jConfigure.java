package com.daniel.coapserveronandroid;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public final class Log4jConfigure {
	// =============================================
	// Log4j for android 的配置初始化
	// 建议放在Application的onCreate方法里
	// 另外要判断手机是否有SD卡
	// =============================================

	private static Logger mLogger = null;

	public static void configure() {
		final LogConfigurator logConfigurator = new LogConfigurator();
		
		// ===============================================================
		// 如果想创建的日记文件在多级目录下，要自己先创建目录，Log4j只能创建日记文件，不能创建多级目录
		// ===============================================================
		logConfigurator.setUseLogCatAppender(true);
		logConfigurator.setFileName(Environment.getExternalStorageDirectory()
				+ File.separator + "debugLog.txt");
		logConfigurator.setRootLevel(Level.ALL); // 设置输出类型
		logConfigurator.setLevel("org.apache", Level.ALL);
		//logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
		//logConfigurator.setMaxFileSize(1024 * 1024 * 5);
		logConfigurator.setImmediateFlush(true);
		logConfigurator.setUseLogCatAppender(true);
		logConfigurator.configure();

		mLogger = Logger.getLogger("Log4jForAndroid");

	}

	public static void debug(String str) {
		mLogger.debug(str);
	}

}