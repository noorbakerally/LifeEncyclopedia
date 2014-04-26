package com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.database.SQLException;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.LifeObject;
import com.android.volley.RequestQueue;

public class Constants {
	public static String REST_URL = "http://apioflife-itspirits.rhcloud.com/v1/";
	public static String SHARE_STRING = "Discover Life with Life Encyclopedia Hey check out my app at: https://play.google.com/store/apps/details?id=com.google.android.apps.plus";
	public static RequestQueue queue;
	public static LifeObject currentLifeObject = null;
	public static int currentLifeObjectPosition = 0;
	
	public final static List <LifeObject> lifeObjects = new ArrayList<LifeObject>();
	public static String capitalize(String word){
		return word.substring(0, 1).toUpperCase() + word.substring(1);
	}
}
