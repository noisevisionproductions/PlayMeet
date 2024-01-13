package com.noisevisionproductions.playmeet.DataManagement;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;


import com.noisevisionproductions.playmeet.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// klasa stworzona w celu filtracji miast z API miast w Polsce, który jest w formacie xml. Jest on pobrany z oficjalnego portalu API GUS. Plik ten zawiera wszystkie miasta, wsie itp. oraz inne informacje, których nie potrzebuje w swojej aplikacji

public class CityXmlParser {

    public static List<String> parseCityNames(Context context) {
        List<String> cityNameList = new ArrayList<>();
        cityNameList.add("Wybierz miasto");
// irytuje przez cały plik cities_in_poland
        try {
            XmlResourceParser xmlResourceParser = context.getResources().getXml(R.xml.cities_in_poland);

// zmienna eventType, która potrzebna jest do obsługi zmiennych zdarzeń w celu analizy pliku
            int eventType = xmlResourceParser.getEventType();

// pętla, która nie zatrzyma się, dopóki nie dojdzie do końca pliku
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xmlResourceParser.getName().equals("row")) {
                    String cityType = null;
                    String cityName = null;
// z tego względu, że nie każdy fragment z miastami w pliku z miastami jest taki sam, to upewniam się, że kiedy dopiero dojdzie do linijki "row", to skaner będzie zczytywał więcej informacji znajdujące się w bloku 
                    while (eventType != XmlPullParser.END_TAG || !xmlResourceParser.getName().equals("row")) {
                        if (eventType == XmlPullParser.START_TAG) {
// jeżeli jest to początek bloku, to pobieram nazwę tagu
                            String tagName = xmlResourceParser.getName();
// następnie porównuje, czy pobrana nazwa tagu jest równa RODZ, która posiada w swoim bloku potrzebne dla mnie miasta
                            if (tagName.equals("RODZ")) {

// jeżeli skaner napotka RODZ, to przypisuje do zmiennej cityType tekst z tego tagu
                                cityType = xmlResourceParser.nextText();
                            } else if (tagName.equals("NAZWA")) {
// jeśli skaner napotka znowu NAZWA, to też  zapisuje tekst z tego tagu 
                                cityName = xmlResourceParser.nextText();
                            }
                        }
// tworzę na nowo zmienna eventType, aby skaner mógł przejść do kolejnego bloku
                        eventType = xmlResourceParser.next();
                    }
// jeżeli typ oraz nazwa miasta nie jest null oraz typ miasta jest równa 1 (co w tym pliku oznacza miasto), to dodaje pobrana nazwę miasta do listy
                    if (cityType != null && cityName != null && (cityType.equals("1"))) {
                        cityNameList.add(cityName);
                    }
                }

                eventType = xmlResourceParser.next();
            }
        } catch (Resources.NotFoundException | XmlPullParserException | IOException e) {

            throw new RuntimeException(e);
        }

// ostatecznie zwracam kompletna listę z miastami
        return cityNameList;
    }
}
