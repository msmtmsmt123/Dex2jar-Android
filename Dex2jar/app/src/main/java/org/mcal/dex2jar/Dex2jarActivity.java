package org.mcal.dex2jar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.googlecode.dex2jar.v3.Dex2jar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Dex2jarActivity extends AppCompatActivity
{
    private static final int MSG_PROCESS = 1;
    private static final int MSG_FINISH = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dex2jar);

        findViewById(R.id.text_input).setVisibility(View.VISIBLE);
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TextInputLayout textInput = (TextInputLayout)findViewById(R.id.text_edit);

                final File from = new File(textInput.getEditText().getText().toString());
                if(!from.canRead() && !from.exists())
                {
                    new AlertDialog.Builder(Dex2jarActivity.this).setTitle(R.string.error).setPositiveButton(android.R.string.ok,null).setMessage(R.string.not_found).show();
                }
                else
                {
                    new Thread()
                    {
                        @Override
                        public void run()
                        {
                            super.run();
                            mHandler.sendEmptyMessage(MSG_PROCESS);
                            toJar(from);
                            mHandler.sendEmptyMessage(MSG_FINISH);
                        }
                    }.start();
                }
            }
        });
    }

   private Handler mHandler = new Handler()
   {
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);

           if(msg.what == MSG_PROCESS)
           {
               findViewById(R.id.text_input).setVisibility(View.GONE);
               findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
           }
           else if(msg.what == MSG_FINISH)
           {
               findViewById(R.id.progress_bar).setVisibility(View.GONE);
               findViewById(R.id.text_view).setVisibility(View.VISIBLE);
           }
       }
   };

    private IOException toJar(final File from)
    {
        Log.d("A", "toJar: start");
        if(!from.canRead())
            return new FileNotFoundException(from.getPath());

        try
        {
            File to = new File(from.getParentFile(),from.getName() + ".jar");
            to.createNewFile();
            Dex2jar.from(from).to(to);
            Log.d("A", "toJar: finish");
            return null;
        }
        catch(IOException ioe)
        {
            return ioe;
        }
    }
}