package com.kplo.beat;

import android.content.Context;
import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

public class Comment_highlight_Adapter extends RecyclerView.Adapter<Comment_highlight_Adapter.ViewHolder> {

    private ArrayList<highlight_comment> mData = null ;
    String id;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked(int position, String setMusic_url);
        void onmore_buttonClicked(int story_comment_num, String comment, int c_pos);
        void onIdClicked(String feed_id);

    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,name ;
        ImageView img,more_button;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            title = itemView.findViewById(R.id.title) ;
            name = itemView.findViewById(R.id.name) ;
            img = itemView.findViewById(R.id.img) ;
            more_button = itemView.findViewById(R.id.more_button);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    Comment_highlight_Adapter(ArrayList<highlight_comment> list) {
        mData = list ;
    }


    @Override
    public Comment_highlight_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_comment, parent, false) ;
        Comment_highlight_Adapter.ViewHolder vh = new Comment_highlight_Adapter.ViewHolder(view) ;

        SharedPreferences sf = context.getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull Comment_highlight_Adapter.ViewHolder holder, final int position) {

        final highlight_comment item = mData.get(position) ;

        if (item.getMy_img_url().equals("")){
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.black)
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .circleCrop()
                    .into(holder.img);
        }else {

            Glide.with(holder.itemView.getContext())
                    .load(item.getMy_img_url())
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .circleCrop()
                    .into(holder.img);
        }


        /*holder.img.setText(text);*/
        holder.title.setText(item.getComment_id());
        holder.name.setText(item.getComment());

        if(item.getComment_id().equals(id)){
            holder.more_button.setVisibility(View.VISIBLE);
        }else{
            holder.more_button.setVisibility(View.INVISIBLE);
        }

        if (mListener != null) {
            final int pos = position;

            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mListener.onIdClicked(item.getComment_id());

                }
            });

            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mListener.onIdClicked(item.getComment_id());

                }
            });
/*
            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onItemClicked(pos,mData.get(pos).getMy_img_url());


                }
            });

            holder.more_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mListener.onmore_buttonClicked(item.story_comment_num,item.getComment(),pos);

                }
            });*/


        }

    }



    @Override
    public int getItemCount() {
        return mData.size();
    }
}
