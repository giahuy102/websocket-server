package com.websocket.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application {

	public static void main(String[] args) {
//		new WebsocketServer().start();
		Pattern pattern = Pattern.compile("GET (((/[a-zA-Z]+)+/?)|(/[a-zA-Z]*)) HTTP/(1\\.1|2\\.0|3\\.0)");
		Matcher matcher = pattern.matcher("GET / HTTP/2.0");
		if (matcher.matches()) {
			System.out.println("Find");
		} else System.out.println("Not found");

	}
}
