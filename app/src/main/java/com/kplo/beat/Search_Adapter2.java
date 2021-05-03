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

public class Search_Adapter2 extends RecyclerView.Adapter<Search_Adapter2.ViewHolder> {

    private ArrayList<Flow_Item> mData = null ;
    String id;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked2(String feed_id);

    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_id;
        ImageView img;


        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            user_id = itemView.findViewById(R.id.user_id) ;
            img = itemView.findViewById(R.id.img) ;
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    Search_Adapter2(ArrayList<Flow_Item> list) {
        mData = list ;
    }


    @Override
    public Search_Adapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_search2, parent, false) ;
        Search_Adapter2.ViewHolder vh = new Search_Adapter2.ViewHolder(view) ;

        SharedPreferences sf = context.getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull Search_Adapter2.ViewHolder holder, final int position) {

        final Flow_Item item = mData.get(position) ;



        holder.user_id.setText(item.getFriend_id());

        if (item.getMy_img_url().equals("")){
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.gibon)
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .circleCrop()
                    .into(holder.img);
        }else{
            Glide.with(holder.itemView.getContext())
                    .load(item.getMy_img_url())
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .circleCrop()
                    .into(holder.img);
        }



        if (mListener != null) {
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                        mListener.onItemClicked2(item.getFriend_id());

                }
            });


        }

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }
}
