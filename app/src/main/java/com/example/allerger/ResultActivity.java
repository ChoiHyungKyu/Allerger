package com.example.allerger;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ResultActivity extends AppCompatActivity {

    private final String dbName = "webnautes";//DBNAME
    private final String tableName = "person";


    private String names[];
    {
        names = new String[]{"bean", "egg", "shrimp", "peach", "kiwi", "flour", "peanut", "fish", "tomato",
            "almond","melon","walnut","hamburger","cheese","salmon","crab","wheat","chocolate","butter","cocoa","canola","soy"};
    }


    private final String phones[];//COMPARE WITH NAMES THEN SHOW TO USER THE KOREAN
    {
        phones = new String[]{"땅콩알러지", "달걀", "갑각류", "복숭아","키위", "밀가루알러지", "땅콩", "물고기", "토마토","아몬드","멜론","호두",
                "유제품 알러지","유제품 알러지","어패류","갑각류","밀가루","초콜렛 알러지","버터알러지","코코아 알러지","카놀라","간장"};
    }



    ArrayList<HashMap<String, String>> personList; //DB CODE
    ListView list;
    private static final String TAG_NAME = "name";
    private static final String TAG_PHONE ="Allergy";

    SQLiteDatabase sampleDB = null;
    ListAdapter adapter;

    Bitmap image; //사용되는 이미지
    private TessBaseAPI mTess; //Tess API reference
    String datapath = "" ; //언어데이터가 있는 경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        list = (ListView) findViewById(R.id.listView);
        personList = new ArrayList<HashMap<String,String>>();


        try {

            sampleDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);//MAKE DB TO SAVE THE INFO ABOUT ALLERGY

            //테이블이 존재하지 않으면 새로 생성합니다.
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
                    + " (name VARCHAR(20), phone VARCHAR(20) );");

            //테이블이 존재하는 경우 기존 데이터를 지우기 위해서 사용합니다.
            sampleDB.execSQL("DELETE FROM " + tableName  );

            //새로운 데이터를 테이블에 집어넣습니다..
            for (int i=0; i<names.length; i++ ) {
                sampleDB.execSQL("INSERT INTO " + tableName
                        + " (name, phone)  Values ('" + names[i] + "', '" + phones[i]+"');");
            }

            sampleDB.close();//close DB

        } catch (SQLiteException se) {
            Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("", se.getMessage());


        }



        Intent intent = getIntent();
        String path = intent.getExtras().getString("path");
        TextView textView = findViewById(R.id.pathId);
        textView.setText(path);  // 텍스트를 경로로 변경


        File imgFile = new File(path);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        ImageView imageView = (ImageView) findViewById(R.id.imgview);
        imageView.setImageBitmap(myBitmap);

        image = myBitmap;
        //언어파일 경로
        datapath = getFilesDir()+ "/tesseract/";

        //트레이닝데이터가 카피되어 있는지 체크
        checkFile(new File(datapath + "tessdata/"));

        //Tesseract API
        String lang = "eng";

        mTess = new TessBaseAPI();
        mTess.init(datapath, lang);
        processImage(imageView);

    }


    public void processImage(View view) {
        String OCRresult = null;
        String print_=null;
        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();
        TextView OCRTextView = (TextView) findViewById(R.id.ocrResult);
        //여기서 부터 데이터 클리어링.



        OCRresult=OCRresult.toLowerCase();// 비교를 위해 소문자로 다 변환하는 코드
        OCRresult=OCRresult.replaceAll("[0-9]","");//숫자제거하는 코드
        OCRresult=OCRresult.replaceAll("[^a-z]"," ");
        print_=OCRresult.trim().replaceAll(" +",", ");

        //여기까지 데이터 클리어링, 소문자로 통일, 숫자, 특수기호 제거.

        //OCRTextView.setText(OCRresult);//여기가 OCR 출력하는 부분.
        OCRTextView.setText(print_);

        showList(OCRresult);
    }


    //copy file to device
    private void copyFiles() {
        try{
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //check file on the device
    private void checkFile(File dir) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if(!dir.exists()&& dir.mkdirs()) {
            copyFiles();
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if(!datafile.exists()) {
                copyFiles();
            }
        }
    }
    //오류시 show list 지우기/
    protected void showList(String Clearing){

        try {

            SQLiteDatabase ReadDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
            Cursor c = ReadDB.rawQuery("SELECT * FROM " + tableName, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        //테이블에서 두개의 컬럼값을 가져와서
                        String Name = c.getString(c.getColumnIndex("name"));
                        String Phone = c.getString(c.getColumnIndex("phone"));
                        HashMap<String,String> persons = new HashMap<String,String>();
                        /*for(int i=0;i<compare.length;i++)
                        {
                            if(compare[i].contains(Name))
                            {
                                persons.put(TAG_NAME,Name);
                                persons.put(TAG_PHONE,Phone);

                                //ArrayList에 추가합니다..
                                personList.add(persons);
                                break;
                            }
                        }*/
                        if (Clearing.contains(Name))
                        {
                            persons.put(TAG_NAME,Name);
                            persons.put(TAG_PHONE,Phone);
                            personList.add(persons);
                        }


                        //HashMap에 넣습니다.

                    } while (c.moveToNext());
                }
            }
            ReadDB.close();
            //새로운 apapter를 생성하여 데이터를 넣은 후..
            adapter = new SimpleAdapter(
                    this, personList, R.layout.list_item,
                    new String[]{TAG_NAME,TAG_PHONE},
                    new int[]{ R.id.name, R.id.phone}
            );


            //화면에 보여주기 위해 Listview에 연결합니다.
            list.setAdapter(adapter);


        } catch (SQLiteException se) {
            Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }

    }
}