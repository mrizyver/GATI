package com.gatisnau.gati;

import android.os.Environment;

public class Schedule {
    public static boolean[] threadIsAlive = new boolean[6];

    static {
        for (int i = 0; i<6; i++)
            threadIsAlive[i] = false;
    }
    public static final String url[] = {
                "http://gatisnau.sumy.ua/images/rozklad/rozklad.png",//понедельник
                "http://gatisnau.sumy.ua/images/rozklad/rozklad1.png",//вторник
                "http://gatisnau.sumy.ua/images/rozklad/rozklad2.png",//среда
                "http://gatisnau.sumy.ua/images/rozklad/rozklad3.png",//четверг
                "http://gatisnau.sumy.ua/images/rozklad/rozklad4.png",//пятница
                "http://gatisnau.sumy.ua/images/rozklad/rozklad5.png"//суббота
    };
//    public static final String urlр[] = {
//            "https://img.freepik.com/free-vector/hello-monday-hand-drawn-letters-coming-out-of-a-cup-of-coffee_23-2147655180.jpg?size=338&ext=jpg",//понедельник
//            "https://cdn.pixabay.com/photo/2016/06/18/17/42/image-1465348_960_720.jpg",//вторник
//            "https://www.gettyimages.com/gi-resources/images/CreativeLandingPage/HP_Sept_24_2018/CR3_GettyImages-159018836.jpg",//среда
//            "https://lh3.googleusercontent.com/Zl7XwhbI_UOgTd92AEBiWFZLBd-LjIe3lSuIh2hhcRXo2c7yInDiqjioRhNbvxpRdTiU9cAdhw=w640-h400-e365",//четверг
//            "https://akm-img-a-in.tosshub.com/indiatoday/images/story/201809/RTR4TKZB_FB_5.jpeg?GXesXdsEh.2YLNT1A4yndHbzEsv04aic",//пятница
//            "http://www.mountsinai.on.ca/about_us/news/news-feature/img/demopage/image-3.jpg/image"//суббота
//    };
//    public static final String url2[] = {
//            "http://gatisnau.sumy.ua/images/rozklad/rozklad.png",//понедельник
//            "http://gatisnau.sumy.ua/images/rozklad/rozklad.png",//вторник
//            "http://gatisnau.sumy.ua/images/rozklad/rozklad.png",//среда
//            "http://gatisnau.sumy.ua/images/rozklad/rozklad.png",//четверг
//            "http://gatisnau.sumy.ua/images/rozklad/rozklad.png",//пятница
//            "http://gatisnau.sumy.ua/images/rozklad/rozklad.png"//суббота
//    };
    public static final String KEY_DAY_OF_WEEK = "day_of_week";
    public static final String URL_TO_NEW_APP = "http://andro.gatisnau.sumy.ua/app.apk";
    public static final String URL_VERSION_CONTROL = "http://andro.gatisnau.sumy.ua/version.txt";
    public  static final String PATH_TO_DOWNLOAD_FOLDER = Environment.getExternalStorageDirectory() + "/download/";

    public static final class ImageDatabase{
        public static final String TABLE_NAME = "image_database";
        public static final class cols{
            public static final String DATE = "date";
            public static final int DATE_INT = 1;
            public static final String IMAGE = "image";
            public static final int IMAGE_INT = 3;
            public static final String DAY_OF_WEEK = "day";
            public static final int DAY_OF_WEEK_INT = 0;
            public static final String SIZE = "size";
            public static final int SIZE_INT = 2;
        }

    }
}


