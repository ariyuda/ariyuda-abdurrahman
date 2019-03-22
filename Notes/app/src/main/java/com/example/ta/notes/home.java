package com.example.ta.notes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import com.example.ta.notes.adapter.NoteAdapter;
import com.example.ta.notes.db.NoteHelper;
import com.example.ta.notes.entity.Note;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import static com.example.ta.notes.tambah_dan_update.REQUEST_UPDATE;

public class home extends AppCompatActivity implements View.OnClickListener, LoadNotesCallback {
    private RecyclerView rvNotes;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    private static final String EXTRA_STATE = "EXTRA_STATE";
    private NoteAdapter adapter;
    private NoteHelper noteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Notes");

        rvNotes = findViewById(R.id.rv_home);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setHasFixedSize(true);

        noteHelper = NoteHelper.getInstance(getApplicationContext());

        noteHelper.open();

        progressBar = findViewById(R.id.progressbar);
        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(this);

        adapter = new NoteAdapter(this);
        rvNotes.setAdapter(adapter);


        if (savedInstanceState == null) {
            new LoadNotesAsync(noteHelper, this).execute();
        } else {
            ArrayList<Note> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) {
                adapter.setListNotes(list);
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getListNotes());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add) {
            Intent intent = new Intent(home.this, tambah_dan_update.class);
            startActivityForResult(intent, tambah_dan_update.REQUEST_ADD);
        }
    }

    @Override
    public void preExecute() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void postExecute(ArrayList<Note> notes) {

        progressBar.setVisibility(View.INVISIBLE);
        adapter.setListNotes(notes);
    }

    private static class LoadNotesAsync extends AsyncTask<Void, Void, ArrayList<Note>> {

        private final WeakReference<NoteHelper> weakNoteHelper;
        private final WeakReference<LoadNotesCallback> weakCallback;

        private LoadNotesAsync(NoteHelper noteHelper, LoadNotesCallback callback) {
            weakNoteHelper = new WeakReference<>(noteHelper);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<Note> doInBackground(Void... voids) {

            return weakNoteHelper.get().getAllNotes();
        }

        @Override
        protected void onPostExecute(ArrayList<Note> notes) {
            super.onPostExecute(notes);

            weakCallback.get().postExecute(notes);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            //memanggil request ADD
            if (requestCode == tambah_dan_update.REQUEST_ADD) {
                if (resultCode == tambah_dan_update.RESULT_ADD) {
                    Note note = data.getParcelableExtra(tambah_dan_update.EXTRA_NOTE);

                    adapter.addItem(note);
                    rvNotes.smoothScrollToPosition(adapter.getItemCount() - 1);

                    showSnackbarMessage("Berhasil ditambahkan");
                }
            }

            else if (requestCode == REQUEST_UPDATE) {
                //menampilkan data setelah diupdate
                if (resultCode == tambah_dan_update.RESULT_UPDATE) {

                    Note note = data.getParcelableExtra(tambah_dan_update.EXTRA_NOTE);
                    int position = data.getIntExtra(tambah_dan_update.EXTRA_POSITION, 0);

                    adapter.updateItem(position, note);
                    rvNotes.smoothScrollToPosition(position);

                    showSnackbarMessage("Berhasil diubah");
                }
                //menampilkan data setelah dihapus
                else if (resultCode == tambah_dan_update.RESULT_DELETE) {
                    int position = data.getIntExtra(tambah_dan_update.EXTRA_POSITION, 0);

                    adapter.removeItem(position);

                    showSnackbarMessage("Berhasil dihapus");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteHelper.close();
    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(rvNotes, message, Snackbar.LENGTH_SHORT).show();
    }
}
