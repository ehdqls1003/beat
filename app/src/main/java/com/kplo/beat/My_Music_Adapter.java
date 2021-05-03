package com.kplo.beat;

import android.content.Context;
import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

public class My_Music_Adapter extends RecyclerView.Adapter<My_Music_Adapter.ViewHolder> {

    private ArrayList<Recent_Main_Item> mData = null ;
    private String u_id;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked(int position, String setMusic_url);
        void onm_PlayClicked(int position,int idx);
        void onMore_button(int idx,String title,String music_url,String music_img_url);

    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,name ;
        ImageView img,more_button,m_play;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            title = itemView.findViewById(R.id.title) ;
            name = itemView.findViewById(R.id.name) ;
            img = itemView.findViewById(R.id.img) ;
            more_button = itemView.findViewById(R.id.more_button);
            m_play = itemView.findViewById(R.id.m_play);



        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    My_Music_Adapter(ArrayList<Recent_Main_Item> list) {
        mData = list ;
    }


    @Override
    public My_Music_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_my_music, parent, false) ;
        My_Music_Adapter.ViewHolder vh = new My_Music_Adapter.ViewHolder(view) ;

        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = context.getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        u_id = sf.getString("id","");

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull My_Music_Adapter.ViewHolder holder, int position) {

        Recent_Main_Item item = mData.get(position) ;
        Glide.with(holder.itemView.getContext())
                .load(item.getMy_img_url())
                .apply(new RequestOptions().circleCrop().centerCrop())
                .centerCrop()
                .into(holder.img);
        /*holder.img.setText(text);*/
        holder.title.setText(item.getTitle());
        holder.name.setText(item.getId());
        if (!item.getId().equals(u_id)){
            holder.more_button.setVisibility(View.INVISIBLE);
        }

        if (mListener != null) {
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onItemClicked(pos,mData.get(pos).getMusic_url());
                    Log.e("20210328", "mData.get(pos).getMusic_url(): " +mData.get(pos).getMusic_url());


                }
            });

            holder.m_play.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onm_PlayClicked(pos,mData.get(pos).getIdx());

                }
            });

            holder.more_button.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onMore_button(mData.get(pos).getIdx(),mData.get(pos).getTitle(),mData.get(pos).getMusic_url(),mData.get(pos).getMy_img_url());
                    Log.e("20210328", "mData.get(pos).getMusic_url():2 " +mData.get(pos).getId());


                }
            });


        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}
