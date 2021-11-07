package com.kolibreath.apttest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kolibreath.annotations.BindView;
import com.kolibreath.annotations.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.text_view)
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.Companion.bind(this);
        textView.setText("haha");
    }

    @OnClick(
            values = {R.id.text_view},
            listenerType = View.OnClickListener.class,
            listenerSetter = "setOnClickListener"
    )
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.text_view:
                Log.d("Test", "成功打印");
                break;
        }
    }

}
