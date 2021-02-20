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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class NewClient_Adapter extends RecyclerView.Adapter<NewClient_Adapter.ViewHolder> {

    private ArrayList<show_message> mData = null ;
    String id;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked(int position, int room_id, String feed_id, String feed_img);
        void onmore_buttonClicked(int story_comment_num, String comment, int c_pos);

    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_id,last_chat,my_last_chat,time,my_time ;
        ImageView img,my_img;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            user_id = itemView.findViewById(R.id.user_id) ;
            time = itemView.findViewById(R.id.time) ;
            img = itemView.findViewById(R.id.img) ;
            last_chat = itemView.findViewById(R.id.last_chat);
            my_last_chat = itemView.findViewById(R.id.my_last_chat);
            my_img = itemView.findViewById(R.id.my_img);
            my_time = itemView.findViewById(R.id.my_time);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    NewClient_Adapter(ArrayList<show_message> list) {
        mData = list ;
    }


    @Override
    public NewClient_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_message, parent, false) ;
        NewClient_Adapter.ViewHolder vh = new NewClient_Adapter.ViewHolder(view) ;

        SharedPreferences sf = context.getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull NewClient_Adapter.ViewHolder holder, final int position) {

        final show_message item = mData.get(position) ;
        //나의 채팅이면
        if (item.getUser_id().equals(id)){
            holder.img.setVisibility(View.GONE);
            holder.last_chat.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.user_id.setVisibility(View.GONE);
            holder.my_time.setVisibility(View.VISIBLE);
            holder.my_last_chat.setVisibility(View.VISIBLE);
            Log.e("값",item.getMessage());
            holder.my_last_chat.setText(item.getMessage());
            String date = item.getTime();
            SimpleDateFormat original_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.KOREA);
            SimpleDateFormat original_format2 = new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
            SimpleDateFormat new_format = new SimpleDateFormat("a KK:mm", Locale.KOREA);
            original_format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            original_format2.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            new_format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            try{

                Date message_time_date = original_format.parse(date);
                String o = new_format.format(message_time_date);
                holder.my_time.setText(o);

            }catch (ParseException e){
                e.printStackTrace();
            }


        }else{
            holder.img.setVisibility(View.VISIBLE);
            holder.last_chat.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);
            holder.user_id.setVisibility(View.VISIBLE);
            holder.my_time.setVisibility(View.GONE);
            holder.my_last_chat.setVisibility(View.GONE);
            if (item.getUser_img().equals("")){
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.black)
                        .apply(new RequestOptions().circleCrop().centerCrop())
                        .centerCrop()
                        .circleCrop()
                        .into(holder.img);
            }else {
                Glide.with(holder.itemView.getContext())
                        .load(item.getUser_img())
                        .apply(new RequestOptions().circleCrop().centerCrop())
                        .centerCrop()
                        .circleCrop()
                        .into(holder.img);
            }
            holder.user_id.setText(item.getUser_id());
            holder.last_chat.setText(item.getMessage());
            holder.time.setText(item.getTime());
            String date = item.getTime();
            SimpleDateFormat original_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.KOREA);
            SimpleDateFormat original_format2 = new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
            SimpleDateFormat new_format = new SimpleDateFormat("a KK:mm", Locale.KOREA);
            original_format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            original_format2.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            new_format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            try{

                Date message_time_date = original_format.parse(date);


                String o = new_format.format(message_time_date);

                holder.time.setText(o);

            }catch (ParseException e){
                e.printStackTrace();
            }
        }

        /*if (item.getImg().equals("")){
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.black)
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .circleCrop()
                    .into(holder.img);
        }else {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImg())
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .circleCrop()
                    .into(holder.img);
        }*/

        holder.user_id.setText(item.getUser_id());


        if (mListener != null) {
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    /*mListener.onItemClicked(pos,mData.get(pos).getR_num(),mData.get(pos).getUser_id(),mData.get(pos).getImg());*/


                }
            });




        }

    }



    @Override
    public int getItemCount() {
        return mData.size();
    }
}
