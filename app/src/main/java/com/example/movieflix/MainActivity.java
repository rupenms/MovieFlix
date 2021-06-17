package com.example.movieflix;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    ListView listView;
    MovieListAdapter adapter;
    ArrayList<MovieData> imageData;
    Context context;
    TextView empty;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        empty = findViewById(R.id.emptyText);
        searchView = findViewById(R.id.searchText);

        Downloadtask downloadtask = new Downloadtask();
        context = this;

        try {
            downloadtask.execute("https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setupSearchView();
    }

    public class Downloadtask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            try {
                URL url = new URL(urls[0]);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String results = jsonObject.getString("results");
                JSONArray array = new JSONArray(results);
                imageData = new ArrayList();

                for (int i=0; i < array.length(); i++) {
                    JSONObject jsonpart = array.getJSONObject(i);

                    String backdropPath = jsonpart.getString("backdrop_path");
                    String posterPath = jsonpart.getString("poster_path");
                    String voteAverage = jsonpart.getString("vote_average");
                    String title = jsonpart.getString("original_title");
                    String overview = jsonpart.getString("overview");

                    if (!backdropPath.equals("") && !posterPath.equals("") && !voteAverage.equals("") && !title.equals("") && !overview.equals("")) {
                        imageData.add(new MovieData("https://image.tmdb.org/t/p/original"+backdropPath,"https://image.tmdb.org/t/p/w342"+posterPath,voteAverage,title,overview));
                    }
                }
                adapter = new MovieListAdapter(context,R.layout.movie_list,imageData);
                listView.setEmptyView(empty);
                listView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showImageDialog(String item,Context mContext) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View convertView = inflater.inflate(R.layout.image_dialog, null);

        alertDialog.setView(convertView);
        ImageView detailImage = convertView.findViewById(R.id.detailImage);
        new ImageLoadTask(item, detailImage).execute();
        alertDialog.setNegativeButton("Close",(dialog, which) -> {
            dialog.cancel();
        });
        alertDialog.show();
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Movie");
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        android.widget.Filter filter = adapter.getFilter();
        filter.filter(newText);
        if (TextUtils.isEmpty(newText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}