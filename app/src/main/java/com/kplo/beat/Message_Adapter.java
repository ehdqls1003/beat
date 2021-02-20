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

public class Message_Adapter extends RecyclerView.Adapter<Message_Adapter.ViewHolder> {

    private ArrayList<Room_List> mData = null ;
    String id;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked(int position, int room_id,String feed_id,String feed_img);
        void onmore_buttonClicked(int story_comment_num, String comment, int c_pos);

    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_id,last_chat,time ;
        ImageView img,more_button;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            user_id = itemView.findViewById(R.id.user_id) ;
            time = itemView.findViewById(R.id.time) ;
            img = itemView.findViewById(R.id.img) ;
            last_chat = itemView.findViewById(R.id.last_chat);
            more_button = itemView.findViewById(R.id.more_button);

        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    Message_Adapter(ArrayList<Room_List> list) {
        mData = list ;
    }


    @Override
    public Message_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_room_list, parent, false) ;
        Message_Adapter.ViewHolder vh = new Message_Adapter.ViewHolder(view) ;

        SharedPreferences sf = context.getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull Message_Adapter.ViewHolder holder, final int position) {

        final Room_List item = mData.get(position) ;

        if (item.getImg().equals("")){
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
        }

        holder.user_id.setText(item.getUser_id());

        if (item.getMessage() == null){
            holder.itemView.setVisibility(View.INVISIBLE);
        }else{
            String date = item.getTime();
            SimpleDateFormat original_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.KOREA);
            SimpleDateFormat original_format2 = new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
            SimpleDateFormat new_format = new SimpleDateFormat("a KK:mm", Locale.KOREA);
            original_format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            original_format2.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            new_format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            try{
                //현재시간
                Date time = new Date();
                //현재시간을 ORIGINAL_FORMATE2 형식으로 변환
                String current_time = original_format2.format(time);
                Log.e("시간","current_time"+current_time);
                Date current_date = original_format2.parse(current_time);
                Date message_date = original_format2.parse(date);

                String message_uptime = original_format2.format(message_date);
                Log.e("시간","message_uptime"+message_uptime);

                Date message_time_date = original_format.parse(date);
                String message_time = new_format.format(message_time_date);

                Date d1 = original_format2.parse(current_time);
                Date d2 = original_format2.parse(message_uptime);


                String o = new_format.format(message_time_date);
                String p = original_format2.format(message_time_date);


                // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
                long diff = d1.getTime() - d2.getTime();
                long diffDays = diff / (24 * 60 * 60 * 1000);
                int dif = (int)diffDays;

                Log.e("시간","diffDays"+diffDays);
                if (dif == 0){
                    holder.time.setText(o);
                }else if(dif == 1){
                    holder.time.setText("어제");
                }else if(dif > 1){
                    holder.time.setText(p);
                }

            }catch (ParseException e){
                e.printStackTrace();
            }

            /*String input = item.getTime();
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat outputFormat = new SimpleDateFormat("a KK:mm");

            Date time = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

            try {
                Date input_date = formatter.parse(input);

            } catch (ParseException e) {
                e.printStackTrace();
            }


            //현재시간
            String end = formatter.format(time);
            //메세지시간
            Date start2 = null;
            try {
                start2 = formatter.parse(input);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String start = formatter.format(start2);

            Date beginDate = null;
            try {
                beginDate = formatter.parse(start);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date endDate = null;
            try {
                endDate = formatter.parse(end);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long diff = endDate.getTime() - beginDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);


            if (diffDays == 0){
                try {
                    holder.time.setText(outputFormat.format(inputFormat.parse(input)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else if(diffDays == 1){
                holder.time.setText("어제");
            }else if(diffDays > 1){
                holder.time.setText(start);
            }*/
            //holder.time.setText(item.getTime());
            holder.last_chat.setText(item.getMessage());
        }


        if (mListener != null) {
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onItemClicked(pos,mData.get(pos).getR_num(),mData.get(pos).getUser_id(),mData.get(pos).getImg());


                }
            });

            holder.more_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //mListener.onmore_buttonClicked(item.story_comment_num,item.getComment(),pos);

                }
            });


        }

    }



    @Override
    public int getItemCount() {
        return mData.size();
    }
}
