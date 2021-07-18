package com.nowinski.kamil.flighttracker.Utils;

import com.nowinski.kamil.flighttracker.Entities.FlightInfo;
import com.nowinski.kamil.flighttracker.Entities.Radar;

import java.util.ArrayList;
import java.util.List;


public class RadarParser {

    private static Radar radar;

    public static Radar parseJsonToRadarObj(String jsonData) {
        int allAirPlanes = Integer.parseInt(jsonData.substring(jsonData.indexOf("\"full_count\":")+13, jsonData.indexOf(",\"")));
        String[] lines = null;
        if(jsonData.endsWith("] }"))
        {
            jsonData = jsonData.substring(jsonData.indexOf(",\"", jsonData.indexOf(",\"") + 1) + 1, jsonData.indexOf("] }")).replace(":[", ",");
            lines = jsonData.split("] ,");
        }
        if(lines != null)
            return buildRadarObj(allAirPlanes, 4, lines);
        else return null;
    }

    private static Radar buildRadarObj(int allAirPlanes, int version, String[] lines) {
        List<FlightInfo> flightInfoList = new ArrayList<>();
        String[] params;
        for(String l : lines) {
            params = l.split(",");
            for(int i=0;i<params.length; ++i) {
                params[i] = params[i].replace("\"\"", "0").replace("\"", "");
            }
            flightInfoList.add(new FlightInfo(params[0], params[1], Double.parseDouble(params[2]), Double.parseDouble(params[3]),
                    Integer.parseInt(params[4]), Integer.parseInt(params[5]), Integer.parseInt(params[6]), Integer.parseInt(params[7]),
                    params[8], params[9], params[10], Integer.parseInt(params[11]),
                    params[12], params[13], params[14], Integer.parseInt(params[15]),
                    Integer.parseInt(params[16]), params[17], Integer.parseInt(params[18]), params[19]));
        }
        radar = new Radar(allAirPlanes, version, flightInfoList);
        return radar;
    }
}
