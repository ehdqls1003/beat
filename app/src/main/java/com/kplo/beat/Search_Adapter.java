package com.kplo.beat;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Search_Adapter extends RecyclerView.Adapter<Search_Adapter.ViewHolder> {

    private ArrayList<Search_Item> mData = null ;
    String id;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked1( String user_id);

    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_id;


        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            user_id = itemView.findViewById(R.id.user_id) ;
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    Search_Adapter(ArrayList<Search_Item> list) {
        mData = list ;
    }


    @Override
    public Search_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_search, parent, false) ;
        Search_Adapter.ViewHolder vh = new Search_Adapter.ViewHolder(view) ;

        SharedPreferences sf = context.getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull final Search_Adapter.ViewHolder holder, final int position) {

        final Search_Item item = mData.get(position) ;

        holder.user_id.setText(item.getSearch());



        if (mListener != null) {
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                        mListener.onItemClicked1(item.getSearch());


                }
            });


        }

    }



    @Override
    public int getItemCount() {
        return mData.size();
    }
}
