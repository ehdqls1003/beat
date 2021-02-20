package com.kplo.beat;

public class Recent_Main_Item {
    /*
        private Drawable iconDrawable ;*/
        private int idx;
        private String id;
        private String title ;
        private String my_img_url ;
        private String music_url;

        public Recent_Main_Item(){
            this.id = id;
            this.title = title;
            this.my_img_url = my_img_url;
            this.music_url = music_url;

        }
/*
        public void setIcon(Drawable icon) {
            iconDrawable = icon ;
        }*/
        public void setTitle(String title) {
            this.title = title ;
        }
        public void setId(String id) {
            this.id = id ;
        }
        public void setMy_img_url(String my_img_url){this.my_img_url = my_img_url;}
        public void setMusic_url(String music_url){this.music_url = music_url;}
/*
        public Drawable getIcon() {
            return this.iconDrawable ;
        }*/
        public String getTitle() {
            return this.title ;
        }
        public String getId() {
            return this.id ;
        }
        public String getMy_img_url(){ return this.my_img_url;}
        public String getMusic_url(){return  this.music_url;}

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }
}
