package me.fernandodominguez.zenmap;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import me.fernandodominguez.zenmap.helpers.FileHelper;
import me.fernandodominguez.zenmap.models.Nmap;

public class MainActivity extends AppCompatActivity {

    private final String NMAP_BINARY_FILE = "nmap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try {
            installNmap();
            Nmap nmap = new Nmap(this);
            Log.d("NMAP VERSION", nmap.version());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void installNmap() throws Exception {

        String dir = getFilesDir() + "/bin/";
        File nmap = new File(dir + NMAP_BINARY_FILE);
        if (nmap.exists()) return;

        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
            for (String file : files) {
                if (file.equals(NMAP_BINARY_FILE)) {
                    InputStream stream = this.getAssets().open(file);
                    new File(dir).mkdir();
                    OutputStream output = new BufferedOutputStream(new FileOutputStream(dir + NMAP_BINARY_FILE));

                    byte data[] = new byte[1024];
                    int count;

                    while ((count = stream.read(data)) != -1) {
                        output.write(data, 0, count);
                    }

                    FileHelper.chmod(new File(dir + NMAP_BINARY_FILE), 0550);
                    output.flush();
                    output.close();
                    stream.close();
                }
            }

        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
