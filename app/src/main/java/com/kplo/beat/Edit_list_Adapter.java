package com.kplo.beat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Edit_list_Adapter extends RecyclerView.Adapter<Edit_list_Adapter.ViewHolder>implements ItemTouchHelperListener {

    private ArrayList<User_Music_List> mData = null ;
    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);
    private ArrayList<Integer> delete = new ArrayList<>();
    private ArrayList<Integer> insert = new ArrayList<>();
    private ArrayList<Integer> listsun = new ArrayList<>();
    private String id;
    private Context context;
    int item_position;

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //이동할 객체 저장
        User_Music_List person = mData.get(fromPosition);
        //이동할 객체 삭제
        mData.remove(fromPosition);
        //이동하고 싶은 position에 추가
        mData.add(toPosition,person);
        //Adapter에 데이터 이동알림
        notifyItemMoved(fromPosition,toPosition);
        Log.e("이동",""+toPosition);

        listsun.clear();
        for (int i = 0; i<mData.size(); i++){
            listsun.add(mData.get(i).getList_num());
        }
        Log.e("리스트사이즈?","+"+listsun.size());
        setStringArrayPref(context,"listsun"+id,listsun);
        return true;

    }


    //버튼정의
    public interface MyRecyclearViewClickListener {
        void onItemClicked(int position, String setMusic_url,int count);
        void counted(int count);
        void Arraylisted(ArrayList<Integer> delete,ArrayList<Integer> list,ArrayList<Integer> insert);
    }

    public interface OnStartDragListener{

    }

    private MyRecyclearViewClickListener mListener;


    //외부에서 지정할수있도록?
    public void setOnClickListener(MyRecyclearViewClickListener listener) {
        mListener = listener;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,name,playing,isplaying ;
        ImageView img,edit_button;

        ViewHolder(View itemView) {
            super(itemView) ;
            Log.e("순서","ViewHolder");

            // 뷰 객체에 대한 참조. (hold strong reference)
            title = itemView.findViewById(R.id.title) ;
            name = itemView.findViewById(R.id.name) ;
            img = itemView.findViewById(R.id.img) ;
            edit_button = itemView.findViewById(R.id.edit_button);
            playing = itemView.findViewById(R.id.playing);
            isplaying = itemView.findViewById(R.id.isplaying);

        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    Edit_list_Adapter(ArrayList<User_Music_List> list) {
        mData = list ;
        Log.e("어뎁뎁뎁","뎁뎁");
        Log.e("순서","Edit_list_Adapter");

    }


    @Override
    public Edit_list_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        this.context = context;


        Log.e("순서","Edit_list_Adapter.ViewHolder");
        SharedPreferences sf = context.getSharedPreferences("sFile",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        id = sf.getString("id","");

        listsun = getStringArrayPref(context,"listsun"+id);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_view_item_edit_list, parent, false) ;
        Edit_list_Adapter.ViewHolder vh = new Edit_list_Adapter.ViewHolder(view) ;


        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull final Edit_list_Adapter.ViewHolder holder, final int position) {

        final User_Music_List item = mData.get(position) ;
        Glide.with(holder.itemView.getContext())
                .load(item.getMusic_img())
                .apply(new RequestOptions().circleCrop().centerCrop())
                .centerCrop()
                .into(holder.img);
        /*holder.img.setText(text);*/
        holder.title.setText(item.getMusic_title());
        holder.name.setText(item.getMusic_id());
        Log.e("리사이클러뷰",""+item.getMusic_img());

        if (item_position == position){
            holder.title.setTextColor(Color.parseColor("#008000"));
            holder.name.setTextColor(Color.parseColor("#008000"));
            if (MyService.isPlaying){
                holder.playing.setVisibility(View.VISIBLE);
                holder.isplaying.setVisibility(View.INVISIBLE);
            }else{
                holder.playing.setVisibility(View.INVISIBLE);
                holder.isplaying.setVisibility(View.VISIBLE);
            }
        }else{

            holder.title.setTextColor(Color.parseColor("#000000"));
            holder.name.setTextColor(Color.parseColor("#000000"));
            holder.playing.setVisibility(View.INVISIBLE);
            holder.isplaying.setVisibility(View.INVISIBLE);
        }

        if ( mSelectedItems.get(position, false) ){
            holder.itemView.setBackgroundColor(Color.BLUE);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);

        }


        if (mListener != null) {
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Log.e("아이템클릭",""+mData.get(pos).getMusic_idx());
                    if ( mSelectedItems.get(position, false) ){
                        mSelectedItems.put(position, false);
                        v.setBackgroundColor(Color.WHITE);
                        if (delete != null){
                            for (int i = 0; i < delete.size();i++){
                                if(delete.get(i) == mData.get(position).getList_num()){
                                    delete.remove(i);
                                    insert.remove(i);
                                    break;
                                }
                            }
                        }
                    } else {
                        mSelectedItems.put(position, true);
                        v.setBackgroundColor(Color.BLUE);
                        delete.add(mData.get(position).getList_num());
                        insert.add(mData.get(position).getMusic_idx());
                    }

                    mListener.onItemClicked(pos,mData.get(pos).getMusic_url(),delete.size());
                    for (int i = 0; i < delete.size();i++){
                        Log.e("아이템클릭",""+delete.get(i));
                    }

                    mListener.Arraylisted(delete,listsun,insert);

                }
            });
            //제일마지막에 순서바꾸기


            /*
            holder.edit_button.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    mListener.onMore_buttonClicked(pos,mData.get(pos).getMusic_id(),mData.get(pos).getList_num());
                    Log.i("뭐얌", "mData.get(pos).getMusic_url(): " +mData.get(pos).getMusic_url());


                }
            });*/


        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void all(){
        for(int i = 0; i < mData.size(); i++){
            if ( mSelectedItems.get(i, false) ){
            } else {
                mSelectedItems.put(i, true);
                boolean is = false;
                for (int j = 0; j < delete.size(); j++){
                    if(mData.get(i).getList_num() == delete.get(j)){
                        is = true;
                    }
                }

                if (is){

                }else {
                    delete.add(mData.get(i).getList_num());
                    insert.add(mData.get(i).getMusic_idx());
                }

            }
        }
        mListener.counted(delete.size());
        mListener.Arraylisted(delete,listsun,insert);
        notifyDataSetChanged();

    }

    public void all2(){
        for(int i = 0; i < mData.size(); i++){
            if ( mSelectedItems.get(i, false) ){
                mSelectedItems.put(i, false);
                delete.clear();
                insert.clear();
                mListener.Arraylisted(delete,listsun,insert);
            } else {
            }
        }
        notifyDataSetChanged();

    }

    public void setStringArrayPref(Context context, String key, ArrayList<Integer> values) {
        Log.e("어레이저장함","저장함");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    private ArrayList<Integer> getStringArrayPref(Context context, String key) {
        Log.e("어레이불러옴","불러옴");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<Integer> urls = new ArrayList<Integer>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    int url = Integer.parseInt(a.optString(i));
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    public void playing(String value6){
        item_position = Integer.parseInt(value6);
        notifyDataSetChanged();

    }

    public ArrayList<Integer> listsun(){
        listsun.clear();
        for (int i = 0; i<mData.size(); i++){
            listsun.add(mData.get(i).getList_num());
        }
        return listsun;
    }


}
