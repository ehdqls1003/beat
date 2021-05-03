package com.kplo.beat;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class m_buy2_Adapter extends RecyclerView.Adapter<m_buy2_Adapter.ViewHolder> {

    private ArrayList<m_buy_item> mData = null ;
    String id;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked(int position, String setMusic_url);
        void ondownloadClicked(String music_url,String music_title);

    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_id,title,time ;
        ImageView user_img,img;
        Button buy,cancel;
        AppCompatButton download;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            title = itemView.findViewById(R.id.title) ;
            user_id = itemView.findViewById(R.id.user_id) ;
            time = itemView.findViewById(R.id.time) ;
            img = itemView.findViewById(R.id.img) ;
            user_img = itemView.findViewById(R.id.user_img);
            buy = itemView.findViewById(R.id.buy);
            cancel = itemView.findViewById(R.id.cancel);
            download = itemView.findViewById(R.id.download);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    m_buy2_Adapter(ArrayList<m_buy_item> list) {
        mData = list ;
    }


    @Override
    public m_buy2_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_m_buy2, parent, false) ;
        m_buy2_Adapter.ViewHolder vh = new m_buy2_Adapter.ViewHolder(view) ;

        SharedPreferences sf = context.getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull m_buy2_Adapter.ViewHolder holder, final int position) {

        final m_buy_item item = mData.get(position) ;

        Glide.with(holder.itemView.getContext())
                .load(item.getMusic_img_url())
                .apply(new RequestOptions().circleCrop().centerCrop())
                .centerCrop()
                .into(holder.img);

        Glide.with(holder.itemView.getContext())
                .load(item.getUser_img_url())
                .apply(new RequestOptions().circleCrop().centerCrop())
                .centerCrop()
                .circleCrop()
                .into(holder.user_img);
        holder.user_id.setText(item.getUser_id());
        holder.title.setText(item.getTitle());
        // String 타입을 Date 타입으로 변환

        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat fm2 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date to = fm.parse(item.getTime());
            String to2 = fm2.format(to);
            holder.time.setText(to2);
        } catch (ParseException e) {
            e.printStackTrace();
        }



        if (mListener != null) {
            final int pos = position;

            holder.download.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.ondownloadClicked(item.getMusic_url(),item.getTitle());

                }
            });
/*
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
