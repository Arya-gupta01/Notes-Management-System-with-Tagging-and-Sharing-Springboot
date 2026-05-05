package com.noteapp.backend.service;

import com.noteapp.backend.entity.Note;
import com.noteapp.backend.entity.User;
import com.noteapp.backend.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    public Note createNote(Note note) {
        return noteRepository.save(note);
    }

    public List<Note> getNotesByOwner(User owner) {
        return noteRepository.findByOwner(owner);
    }

    public List<Note> getSharedNotes(User user) {
        return noteRepository.findBySharedWith(user);
    }

    public List<Note> getPublicNotes() {
        return noteRepository.findByIsPublicTrue();
    }

    public Optional<Note> getNoteById(Long id) {
        return noteRepository.findById(id);
    }

    public Note updateNote(Note note) {
        return noteRepository.save(note);
    }

    public void deleteNote(Long id) {
        noteRepository.deleteById(id);
    }

    public void shareNote(Note note, User user) {
        note.getSharedWith().add(user);
        noteRepository.save(note);
    }

    public void unshareNote(Note note, User user) {
        note.getSharedWith().remove(user);
        noteRepository.save(note);
    }
}