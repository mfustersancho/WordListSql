/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.wordlistsql;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Activity to edit an existing or create a new word.
 */
public class EditWordActivity extends AppCompatActivity {

    private static final String TAG = EditWordActivity.class.getSimpleName();

    private static final int NO_ID = -99;
    private static final String NO_WORD = "";

    private EditText mEditWordView;
    private EditText mEditDefinitionView;

    // Unique tag for the intent reply.
    public static final String EXTRA_WORD = "com.example.android.wordlistsql.WORD";
    public static final String EXTRA_DEFINITION = "com.example.android.wordlistsql.DEFINITION";
    int mId = MainActivity.WORD_ADD;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word);

        mEditWordView = (EditText) findViewById(R.id.edit_word);
        mEditDefinitionView = (EditText) findViewById(R.id.edit_definition);

        // Get data sent from calling activity.
        Bundle extras = getIntent().getExtras();

        // If we are passed content, fill it in for the user to edit.
        if (extras != null) {
            int id = extras.getInt(WordListAdapter.EXTRA_ID, NO_ID);
            String word = extras.getString(WordListAdapter.EXTRA_WORD, NO_WORD);
            String definition = extras.getString(WordListAdapter.EXTRA_DEFINITION, NO_WORD);
            if (id != NO_ID && word != NO_WORD) {
                mId = id;
                mEditWordView.setText(word);
                mEditDefinitionView.setText(definition);
            }
        } // Otherwise, start with empty fields.
    }

    /**
     *  Click handler for the Save button.
     *  Creates a new intent for the reply, adds the reply message to it as an extra,
     *  sets the intent result, and closes the activity.
     */
    public void returnReply(View view) {
        String word = ((EditText) findViewById(R.id.edit_word)).getText().toString();
        String definition = ((EditText) findViewById(R.id.edit_definition)).getText().toString();
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_WORD, word);
        replyIntent.putExtra(EXTRA_DEFINITION, definition);
        replyIntent.putExtra(WordListAdapter.EXTRA_ID, mId);
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}

