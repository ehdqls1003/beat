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
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

import static android.content.Context.MODE_PRIVATE;

public class allFeed_Details_Adapter extends RecyclerView.Adapter<allFeed_Details_Adapter.ViewHolder> {

    private ArrayList<Feed_Item> mData = null ;
    private ArrayList<Feed_like_Item> mData2 = null ;
    String id;
    boolean like = false;

    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked(int position, String setMusic_url);
        void onu_nameClicked(int position, String u_name);
        void onheartClicked(int position, String id,String idx);
        void onheart_outlineClicked(int position, String id,String idx);
        void onCommentClicked(int idx);
    }

    private MyRecyclearViewClickListener mListener;

    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView u_img,f_img,heart,heart_outline;
        TextView u_name,heart_count,comment_count,f_story,comment;
        ViewPager mViewPager;
        Feed_ViewPagerAdapter mViewPagerAdapter;
        CircleIndicator indicator;
        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            u_img = itemView.findViewById(R.id.u_img) ;/*
            f_img = itemView.findViewById(R.id.f_img) ;*/
            mViewPager = itemView.findViewById(R.id.viewPagerMain);
            u_name = itemView.findViewById(R.id.u_name) ;
            heart_count = itemView.findViewById(R.id.heart_count) ;
            comment_count = itemView.findViewById(R.id.comment_count) ;
            f_story = itemView.findViewById(R.id.f_story) ;
            indicator = itemView.findViewById(R.id.indicator);
            heart = itemView.findViewById(R.id.heart);
            heart_outline = itemView.findViewById(R.id.heart_outline);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    allFeed_Details_Adapter(ArrayList<Feed_Item> list,ArrayList<Feed_like_Item> list2) {
        mData = list ;
        mData2 = list2;
    }


    @Override
    public allFeed_Details_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_feed_details, parent, false) ;
        allFeed_Details_Adapter.ViewHolder vh = new allFeed_Details_Adapter.ViewHolder(view);
        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = context.getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");
        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull final allFeed_Details_Adapter.ViewHolder holder, int position) {

        final Feed_Item item = mData.get(position) ;
        if (item.getMy_img_url().equals("")){
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.black)
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .circleCrop()
                    .into(holder.u_img);
        }else{
            Glide.with(holder.itemView.getContext())
                    .load(item.getMy_img_url())
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .circleCrop()
                    .into(holder.u_img);
        }

        holder.heart_count.setText(Integer.toString(item.getHeart_count()));
        holder.comment_count.setText(Integer.toString(item.getCommnet_count()));
        holder.u_name.setText(item.getId());
        for(int i = 0; i < mData2.size(); i++){
            Log.e("하트","mData2.get(i).getIdx()"+mData2.get(i).getIdx());
            if (Integer.toString(item.getIdx()).equals(mData2.get(i).getIdx())){
                Log.e("하트","mData2.get(i).getId()"+mData2.get(i).getId());
                Log.e("하트","id"+id);
                if(mData2.get(i).getId().equals(id)){
                    like = true;
                    break;
                }
            }
        }
        if (like){
            holder.heart.setVisibility(View.VISIBLE);
            holder.heart_outline.setVisibility(View.INVISIBLE);

            like = false;
        }else{
            holder.heart.setVisibility(View.INVISIBLE);
            holder.heart_outline.setVisibility(View.VISIBLE);
        }

        if (item.getStory().equals("")) {
        }else{
            holder.f_story.setText(item.getStory());
        }
        /*if(item.getStory_img_url().equals("")){
            holder.f_img.setVisibility(View.GONE);
        }else{
            holder.f_img.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(item.getStory_img_url())
                    .apply(new RequestOptions().circleCrop().centerCrop())
                    .centerCrop()
                    .into(holder.f_img);
        }*/

        ArrayList<String> images = new ArrayList<>();
        if (!item.getStory_img_url().equals("")){
            images.add(item.getStory_img_url());
            if (!item.getStory_img_url2().equals("")){
                images.add(item.getStory_img_url2());
                if (!item.getStory_img_url3().equals("")){
                    images.add(item.getStory_img_url3());
                    if (!item.getStory_img_url4().equals("")){
                        images.add(item.getStory_img_url4());
                        if (!item.getStory_img_url5().equals("")){
                            images.add(item.getStory_img_url5());
                        }
                    }
                }
            }
        }
        Log.e("뷰페이저 크기",""+item.getStory_img_url2());
        Log.e("뷰페이저 크기",""+images.size());
        holder.mViewPagerAdapter = new Feed_ViewPagerAdapter(holder.itemView.getContext(), images);
        // Adding the Adapter to the ViewPager
        holder.mViewPager.setAdapter(holder.mViewPagerAdapter);
        holder.indicator.setViewPager(holder.mViewPager);

        if (item.getStory_img_url().equals("")){
            holder.mViewPager.setVisibility(View.GONE);
        }else {
            holder.mViewPager.setVisibility(View.VISIBLE);
        }
/*
        holder.heart_count.setText(String.valueOf(item.getHeart_count()));
        holder.comment_count.setText(String.valueOf(item.getCommnet_count()));*/


        if (mListener != null) {
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    Log.e("프래그먼트",""+pos);



                }
            });

            holder.u_img.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onu_nameClicked(pos,item.getId());
                    Log.e("프래그먼트",""+pos);

                }
            });
            holder.u_name.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onu_nameClicked(pos,item.getId());
                    Log.e("프래그먼트",""+pos);

                }
            });

            holder.heart.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onheartClicked(pos,id,Integer.toString(item.getIdx()));
                    holder.heart.setVisibility(View.INVISIBLE);
                    holder.heart_outline.setVisibility(View.VISIBLE);

                    for (int i = 0; i < mData.size(); i++){
                        if (mData.get(i).getIdx() == item.getIdx()){
                            mData.get(i).setHeart_count(item.getHeart_count()-1);
                            break;
                        }
                    }

                    for (int i = 0; i < mData2.size(); i++){
                        if (mData2.get(i).getIdx().equals(Integer.toString(item.getIdx()))){
                            if (mData2.get(i).getId().equals(id)){
                                mData2.remove(i);
                                break;
                            }
                        }
                    }
                    notifyDataSetChanged();

                }
            });
            holder.heart_outline.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onheart_outlineClicked(pos,id,Integer.toString(item.getIdx()));
                    holder.heart.setVisibility(View.VISIBLE);
                    holder.heart_outline.setVisibility(View.INVISIBLE);

                    Feed_like_Item items = new Feed_like_Item();
                    items.setId(id);
                    items.setIdx(Integer.toString(item.getIdx()));
                    mData2.add(items);
                    for (int i = 0; i < mData.size(); i++){
                        if (mData.get(i).getIdx() == item.getIdx()){
                            mData.get(i).setHeart_count(item.getHeart_count()+1);
                            break;
                        }
                    }
                    notifyDataSetChanged();
                    Log.e("프래그먼트",""+pos);

                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onCommentClicked(item.getIdx());



                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
