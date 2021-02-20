package com.kplo.beat;

public class Feed_Item {
        private int idx;
        private String id;
        private String story;
        private String story_img_url;
        private String story_img_url2;
        private String story_img_url3;
        private String story_img_url4;
        private String story_img_url5;
        private String day;
        private int heart_count;
        private int commnet_count;
        private String my_img_url;

        public void setStory(String story) {
            this.story = story ;
        }
        public void setId(String id) {
            this.id = id ;
        }
        public void setStory_img_url(String story_img_url){this.story_img_url = story_img_url;}
        public void setDay(String day){this.day = day;}
        public void setIdx(int idx){this.idx = idx;}
        public void setHeart_count(int heart_count){this.heart_count = heart_count;}
        public void setCommnet_count(int commnet_count){this.commnet_count = commnet_count;}
        public void setMy_img_url(String my_img_url){this.my_img_url = my_img_url;}

        public void setStory_img_url5(String story_img_url5) {
                this.story_img_url5 = story_img_url5;
        }

        public void setStory_img_url4(String story_img_url4) {
                this.story_img_url4 = story_img_url4;
        }

        public void setStory_img_url3(String story_img_url3) {
                this.story_img_url3 = story_img_url3;
        }

        public void setStory_img_url2(String story_img_url2) {
                this.story_img_url2 = story_img_url2;
        }

        public String getStory_img_url5() {
                return story_img_url5;
        }

        public String getStory_img_url4() {
                return story_img_url4;
        }

        public String getStory_img_url3() {
                return story_img_url3;
        }

        public String getStory_img_url2() {
                return story_img_url2;
        }

        public String getStory() {
            return this.story ;
        }
        public String getId() {
            return this.id ;
        }
        public String getStory_img_url(){ return this.story_img_url;}
        public String getDay(){return  this.day;}
        public int getIdx(){return this.idx;}
        public int getHeart_count(){return  this.heart_count;}
        public int getCommnet_count(){return this.commnet_count;}
        public String getMy_img_url(){return this.my_img_url;}



}
