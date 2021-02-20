package com.kplo.beat;

import android.content.Context;
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

public class Recent_main_Adapter extends RecyclerView.Adapter<Recent_main_Adapter.ViewHolder> {

    private ArrayList<Recent_Main_Item> mData = null ;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,name ;
        ImageView img;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            title = itemView.findViewById(R.id.title) ;
            name = itemView.findViewById(R.id.name) ;
            img = itemView.findViewById(R.id.img) ;
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    Recent_main_Adapter(ArrayList<Recent_Main_Item> list) {
        mData = list ;
    }


    @Override
    public Recent_main_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item, parent, false) ;
        Recent_main_Adapter.ViewHolder vh = new Recent_main_Adapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull Recent_main_Adapter.ViewHolder holder, int position) {

        Recent_Main_Item item = mData.get(position) ;
        Glide.with(holder.itemView.getContext())
                .load(item.getMy_img_url())
                .apply(new RequestOptions().circleCrop().centerCrop())
                .centerCrop()
                .circleCrop()
                .into(holder.img);
        /*holder.img.setText(text);*/
        holder.title.setText(item.getTitle());
        holder.name.setText(item.getId());



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
