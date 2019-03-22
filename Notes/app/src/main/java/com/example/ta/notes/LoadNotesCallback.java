package com.example.ta.notes;

import com.example.ta.notes.entity.Note;
import java.util.ArrayList;

public interface LoadNotesCallback {
    void preExecute();

    void postExecute(ArrayList<Note> notes);
}

