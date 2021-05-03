package com.kplo.beat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Show_highlight_Adapter extends RecyclerView.Adapter<Show_highlight_Adapter.ViewHolder> {

    private ArrayList<Show_highlight_Item> mData = null ;
    boolean play;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked();
        void getMusic_url(String music_url,int time_s,int time_e);
        void user_id_Clicked(String feed_id);
        void user_img_Cliked(String feed_id);
        void heart_Cliked(int m_idx);
        void heart_outline_Cliked(int m_idx);
        void comment_cliked(int music_h_num);

    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_id,title,heart_count ;
        ImageView img_blur,img,user_img,comment,heart_outline,heart,play,pause;
        ConstraintLayout con;

        ViewHolder(View itemView) {
            super(itemView) ;

            con = itemView.findViewById(R.id.con);
            Log.e("20210331","ViewHolder");
            // 뷰 객체에 대한 참조. (hold strong reference)
            user_id = itemView.findViewById(R.id.user_id);
            title = itemView.findViewById(R.id.title);
            heart_count = itemView.findViewById(R.id.heart_count);
/*
            img_blur = itemView.findViewById(R.id.img_blur);*/
            img = itemView.findViewById(R.id.img);
            user_img = itemView.findViewById(R.id.user_img);
            comment = itemView.findViewById(R.id.comment);
            heart_outline = itemView.findViewById(R.id.heart_outline);
            heart = itemView.findViewById(R.id.heart);
            play = itemView.findViewById(R.id.play);
            pause = itemView.findViewById(R.id.pause);

        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    Show_highlight_Adapter(ArrayList<Show_highlight_Item> list) {
        mData = list ;
    }


    @Override
    public Show_highlight_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("20210331","onCreateViewHolder");
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_showhighlight, parent, false) ;
        Show_highlight_Adapter.ViewHolder vh = new Show_highlight_Adapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull final Show_highlight_Adapter.ViewHolder holder, int position) {
        Log.e("20210331","onBindViewHolder");
        final Show_highlight_Item item = mData.get(position);

        //이미지
        Glide.with(holder.itemView.getContext())
                .load(item.getMusic_img())
                .centerCrop()
                .into(holder.img);
/*
        //이미지 배경 - 블러
        Glide.with(holder.itemView.getContext())
                .load(item.getMusic_img())
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25,3)))
                .into(holder.img_blur);*/

        holder.user_id.setText(item.getUser_id());
        holder.title.setText(item.getTitle());
        holder.heart_count.setText(String.valueOf(item.getHeart_count()));

        if (item.getUser_img().equals("")){
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.gibon)
                    .centerCrop()
                    .circleCrop()
                    .into(holder.user_img);

        }else{
            Glide.with(holder.itemView.getContext())
                    .load(item.getUser_img())
                    .centerCrop()
                    .circleCrop()
                    .into(holder.user_img);
        }
        if (item.getHeart() == 1){
            holder.heart.setVisibility(View.VISIBLE);
            holder.heart_outline.setVisibility(View.GONE);
        }else{
            holder.heart_outline.setVisibility(View.VISIBLE);
            holder.heart.setVisibility(View.GONE);
        }
        holder.play.setVisibility(View.VISIBLE);
        holder.pause.setVisibility(View.INVISIBLE);



        if (mListener != null) {
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    mListener.onItemClicked();
                    if (play){
                        holder.pause.setVisibility(View.INVISIBLE);
                        holder.play.setVisibility(View.VISIBLE);
                    }else{
                        holder.play.setVisibility(View.INVISIBLE);
                        holder.pause.setVisibility(View.VISIBLE);
                    }

                }
            });
            holder.user_id.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.user_id_Clicked(item.getUser_id());


                }
            });
            holder.user_img.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.user_img_Cliked(item.getUser_id());


                }
            });

            holder.heart.setOnClickListener(new View.OnClickListener(){
                //좋아요 감소
                @Override
                public void onClick(View v) {
                    mListener.heart_Cliked(item.getM_idx());
                    holder.heart.setVisibility(View.INVISIBLE);
                    holder.heart_outline.setVisibility(View.VISIBLE);

                    String h_c_s = holder.heart_count.getText().toString();
                    int h_c_i = Integer.parseInt(h_c_s) - 1;
                    holder.heart_count.setText(String.valueOf(h_c_i));

                }
            });

            holder.heart_outline.setOnClickListener(new View.OnClickListener(){
                //좋아요 증가
                @Override
                public void onClick(View v) {
                    mListener.heart_outline_Cliked(item.getM_idx());
                    holder.heart_outline.setVisibility(View.INVISIBLE);
                    holder.heart.setVisibility(View.VISIBLE);

                    String h_c_s = holder.heart_count.getText().toString();
                    int h_c_i = Integer.parseInt(h_c_s) + 1;
                    holder.heart_count.setText(String.valueOf(h_c_i));
                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.comment_cliked(item.getMusic_h_num());


                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void get_music(int position){
        mListener.getMusic_url(mData.get(position).getMusic_url(),mData.get(position).getTime_s(),mData.get(position).getTime_e());
        notifyDataSetChanged();
    }
    public void get_play(boolean play){
        this.play = play;
    }

}
