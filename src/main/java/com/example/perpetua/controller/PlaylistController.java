package com.example.perpetua.controller;

import com.example.perpetua.dto.Song;
import com.example.perpetua.services.MusixmatchService;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class PlaylistController {

    final static Logger logger = LoggerFactory.getLogger(PlaylistController.class);
    @Autowired
    MusixmatchService musixmatchService;
    List<Song> result = new ArrayList<>();

    @GetMapping("/getSubsequentSong")
    public List<Song> getlyrics(@RequestParam String apikey){
        logger.info("fetching subsequent song");
        String randomLyrics = getRandomWords(result.get(0));
        Song song = musixmatchService.fetchSong(randomLyrics, apikey, 0);
        if (song == null){
            return new ArrayList<Song>();
        }
        result.remove(0);
        result.add(song.clone());
        return result;
    }


    @GetMapping("/generatePlaylist")
    public List<Song> generatePlaylist( @RequestParam String apikey)  {
        logger.info("generating playlist");
        result = new ArrayList<>();
        Song song1 =  musixmatchService.fetchSong("Any word in the lyrics", apikey, 0);
        Song song2 = musixmatchService.fetchSong("Any word in the lyrics", apikey, 1);
        result.add(song1);
        result.add( song2);

//        String randomLyrics = getRandomWords(song1);
//        Song song = musixmatchService.fetchSong(randomLyrics, apikey, 0);
//        while (song != null){
//            result.remove(1);
//            result.add(0, song.clone());
//            Song songBuff = result.get(1);
//            String randomLyrics1 = getRandomWords(songBuff);
//            song = musixmatchService.fetchSong(randomLyrics1, apikey, 0);
//        }
        return result;
    }

    @GetMapping("/getSong")
    public Song  getlyrics(@RequestParam String lyrics, @RequestParam String apikey){
        logger.info("fetching song");
        Song song = musixmatchService.fetchSong(lyrics, apikey, 0);
        if (song == null){
            return null;
        }
        return song;
    }

    public String getRandomWords(Song song1) {
        logger.info("getting random words");
        List<String> str2 = new ArrayList<>();
        for (String token : song1.getLyrics().split("\\W+") ){
            str2.add(token);
        }
        List<String> listOfWords = str2.subList(0, str2.size() - 8); // removing "this is not for commercial use " from the end
        LinkedList<String> clone = new LinkedList<>(listOfWords);
        StringBuffer result = new StringBuffer();
        Random rand = new Random();
        int numberOfElements = 5;
        List<Integer> randindxs = new ArrayList<>();

        for (int i = 0; i < numberOfElements && i < clone.size();  i++) {
            int randomIndex = rand.nextInt(clone.size());
            randindxs.add(randomIndex);
            result.append(clone.get(randomIndex));
            clone.remove(randomIndex);
        }

        return result.toString();
    }

}
