package com.example.perpetua.repository;

import com.example.perpetua.dto.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepo extends JpaRepository<Song, String> {
}
