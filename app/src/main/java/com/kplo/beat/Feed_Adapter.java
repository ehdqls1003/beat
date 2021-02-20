package com.kplo.beat;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class Feed_Adapter extends RecyclerView.Adapter<Feed_Adapter.ViewHolder> {

    private ArrayList<Feed_Item> mData = null ;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked(int position, int idx);

    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView story,name ;
        ImageView img;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            story = itemView.findViewById(R.id.story) ;
            img = itemView.findViewById(R.id.img) ;
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    Feed_Adapter(ArrayList<Feed_Item> list) {
        mData = list ;
    }


    @Override
    public Feed_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_feed, parent, false) ;
        Feed_Adapter.ViewHolder vh = new Feed_Adapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull Feed_Adapter.ViewHolder holder, int position) {

        final Feed_Item item = mData.get(position) ;
        if (item.getStory_img_url().equals("")){
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.black)
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .into(holder.img);
            holder.story.setText(item.getStory());
            holder.story.setTextColor(Color.parseColor("#ffffff"));
        }else{
            Glide.with(holder.itemView.getContext())
                    .load(item.getStory_img_url())
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .into(holder.img);
            holder.story.setVisibility(View.INVISIBLE);
        }


        if (mListener != null) {
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    Log.e("프래그먼트",""+item.getIdx());
                    mListener.onItemClicked(pos,item.getIdx());



                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
