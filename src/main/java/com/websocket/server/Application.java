package com.websocket.server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;

public class Application {

	public static void main(String[] args) throws Exception {
//		new WebsocketServer().start();

		// Pattern pattern = Pattern.compile("GET (((/[a-zA-Z]+)+/?)|(/[a-zA-Z]*)) HTTP/(1\\.1|2\\.0|3\\.0)");
		// Matcher matcher = pattern.matcher("GET / HTTP/2.0");
		// if (matcher.matches()) {
		// 	System.out.println("Find");
		// } else System.out.println("Not found");

		String s = "dGhlIHNhbXBsZSBub25jZQ==258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		// System.out.println(Base64.getEncoder().encodeToString(DigestUtils.sha1Hex(s).getBytes()));
		
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
		byte[] sha1Hash = messageDigest.digest(s.getBytes(StandardCharsets.UTF_8));
		System.out.println(Base64.getEncoder().encodeToString(sha1Hash));
	}
}
