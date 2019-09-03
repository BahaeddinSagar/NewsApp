package ly.bsagar.newsapp;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class DisplayNewsActivity extends AppCompatActivity
        implements LoaderCallbacks<ArrayList<News>> {
    private static final int NEWS_LOADER_ID = 1;
    private static final String TAG = "GaurdianNews";
    final String GUARDIANURL = "https://content.guardianapis.com/search?api-key=55f469f4-5fcf-4b6a-8241-be08424a0b74";
    ListView newsListView;
    NewsArrayAdapter arrayAdapter;
    TextView emptyTextView;
    View loadingIndicator ;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_news);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadingIndicator = findViewById(R.id.loading_indicator);
        emptyTextView = findViewById(R.id.textView);
        newsListView = findViewById(R.id.NewsListView);
        arrayAdapter = new NewsArrayAdapter(this, 0, new ArrayList<News>());

        newsListView.setAdapter(arrayAdapter);
        newsListView.setEmptyView(emptyTextView);

        makeNetworkRequest();

    }

    private void makeNetworkRequest() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting()){
            android.app.LoaderManager manager = getLoaderManager();
            manager.initLoader(NEWS_LOADER_ID, null, this).forceLoad();
        } else {
            emptyTextView.setText(R.string.connectivityissue);
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public Loader<ArrayList<News>> onCreateLoader(int id, @Nullable Bundle args) {
        String sectionRequested = sharedPreferences.getString("sectionKey","all");
        if (!sectionRequested.equals("all")) {
            Uri uri = Uri.parse(GUARDIANURL);
            return new NewsLoader(this,
                    uri.buildUpon().appendQueryParameter("section",sectionRequested).toString());
        }
        return new NewsLoader(this, GUARDIANURL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<News>> loader, ArrayList<News> data) {
        arrayAdapter.clear();
        loadingIndicator.setVisibility(View.GONE);
        emptyTextView.setText(R.string.cannotConnectToServer);
        if (data != null && !data.isEmpty()) {
            arrayAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<News>> loader) {
        arrayAdapter.clear();
    }


    static class NewsLoader extends AsyncTaskLoader<ArrayList<News>> {

        String urlString;

        public NewsLoader(@NonNull Context context, String stringurl) {
            super(context);
            urlString = stringurl;
        }

        @Nullable
        @Override
        public ArrayList<News> loadInBackground() {
            ArrayList<News> result = null;
            if (urlString == null) {
                return null;
            }
            try {
                result = HelperFunctions.getNewsFromGaurdian(urlString);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings){
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.reload){
            makeNetworkRequest();
        }
        return super.onOptionsItemSelected(item);
    }
}
