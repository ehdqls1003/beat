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
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class My_flowing_Adapter extends RecyclerView.Adapter<My_flowing_Adapter.ViewHolder> {

    private ArrayList<Flow_Item> mData = null ;
    private ArrayList<Flow_Item> mData2 = null ;
    boolean flow = false;
    String id;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked(int position, String setMusic_url);
        void onfollowClicked(int position, String setU_name);
        void onfollowingClicked(int position, String setU_name);

    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,name ;
        ImageView img;
        AppCompatButton following,follow;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            title = itemView.findViewById(R.id.title) ;
            name = itemView.findViewById(R.id.name) ;
            img = itemView.findViewById(R.id.img) ;
            following = itemView.findViewById(R.id.following) ;
            follow = itemView.findViewById(R.id.follow) ;
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    My_flowing_Adapter(ArrayList<Flow_Item> list,ArrayList<Flow_Item> list2) {
        mData = list ;
        mData2 = list2 ;
    }


    @Override
    public My_flowing_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_my_flowing, parent, false) ;
        My_flowing_Adapter.ViewHolder vh = new My_flowing_Adapter.ViewHolder(view) ;
        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = context.getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");
        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull My_flowing_Adapter.ViewHolder holder, int position) {

        final Flow_Item item = mData.get(position) ;
        Glide.with(holder.itemView.getContext())
                .load(item.getMy_img_url())
                .apply(new RequestOptions().circleCrop().centerCrop())
                .centerCrop()
                .circleCrop()
                .into(holder.img);
        /*holder.img.setText(text);*/
        holder.name.setText(item.getFriend_id());

        if (item.getMy_img_url().equals("")){
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.gibon)
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .circleCrop()
                    .into(holder.img);
            /*holder.img.setText(text);*/
            holder.name.setText(item.getFriend_id());
        }
        Log.e("호",""+mData2.size());
        for (int i = 0; i < mData2.size(); i++){
            Log.e("호","item.getMy_id()"+item.getMy_id());
            Log.e("호","mData2.get(i).getFriend_id("+mData2.get(i).getFriend_id());
            if (item.getFriend_id().equals(mData2.get(i).getFriend_id())){
                flow = true;
                break;
            }
        }
        Log.e("호","flow"+flow);
        if (!item.getFriend_id().equals(id)){
            if (flow){
                holder.follow.setVisibility(View.INVISIBLE);
                holder.following.setVisibility(View.VISIBLE);
                flow = false;
            }else{
                holder.follow.setVisibility(View.VISIBLE);
                holder.following.setVisibility(View.INVISIBLE);
            }
        }

        if (mListener != null) {
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {



                }
            });

            holder.follow.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onfollowClicked(pos,item.getFriend_id());


                }
            });

            holder.following.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onfollowingClicked(pos,item.getFriend_id());



                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}
