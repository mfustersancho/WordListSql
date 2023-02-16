package com.example.wordlistsql;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = EditWordActivity.class.getSimpleName();

    private TextView mTextView;
    private EditText mSearchedWordView;
    private WordListOpenHelper mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSearchedWordView = findViewById(R.id.search_word);
        mTextView = findViewById(R.id.search_result);
        mDB = new WordListOpenHelper(this);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String word = intent.getStringExtra(SearchManager.QUERY);
            mSearchedWordView.setText(getString(R.string.search_term) + word);
            Cursor cursor = mDB.search(word);
            if(cursor != null && (cursor.getCount() > 0)) {
                cursor.moveToFirst();
                int index;
                String result;
                do{
                    index = cursor.getColumnIndex(WordListOpenHelper.KEY_WORD);
                    result = cursor.getString(index);
                    mTextView.append(result + "\n");
                } while(cursor.moveToNext());
                cursor.close();
            } else {
                mTextView.setText(R.string.term_not_found);
            }
        }
    }
}