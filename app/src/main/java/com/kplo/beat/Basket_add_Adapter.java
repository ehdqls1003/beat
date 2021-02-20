package com.kplo.beat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Basket_add_Adapter extends RecyclerView.Adapter<Basket_add_Adapter.ViewHolder> {

    private ArrayList<playlist> mData = null ;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked(int position, int playnum,String playlist_n);

    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,playlist_n ;
        ImageView img;
        AppCompatButton following,follow;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            playlist_n = itemView.findViewById(R.id.playlist_n) ;
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    Basket_add_Adapter(ArrayList<playlist> list) {
        mData = list ;
    }


    @Override
    public Basket_add_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_basketadd, parent, false) ;
        Basket_add_Adapter.ViewHolder vh = new Basket_add_Adapter.ViewHolder(view) ;


        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull Basket_add_Adapter.ViewHolder holder, int position) {

        final playlist item = mData.get(position) ;

        holder.playlist_n.setText(item.getPlaylist_n());


        if (mListener != null) {
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onItemClicked(pos,mData.get(pos).getPlaynum(),mData.get(pos).getPlaylist_n());


                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}
