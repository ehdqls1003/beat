package com.kplo.beat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class Feed_Details_Adapter extends RecyclerView.Adapter<Feed_Details_Adapter.ViewHolder> {

    private ArrayList<Feed_Item> mData = null ;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked(int position, String setMusic_url);
        void onCommentClicked(int idx);

    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView u_img,f_img,heart;
        TextView u_name,heart_count,comment_count,f_story,comment;
        ViewPager mViewPager;
        Feed_ViewPagerAdapter mViewPagerAdapter;
        CircleIndicator indicator;
        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            u_img = itemView.findViewById(R.id.u_img) ;/*
            f_img = itemView.findViewById(R.id.f_img) ;*/
            mViewPager = itemView.findViewById(R.id.viewPagerMain);
            u_name = itemView.findViewById(R.id.u_name) ;
            heart_count = itemView.findViewById(R.id.heart_count) ;
            comment = itemView.findViewById(R.id.comment);
            comment_count = itemView.findViewById(R.id.comment_count) ;
            f_story = itemView.findViewById(R.id.f_story) ;
            indicator = itemView.findViewById(R.id.indicator);
            heart= itemView.findViewById(R.id.heart);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    Feed_Details_Adapter(ArrayList<Feed_Item> list) {
        mData = list ;
    }


    @Override
    public Feed_Details_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_feed_details, parent, false) ;
        Feed_Details_Adapter.ViewHolder vh = new Feed_Details_Adapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull final Feed_Details_Adapter.ViewHolder holder, int position) {

        final Feed_Item item = mData.get(position) ;
        if (item.getMy_img_url().equals("")){
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.black)
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .circleCrop()
                    .into(holder.u_img);
        }else{
            Glide.with(holder.itemView.getContext())
                    .load(item.getMy_img_url())
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .circleCrop()
                    .into(holder.u_img);
        }

        holder.heart_count.setText(Integer.toString(item.getHeart_count()));
        holder.comment_count.setText(Integer.toString(item.getCommnet_count()));
        holder.u_name.setText(item.getId());

        if (item.getStory().equals("")) {
        }else{
            holder.f_story.setText(item.getStory());
        }
        /*if(item.getStory_img_url().equals("")){
            holder.f_img.setVisibility(View.GONE);
        }else{
            holder.f_img.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(item.getStory_img_url())
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .into(holder.f_img);
        }*/

        ArrayList<String> images = new ArrayList<>();
        if (!item.getStory_img_url().equals("")){
            images.add(item.getStory_img_url());
            if (!item.getStory_img_url2().equals("")){
                images.add(item.getStory_img_url2());
                if (!item.getStory_img_url3().equals("")){
                    images.add(item.getStory_img_url3());
                    if (!item.getStory_img_url4().equals("")){
                        images.add(item.getStory_img_url4());
                        if (!item.getStory_img_url5().equals("")){
                            images.add(item.getStory_img_url5());
                        }
                    }
                }
            }
        }
        Log.e("뷰페이저 크기",""+item.getStory_img_url2());
        Log.e("뷰페이저 크기",""+images.size());
        holder.mViewPagerAdapter = new Feed_ViewPagerAdapter(holder.itemView.getContext(), images);
        // Adding the Adapter to the ViewPager
        holder.mViewPager.setAdapter(holder.mViewPagerAdapter);
        holder.indicator.setViewPager(holder.mViewPager);

        if (item.getStory_img_url().equals("")){
            holder.mViewPager.setVisibility(View.GONE);
        }else {
            holder.mViewPager.setVisibility(View.VISIBLE);
        }
/*
        holder.heart_count.setText(String.valueOf(item.getHeart_count()));
        holder.comment_count.setText(String.valueOf(item.getCommnet_count()));*/


        if (mListener != null) {
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    Log.e("프래그먼트",""+pos);



                }
            });
            holder.comment.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onCommentClicked(item.getIdx());



                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
