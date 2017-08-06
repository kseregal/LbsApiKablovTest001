package ru.onego.lbsapikablovtest001;

import java.io.ByteArrayInputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;
/**
 * Created by Серега on 03.08.2017.
 */

public class LbsInfo {

    private static final int ID_UNKNOWN = 0;
    private static final int ID_ERROR = 1;
    private static final int ID_POSITION = 2;
    private static final int ID_LATITUDE = 3;
    private static final int ID_LONGTITUDE = 4;
    private static final int ID_ALTITUDE = 5;
    private static final int ID_PRECISION = 6;
    private static final int ID_TYPE = 7;

    private static final String TAG_ERROR = "error";
    private static final String TAG_POSITION = "position";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGTITUDE = "longitude";
    private static final String TAG_ALTITUDE = "altitude";
    private static final String TAG_PRECISION = "precision";
    private static final String TAG_TYPE= "type";

    public String lbsLatitude;
    public String lbsLongtitude;
    public String lbsAltitude;
    public String lbsPrecision;
    public String lbsType;

    public boolean isError;
    public String errorMessage;

    public static LbsInfo parseByteData(byte[] response) {
        LbsInfo result = new LbsInfo();
        if (response != null && response.length > 0) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(response);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(bais, null);
                int elemId = ID_UNKNOWN;
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tag = xpp.getName();
                        if (TAG_ERROR.equals(tag)) {
                            elemId = ID_ERROR;
                            result.isError = true;
                        } else if (TAG_POSITION.equals(tag)) {
                            elemId = ID_POSITION;
                            result.isError = false;
                        } else if (elemId == ID_POSITION && TAG_LATITUDE.equals(tag)) {
                            elemId = ID_LATITUDE;
                        } else if (elemId == ID_POSITION && TAG_LONGTITUDE.equals(tag)) {
                            elemId = ID_LONGTITUDE;
                        } else if (elemId == ID_POSITION && TAG_ALTITUDE.equals(tag)) {
                            elemId = ID_ALTITUDE;
                        } else if (elemId == ID_POSITION && TAG_PRECISION.equals(tag)) {
                            elemId = ID_PRECISION;
                        } else if (elemId == ID_POSITION && TAG_TYPE.equals(tag)) {
                            elemId = ID_TYPE;
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        String tag = xpp.getName();
                        if (TAG_ERROR.equals(tag)) {
                            elemId = ID_UNKNOWN;
                        } else if (TAG_POSITION.equals(tag)) {
                            elemId = ID_UNKNOWN;
                        } else if (TAG_LATITUDE.equals(tag) || TAG_LONGTITUDE.equals(tag) || TAG_ALTITUDE.equals(tag) || TAG_PRECISION.equals(tag) || TAG_TYPE.equals(tag)) {
                            elemId = ID_POSITION;
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (elemId == ID_ERROR) {
                            result.errorMessage = xpp.getText();
                        } else if (elemId == ID_LATITUDE) {
                            result.lbsLatitude = xpp.getText();
                        } else if (elemId == ID_LONGTITUDE) {
                            result.lbsLongtitude = xpp.getText();
                        } else if (elemId == ID_ALTITUDE) {
                            result.lbsAltitude = xpp.getText();
                        } else if (elemId == ID_PRECISION) {
                            result.lbsPrecision = xpp.getText();
                        } else if (elemId == ID_TYPE) {
                            result.lbsType = xpp.getText();
                        }
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                Log.e("LBS","parse response error "+e );
            }
        }
        return result;
    }
}

