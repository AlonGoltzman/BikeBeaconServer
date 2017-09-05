package com.bikebeacon.utils;

import static com.bikebeacon.pojo.Constants.LOG_TYPE.ERROR;
import static com.bikebeacon.pojo.Constants.LOG_TYPE.INFO;
import static com.bikebeacon.pojo.Constants.LOG_PATH;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.bikebeacon.pojo.Constants.LOG_TYPE;

public final class PrintUtil {

	private static final ArrayList<Message> log = new ArrayList<>();

	public static void log(String type, String msg) {
		System.out.println(type + " - " + msg);
		log.add(new Message(type, msg));
	}

	public static void log_f(String type, String msg, Object... params) {
		System.out.printf(type + " - " + msg + "\n", params);
		log.add(new Message(type, String.format(msg, params)));
	}

	public static void error(String method, String msg) {
		System.err.println(method + " - " + msg);
		log.add(new Message(method, ERROR, msg));
	}

	public static void error_f(String method, String msg, Object... params) {
		System.err.println(method + " - " + String.format(msg, params));
		log.add(new Message(method, ERROR, msg));
	}

	public static void fileify() {
		try (FileOutputStream stream = new FileOutputStream(LOG_PATH)) {
			for (Message message : log)
				stream.write(message.toString().getBytes());
			log.clear();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final class Message {
		private String message;
		private String caller;
		private String type;
		private Date time;

		Message(String method, String msg) {
			this(method, INFO, msg);
		}

		Message(String method, LOG_TYPE logType, String msg) {
			caller = method;
			type = logType.toString();
			message = msg;
			time = new Date();
			time.setTime(System.currentTimeMillis());
		}

		@Override
		public String toString() {
			return String.format("[%s][%s][%s] - %s\n", time.toString(), type, caller, message);
		}
	}

}
