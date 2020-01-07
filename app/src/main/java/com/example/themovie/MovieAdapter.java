package com.example.themovie;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.themovie.utilities.JsonMoviesUtils;
import com.example.themovie.utilities.UrlUtils;
import com.squareup.picasso.Picasso;

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    final private ListItemClickListener mOnClickListener;
    private static int viewHolderCount;
    private int mNumberItems;
    private Context globalContext;
    private String [] JsonMoviesArray;

    //an interface that will be override in the main activity, to use it when user click at a movie post:
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    //movie adapter constructor:
    public MovieAdapter(int numberOfItems, ListItemClickListener listener, String [] JsonMoviesArr) {
        mNumberItems = numberOfItems;
        mOnClickListener = listener;
        JsonMoviesArray = JsonMoviesArr;
        viewHolderCount = 0;
    }

    public MovieAdapter(int numberOfItems,  String [] favoriteMoviesArr, ListItemClickListener listener) {
        mNumberItems = numberOfItems;
        mOnClickListener = listener;
        viewHolderCount = 0;
    }


    //when a view holder been created for the first time, it will have the next:
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //we get the context here:
        globalContext = viewGroup.getContext();

        // get the layout id of the single item:
        int layoutIdForListItem = R.layout.movie_list_item;

        //inflater:
        LayoutInflater inflater = LayoutInflater.from(globalContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        //create a new view holder:
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        viewHolderCount++;
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "
                + viewHolderCount);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }


    class MovieViewHolder extends RecyclerView.ViewHolder
            implements OnClickListener {

        ImageView imgView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.movie_iv);
            itemView.setOnClickListener(this);

        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex) {
            //when the view holder been bond, we do the next:
            ///get the movie json string for a single movie:
            String movieJsonStr =JsonMoviesArray[listIndex];

            ///get the path of the poster of this movie:
            String imgPath = JsonMoviesUtils.imageMoviePath(movieJsonStr);

            //then we make the full URL for the movie poster:
            String imgUrl = UrlUtils.buildImageUrl(imgPath);

            // here we populate the image view with the image:
            Picasso.with(globalContext).load(imgUrl)
                    .into(imgView);

        }


        /**
         * Called whenever a user clicks on an item in the list.
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);

        }
    }


}
