package com.example.perpetua.services;

import com.example.perpetua.controller.PlaylistController;
import com.example.perpetua.dto.Song;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

@Service
public class MusixmatchService {
    final static Logger logger = LoggerFactory.getLogger(PlaylistController.class);
    public Song fetchSong(String lyrics, String apiKey) {
        Song result = null;
        try {
            URI uri = null;
            if (lyrics.equalsIgnoreCase("")) {
                uri = new URIBuilder("https://api.musixmatch.com/ws/1.1/track.search")
                        .addParameter("format", "jsonp")
                        .addParameter("callback", "callback")
                        .addParameter("quorum_factor", "1")
                        .addParameter("apikey", apiKey)
                        .build();

            } else {
                uri = new URIBuilder("https://api.musixmatch.com/ws/1.1/track.search")
                        .addParameter("format", "jsonp")
                        .addParameter("callback", "callback")
                        .addParameter("q_lyrics", lyrics)
                        .addParameter("quorum_factor", "1")
                        .addParameter("apikey", apiKey)
                        .build();
            }
            URL url = new URL(uri.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.connect();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            String withoutcallback = content.toString().replaceFirst("callback\\(", "") ;
            logger.info(withoutcallback);
            String res = withoutcallback.substring(0, withoutcallback.length() - 1);
            JSONObject json = new JSONObject(res);
            String trackName = json.getJSONObject("message").getJSONObject("body").getJSONArray("track_list").getJSONObject(0).getJSONObject("track").getString("track_name");
            int hasLyrics = json.getJSONObject("message").getJSONObject("body").getJSONArray("track_list").getJSONObject(0).getJSONObject("track").getInt("has_lyrics");
            String artisitName = json.getJSONObject("message").getJSONObject("body").getJSONArray("track_list").getJSONObject(0).getJSONObject("track").getString("artist_name");
            int trackId = json.getJSONObject("message").getJSONObject("body").getJSONArray("track_list").getJSONObject(0).getJSONObject("track").getInt("track_id");
            if (hasLyrics == 1 ){
                String fullLyrics = fetchLyrics(trackId, apiKey);
                if (fullLyrics.isEmpty()){
                    return null;
                } else {
                    result  = new Song(trackName, artisitName, fullLyrics) ;
                }
            }

        } catch (Exception e){
            logger.warn(e.getMessage());
            return null;
        }

        return result;
    }


    public String fetchLyrics(int trackId, String apiKey) {
        String result = "";
        try {
            URI uri = new URIBuilder("https://api.musixmatch.com/ws/1.1/track.lyrics.get")
                    .addParameter("format", "jsonp")
                    .addParameter("callback", "callback")
                    .addParameter("track_id",""+ trackId)
                    .addParameter("apikey", apiKey)
                    .build();
            URL url = new URL(uri.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.connect();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            String withoutcallback = content.toString().replaceFirst("callback\\(", "") ;
            String res = withoutcallback.substring(0, withoutcallback.length() - 1);
            JSONObject json = new JSONObject(res);
            result = json.getJSONObject("message").getJSONObject("body").getJSONObject("lyrics").getString("lyrics_body");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return result;
    }

}
