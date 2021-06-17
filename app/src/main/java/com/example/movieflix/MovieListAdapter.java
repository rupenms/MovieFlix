package com.example.movieflix;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MovieListAdapter extends BaseAdapter implements Filterable {

    private int resourceLayout;
    private Context mContext;
    MainActivity mainActivity;

    private ArrayList<MovieData> imageList;
    private ArrayList<MovieData> temp;

    public MovieListAdapter(Context context, int resource, ArrayList<MovieData> imageList) {
        super();
        this.resourceLayout = resource;
        this.mContext = context;
        this.imageList = imageList;
        mainActivity = new MainActivity();
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ImageView imageView,deleteImage;
        TextView title,overview;
        String imageURL;
        RelativeLayout relativeLayout;

        if (view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(resourceLayout, null);
        }

        if (view != null) {
            imageView = view.findViewById(R.id.movieImage);
            title = view.findViewById(R.id.title);
            overview = view.findViewById(R.id.overview);
            deleteImage = view.findViewById(R.id.delete);
            relativeLayout = view.findViewById(R.id.imageLayout);

            if (Double.parseDouble(imageList.get(position).getVote()) > 7) {
                imageURL = imageList.get(position).getBackDrop();
                new ImageLoadTask(imageURL, imageView).execute();
                title.setVisibility(View.GONE);
                overview.setVisibility(View.GONE);
            } else {
                imageURL = imageList.get(position).getPoster();
                new ImageLoadTask(imageURL, imageView).execute();
                title.setVisibility(View.VISIBLE);
                overview.setVisibility(View.VISIBLE);
                title.setText(imageList.get(position).getTitle());
                overview.setText(imageList.get(position).getOverview());
            }

            deleteImage.setOnClickListener(v -> {
                showDeleteDialog(position,mContext);
            });

            relativeLayout.setOnClickListener(v -> {
                mainActivity.showImageDialog(imageURL,mContext);
            });
        }
        return view;
    }

    private void showDeleteDialog(int position,Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("Delete Item");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton("Yes",(dialog, which) -> {
            Integer index = position;
            if (imageList.get(index) != null) {
                imageList.remove(index.intValue());
            }
            notifyDataSetChanged();
        });
        alert.setNegativeButton("No",(dialog, which) -> {
            dialog.cancel();
        });
        alert.show();
    }


    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filterResults = new FilterResults();
                final ArrayList<MovieData> results = new ArrayList();
                if (temp == null)
                    temp = imageList;
                if (constraint != null) {
                    if (temp != null && temp.size() > 0) {
                        for (final MovieData movie : temp) {
                            if (movie.getTitle().toLowerCase().contains(constraint.toString()))
                                results.add(movie);
                            else
                                Toast.makeText(mContext, "No Results Found", Toast.LENGTH_LONG).show();
                        }
                    }
                    filterResults.values = results;
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {
                imageList = (ArrayList<MovieData>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}