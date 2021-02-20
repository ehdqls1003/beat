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

public class User_Playlist_Adapter extends RecyclerView.Adapter<User_Playlist_Adapter.ViewHolder> {

    private ArrayList<User_Music_List> mData = null ;
    int item_position;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked(int position, String setMusic_url);
        void onMore_buttonClicked(int pos, String Music_id,int music_num);
    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,name,playing,isplaying;
        ImageView img,more_button;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            title = itemView.findViewById(R.id.title) ;
            name = itemView.findViewById(R.id.name) ;
            img = itemView.findViewById(R.id.img) ;
            more_button = itemView.findViewById(R.id.more_button);
            playing = itemView.findViewById(R.id.playing);
            isplaying = itemView.findViewById(R.id.isplaying);

        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    User_Playlist_Adapter(ArrayList<User_Music_List> list) {
        mData = list ;
    }


    @Override
    public User_Playlist_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_my_play_list, parent, false) ;
        User_Playlist_Adapter.ViewHolder vh = new User_Playlist_Adapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull User_Playlist_Adapter.ViewHolder holder, int position) {

        User_Music_List item = mData.get(position) ;
        Glide.with(holder.itemView.getContext())
                .load(item.getMusic_img())
                .apply(new RequestOptions().circleCrop().centerCrop())
                .centerCrop()
                .into(holder.img);
        /*holder.img.setText(text);*/
        holder.title.setText(item.getMusic_title());
        holder.name.setText(item.getMusic_id());
        Log.e("리사이클러뷰",""+item.getMusic_img());

        if (item_position == position){
            holder.title.setTextColor(Color.parseColor("#008000"));
            holder.name.setTextColor(Color.parseColor("#008000"));
            if (MyService.isPlaying){
                holder.playing.setVisibility(View.VISIBLE);
                holder.isplaying.setVisibility(View.INVISIBLE);
            }else{
                holder.playing.setVisibility(View.INVISIBLE);
                holder.isplaying.setVisibility(View.VISIBLE);
            }
        }else{

            holder.title.setTextColor(Color.parseColor("#000000"));
            holder.name.setTextColor(Color.parseColor("#000000"));
            holder.playing.setVisibility(View.INVISIBLE);
            holder.isplaying.setVisibility(View.INVISIBLE);
        }


        if (mListener != null) {
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onItemClicked(pos,mData.get(pos).getMusic_url());
                    Log.i("뭐얌", "mData.get(pos).getMusic_url(): " +mData.get(pos).getMusic_url());


                }
            });
            holder.more_button.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onMore_buttonClicked(pos,mData.get(pos).getMusic_id(),mData.get(pos).getList_num());
                    Log.i("뭐얌", "mData.get(pos).getMusic_url(): " +mData.get(pos).getMusic_url());


                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void playing(String value6){
        item_position = Integer.parseInt(value6);
        notifyDataSetChanged();

    }

    public void isplaying(){
        notifyDataSetChanged();
    }
}
