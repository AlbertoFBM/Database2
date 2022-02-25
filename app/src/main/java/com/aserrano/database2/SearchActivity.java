package com.aserrano.database2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = EditWordActivity.class.getSimpleName();
    private TextView mTextView;
    private EditText mEditWordView;
    private WordListOpenHelper mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mDB = new WordListOpenHelper(this);
        mTextView = (TextView) findViewById(R.id.search_result);
        mEditWordView = (EditText) findViewById(R.id.search_word);

    }


    public void showResult(View view){

        String word = String.valueOf(mEditWordView.getText());
        mTextView.append("\nResult for " + word + ":\n\n");

        Cursor cursor = mDB.search(word);

        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();

            int index;
            String result;

            do{

                index = cursor.getColumnIndex(WordListOpenHelper.KEY_WORD);
                result = cursor.getString(index);

                mTextView.append(result + "\n");

            }while (cursor.moveToNext());
            cursor.close();
        }else{
            mTextView.append(getString(R.string.no_results));
        }

    }

}