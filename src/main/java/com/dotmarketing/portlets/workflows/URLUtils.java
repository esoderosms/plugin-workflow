package com.dotmarketing.portlets.workflows;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class URLUtils {
	private static String regexLastBar = "\\/(?=[^/]*$)";

	public static URL decodeURL(String urlStr) throws UnsupportedEncodingException{
		URL url = null;
		if(urlStr != null && !urlStr.equals("")){
			try {
				url = new URL(URLDecoder.decode(urlStr, StandardCharsets.UTF_8.toString()));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return url;
	}
	
	public static String getURLWithoutFragment(URL url) throws UnsupportedEncodingException{
		return url == null ? "" : url.getProtocol() + "://" + url.getHost() + url.getPath();
	}
	
	public static String getURLParenthPath(URL url) throws UnsupportedEncodingException{
		return splitPath(url, 0) + "/";
	}
	
	public static String getURLAssetName(URL url) throws UnsupportedEncodingException{
		return splitPath(url, 1);
	}
	
	private static String splitPath(URL url, int position){
		if(url != null){
			String[] parentPath = url.getPath().split(regexLastBar);
			if(parentPath != null){
				return parentPath[position];
			}
		}
		return "";
	}
}
