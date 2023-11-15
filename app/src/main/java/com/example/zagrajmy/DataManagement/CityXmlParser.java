package com.example.zagrajmy.DataManagement;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import com.example.zagrajmy.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CityXmlParser {

    public static List<String> parseCityNames(Context context) {
        List<String> cityNameList = new ArrayList<>();
        cityNameList.add("Wybierz miasto");

        try {
            XmlResourceParser xmlResourceParser = context.getResources().getXml(R.xml.cities_in_poland);

            int eventType = xmlResourceParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xmlResourceParser.getName().equals("row")) {
                    String cityType = null;
                    String cityName = null;

                    while (eventType != XmlPullParser.END_TAG || !xmlResourceParser.getName().equals("row")) {
                        if (eventType == XmlPullParser.START_TAG) {
                            String tagName = xmlResourceParser.getName();

                            if (tagName.equals("RODZ")) {
                                cityType = xmlResourceParser.nextText();
                            } else if (tagName.equals("NAZWA")) {
                                cityName = xmlResourceParser.nextText();
                            }
                        }

                        eventType = xmlResourceParser.next();
                    }

                    if (cityType != null && cityName != null && (cityType.equals("1") || cityType.equals("3"))) {
                        cityNameList.add(cityName);
                    }
                }

                eventType = xmlResourceParser.next();
            }
        } catch (Resources.NotFoundException | XmlPullParserException | IOException e) {

            throw new RuntimeException(e);
        }
        return cityNameList;
    }
}
