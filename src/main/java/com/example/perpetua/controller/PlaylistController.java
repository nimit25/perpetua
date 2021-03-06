package com.example.perpetua.controller;

import com.example.perpetua.dto.Playlist;
import com.example.perpetua.dto.Song;
import com.example.perpetua.repository.PlaylistRepo;
import com.example.perpetua.repository.SongRepo;
import com.example.perpetua.services.MusixmatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@RestController
public class PlaylistController {

    final static Logger logger = LoggerFactory.getLogger(PlaylistController.class);

    @Autowired
    SongRepo songRepo;

    @Autowired
    PlaylistRepo playlistRepo;

    @Autowired
    MusixmatchService musixmatchService;


    @GetMapping("/getSubsequentSong/{id}")
    public Playlist getSubsequentSong(@PathVariable String id,  @RequestParam String apikey){
        logger.info("fetching subsequent song for playlist " + id);
        Playlist playlist = playlistRepo.findById(Long.valueOf(id)).get();
        List<Song> result = playlist.getSongs();
        String randomLyrics = getRandomWords(result.get(0));
        logger.info("random lyrics were " + randomLyrics);
        Song song = musixmatchService.fetchSong(randomLyrics, apikey);
        if (song != null){
            songRepo.save(song);
            result.remove(0);
            result.add(song);
            playlist.setSongs(result);

            playlistRepo.save(playlist);
            return playlist;
        }

        result.remove(0);
        playlist.setSongs(result);
        playlistRepo.save(playlist);
        return playlist;


    }


    @GetMapping("/generatePlaylist")
    public Playlist generatePlaylist( @RequestParam String apikey)  {
        logger.info("generating playlist");
        List<Song> result = new ArrayList<>();
        Song song1 =  musixmatchService.fetchSong("Any word in the lyrics", apikey);
        Song song2 = nextSong(song1, apikey);
//        Song song2 = musixmatchService.fetchSong("Any word in the lyrics", apikey, 1);
        result.add(song1);
        result.add( song2);
        songRepo.saveAll(result);
        Playlist playlist = new Playlist(result);
        playlistRepo.save(playlist);

//        String randomLyrics = getRandomWords(song1);
//        Song song = musixmatchService.fetchSong(randomLyrics, apikey, 0);
//        while (song != null){
//            result.remove(1);
//            result.add(0, song.clone());
//            Song songBuff = result.get(1);
//            String randomLyrics1 = getRandomWords(songBuff);
//            song = musixmatchService.fetchSong(randomLyrics1, apikey, 0);
//        }
        return playlist;
    }

    @GetMapping("/getSong")
    public Song  getLyrics(@RequestParam String lyrics, @RequestParam String apikey){
        logger.info("fetching song");
        Song song = musixmatchService.fetchSong(lyrics, apikey);
        if (song == null){
            return null;
        }
        return song;
    }

    public String getRandomWords(Song song1) {
        logger.info("getting random words");
        List<String> str2 = new ArrayList<>();
        for (String token : song1.getLyrics().split("\\W+") ){// Array.asList(
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
            String string =  clone.get(randomIndex) + " ";
            result.append(string);
            clone.remove(randomIndex);
        }

        return result.toString();
    }

    public Song nextSong(Song song, String apikey){
        String randomLyrics = getRandomWords(song);
        Song song2 = musixmatchService.fetchSong(randomLyrics, apikey);
        return song;
    }

}
