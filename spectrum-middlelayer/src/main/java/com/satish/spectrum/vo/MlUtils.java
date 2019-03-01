package com.satish.spectrum.vo;

import java.time.Duration;

public class MlUtils {

	public static String shorten(String toShort){
		String shortened = "";
		int length = toShort.length();
		if (length > 9){
			shortened = toShort.substring(0, 3);
			shortened += "~";
			shortened += toShort.substring(length-3, length);
		} else {
			shortened = toShort;
		}
		return shortened;
	}
	
	public static String toBtcString(long satoshi){
		return String.format("%.1f BTC", satoshi/1.0E8);
	}
	
	public static String formatDuration(Duration duration) {
	    long seconds = duration.getSeconds();
	    long absSeconds = Math.abs(seconds);
	    String positive = String.format(
	        "%d:%02d:%02d",
	        absSeconds / 3600,
	        (absSeconds % 3600) / 60,
	        absSeconds % 60);
	    return seconds < 0 ? "-" + positive : positive;
	}
	
	public static String formatDurationInMs(Duration duration) {
	    long ms = duration.toMillis();
	    long absMs = Math.abs(ms);
	    String positive = String.format(
	        "%d:%02d:%02d.%03d",
	        absMs / 3600000,
	        (absMs % 3600000) / 60000,
	        (absMs % 60000) / 1000,
	        absMs % 1000);
	    return ms < 0 ? "-" + positive : positive;
	}

	public static String toBtcString(Long satoshi, boolean withUnits) {
		if (withUnits)
			return toBtcString(satoshi);
		else
		    return String.format("%.1f", satoshi/1.0E8);
	}
	
	public static long btcToSatoshi(double btc){
		return (long) (btc * 100000000);
	}
}
