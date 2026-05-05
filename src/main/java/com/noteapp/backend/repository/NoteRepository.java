package com.noteapp.backend.repository;

import com.noteapp.backend.entity.Note;
import com.noteapp.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByOwner(User owner);
    List<Note> findBySharedWith(User user);
    List<Note> findByIsPublicTrue();
}