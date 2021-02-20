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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class write_story_Adapter extends RecyclerView.Adapter<write_story_Adapter.ViewHolder> {

    private ArrayList<String> mData = null ;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked(int position, String setMusic_url);

    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img,minus;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            img = itemView.findViewById(R.id.img) ;
            minus = itemView.findViewById(R.id.minus) ;
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    write_story_Adapter(ArrayList<String> list) {
        mData = list ;
    }


    @Override
    public write_story_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_write_story, parent, false) ;
        write_story_Adapter.ViewHolder vh = new write_story_Adapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull write_story_Adapter.ViewHolder holder, int position) {

        final String item = mData.get(position) ;
        Glide.with(holder.itemView.getContext())
                .load(item)
                .apply(new RequestOptions().circleCrop().centerCrop())
                .centerCrop()
                .circleCrop()
                .into(holder.img);

        if (mListener != null) {
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {


                }
            });

            holder.minus.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    mData.remove(pos);
                    notifyDataSetChanged();

                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
