package com.noteapp.backend.controller;

import com.noteapp.backend.entity.Note;
import com.noteapp.backend.entity.User;
import com.noteapp.backend.service.NoteService;
import com.noteapp.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Note>> getMyNotes(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        List<Note> notes = noteService.getNotesByOwner(user);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/shared")
    public ResponseEntity<List<Note>> getSharedNotes(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        List<Note> notes = noteService.getSharedNotes(user);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/public")
    public ResponseEntity<List<Note>> getPublicNotes() {
        List<Note> notes = noteService.getPublicNotes();
        return ResponseEntity.ok(notes);
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        note.setOwner(user);
        Note savedNote = noteService.createNote(note);
        return ResponseEntity.ok(savedNote);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Note noteDetails, Authentication authentication) {
        Optional<Note> optionalNote = noteService.getNoteById(id);
        if (optionalNote.isPresent()) {
            Note note = optionalNote.get();
            User user = userService.findByUsername(authentication.getName()).orElseThrow();
            if (note.getOwner().equals(user) || note.getSharedWith().contains(user)) {
                note.setTitle(noteDetails.getTitle());
                note.setContent(noteDetails.getContent());
                note.setPublic(noteDetails.isPublic());
                Note updatedNote = noteService.updateNote(note);
                return ResponseEntity.ok(updatedNote);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id, Authentication authentication) {
        Optional<Note> optionalNote = noteService.getNoteById(id);
        if (optionalNote.isPresent()) {
            Note note = optionalNote.get();
            User user = userService.findByUsername(authentication.getName()).orElseThrow();
            if (note.getOwner().equals(user)) {
                noteService.deleteNote(id);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<?> shareNote(@PathVariable Long id, @RequestParam String username, Authentication authentication) {
        Optional<Note> optionalNote = noteService.getNoteById(id);
        Optional<User> optionalUser = userService.findByUsername(username);
        if (optionalNote.isPresent() && optionalUser.isPresent()) {
            Note note = optionalNote.get();
            User owner = userService.findByUsername(authentication.getName()).orElseThrow();
            User shareUser = optionalUser.get();
            if (note.getOwner().equals(owner)) {
                noteService.shareNote(note, shareUser);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/{id}/unshare")
    public ResponseEntity<?> unshareNote(@PathVariable Long id, @RequestParam String username, Authentication authentication) {
        Optional<Note> optionalNote = noteService.getNoteById(id);
        Optional<User> optionalUser = userService.findByUsername(username);
        if (optionalNote.isPresent() && optionalUser.isPresent()) {
            Note note = optionalNote.get();
            User owner = userService.findByUsername(authentication.getName()).orElseThrow();
            User shareUser = optionalUser.get();
            if (note.getOwner().equals(owner)) {
                noteService.unshareNote(note, shareUser);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }
}