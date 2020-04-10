package com.blog.app.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blog.app.R;
import com.blog.app.model.PostResponse;

import org.jsoup.Jsoup;

import java.text.SimpleDateFormat;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder>{
    public List<PostResponse> postList ;
    private int rowLayout;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView posttitle,postdate,postcontent;

        public MyViewHolder(View view) {
            super(view);
            posttitle = view.findViewById(R.id.posttitle);
            postdate = view.findViewById(R.id.postdate);
            postcontent = view.findViewById(R.id.postcontent);


        }
    }

    public PostsAdapter(List<PostResponse> postList,int Rowlayout,Context context) {
        this.postList = postList;
        this.rowLayout = Rowlayout;
        this.context = context ;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        PostResponse post = postList.get(position);

        holder.posttitle.setText(Jsoup.parse(post.getTitle().getRendered()).text());

        String date = post.getDate_gmt().substring(0,10);
        holder.postdate.setText(formatDateFromOnetoAnother(date,"yyyy-MM-dd","MMMM dd, yyyy"));

        holder.postcontent.setText(Jsoup.parse(post.getExcerpt().getRendered()).text());

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static String formatDateFromOnetoAnother(String date,String givenformat,String resultformat) {

        String result = "";
        SimpleDateFormat sdf;
        SimpleDateFormat sdf1;

        try {
            sdf = new SimpleDateFormat(givenformat);
            sdf1 = new SimpleDateFormat(resultformat);
            result = sdf1.format(sdf.parse(date));
        }
        catch(Exception e) {
            e.printStackTrace();
            return "";
        }
        finally {
            sdf=null;
            sdf1=null;
        }
        return result;
    }



}
