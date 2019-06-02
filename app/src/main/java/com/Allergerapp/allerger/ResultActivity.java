package com.Allergerapp.allerger;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.AssetManager;
import android.view.View;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultActivity extends AppCompatActivity {

    private final String dbName = "webnautes";// DBNAME
    private final String tableName = "person";
    Bitmap imageBitmap;
    ImageView imageView;

    private String names[];
    {
        names = new String[]{"bean", "egg", "shrimp", "peach", "kiwi", "flour", "peanut", "fish", "tomato",
                "almond","melon","walnut","hamburger","cheese","salmon","crap","wheat","chocolate","butter","cocoa","canola","soy","albumin","mayonnaise","globulin",
                "ovalbumin","ovomucin","ovomucoid","livetin","baked","lecithin","macaroni","nougat","pasta",
                "cream","casein","curds","custard","ghee","lacto","pudding","yogurt","margarine","miso","sprouts","tofu","nuts", "cashews","hazelnuts","ginko", "chestnut",
                "lichi","lichee","lychee","pecans","pine","pistachio","langoustine","langouste","scampo", "tomalley","abalone"
                ,"clams","mussel","octopus","oyster","snail","escargot","surimi","abocado","banana"};
    }

    private final String phones[];// COMPARE WITH NAMES THEN SHOW TO USER THE KOREAN
    {
        phones = new String[]{"견과류알러지(땅콩)", "계란 알러지(달걀)", "갑각류 알러지(새우)", "과일 알러지(복숭아)","과일 알러지(키위)", "밀가루 알러지(밀)", "견과류 알러지(땅콩)", "어패류 알러지(물고기)", "과일 알러지(토마토)","견과류 알러지(아몬드)","과일 알러지(멜론)","견과류 알러지(호두)",
                "유제품 알러지(햄버거)","유제품 알러지(치즈)","어패류 알러지(연어)","갑각류 알러지(게)","밀가루 알러지(밀)","유제품 알러지(초콜렛)","유제품 알러지(버터)","코코아 알러지"," 콩 알러지(카놀라)","콩 알러지(된장)","계란 알러지","계란 알러지(마요네즈 함유)","계란 알러지",
                "계란관련 알러지","계란관련 알러지","계란관련 알러지","계란관련 알러지","계란관련 알러지(빵)","계란관련 알러지","계란관련 알러지","계란관련 알러지(누가초콜릿)",
                "계란관련 알러지(파스타면)","유제품 알러지(크림)","유제품 알러지(카제인)","유제품 알러지","유제품 알러지(커스타드)","유제품 알러지(버터오일)","유제품 알러지",
                "유제품 알러지(푸딩)", "유제품 알러지(요거트)", "유제품 알러지(마가린)","콩 알러지(미소)","콩 알러지(콩나물)","콩 알러지(두부)","견과류 알러지","견과류 알러지(캐슈넛)","견과류 알러지(헤이즐넛)"
                , "견과류 알러지(은행)","견과류 알러지(밤)","견과류 알러지(리찌넛)","견과류 알러지(리찌넛)","견과류 알러지(리찌넛)","견과류 알러지(피칸)","견과류 알러지(잣)","견과류 알러지(피스타치오)",
                "갑각류 알러지(랍스터)","갑각류 알러지(랍스터)","갑각류 알러지(랍스터)","갑각류 알러지(랍스터)", "갑각류 알러지(전복)","갑각류 알러지(조개)","갑각류 알러지(홍합)","해산물 알러지(문어)",
                "갑각류 알러지(굴)","달팽이","달팽이","맛살","아보카도","바나나"};
    }

    ArrayList<HashMap<String, String>> personList; //DB CODE
    ListView list;
    private static final String TAG_NAME = "name";
    private static final String TAG_PHONE ="Allergy";

    SQLiteDatabase sampleDB = null;
    //ListAdapter adapter;
    ListViewAdapter adapter;

    Bitmap image; // 사용되는 이미지
    private TessBaseAPI mTess; // Tess API reference
    String datapath = "" ; // 언어데이터가 있는 경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        list = findViewById(R.id.listView);


        adapter=new ListViewAdapter();
        list.setAdapter(adapter);

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




        /*
        Intent intent = getIntent();
        String path = intent.getExtras().getString("path");
        TextView textView = findViewById(R.id.pathId);
        textView.setText(path);  // 텍스트를 경로로 변경

        File imgFile = new File(path);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        */
        int code;
        Intent data = getIntent();
        Bundle extras = data.getExtras();
        code = extras.getInt("code");

        //code == 0  by gallery
        if(code ==0){
            String path = data.getExtras().getString("path");
            //TextView textView = findViewById(R.id.pathId);
            //textView.setText(path);  // 텍스트를 경로로 변경
            Log.v("태그",path);
            File imgFile = new File(path);
            Log.v("태그",imgFile.getAbsolutePath());
            imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView = findViewById(R.id.result_IMG);
            imageView.setImageBitmap(imageBitmap);

        }
        //code == 1    through camera
        else if(code ==1){
            String path = extras.getString("path");
            File imgFile = new File(path);
            Log.v("태그",imgFile.getAbsolutePath());
            imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            imageBitmap = resizeBitmap(imageBitmap);
            imageBitmap =rotateImage(imageBitmap, 90);

            imageView = findViewById(R.id.result_IMG);
            imageView.setImageBitmap(imageBitmap);

        }
        else{
            Toast.makeText(ResultActivity.this, "오류.", Toast.LENGTH_SHORT).show();
        }

        image = imageBitmap;
        //언어파일 경로
        datapath = getFilesDir()+ "/tesseract/";

        //트레이닝데이터가 카피되어 있는지 체크
        checkFile(new File(datapath + "tessdata/"));

        //Tesseract API
        String lang = "eng";

        mTess = new TessBaseAPI();
        mTess.init(datapath, lang);
        mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ1234567890',.?;/ ");
        processImage(imageView);

    }




    public void processImage(View view) {
        String OCRresult = null;
        String print_= null;
        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();
        TextView OCRTextView = findViewById(R.id.ocrResult);
        //여기서 부터 데이터 클리어링.



        OCRresult=OCRresult.toLowerCase();// 비교를 위해 소문자로 다 변환하는 코드
        OCRresult=OCRresult.replaceAll("[0-9]","");//숫자제거하는 코드
        OCRresult=OCRresult.replaceAll("[^a-z]"," ");
        print_=OCRresult.trim().replaceAll(" +",", ");

        //여기까지 데이터 클리어링, 소문자로 통일, 숫자, 특수기호 제거.

        OCRTextView.setText(OCRresult);//여기가 OCR 출력하는 부분.
        OCRTextView.setText(print_);

        showList(OCRresult);
    }
    static public Bitmap resizeBitmap(Bitmap original) {

        int resizeWidth = 800;

        double aspectRatio = (double) original.getHeight() / (double) original.getWidth();
        int targetHeight = (int) (resizeWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(original, resizeWidth, targetHeight, false);
        if (result != original) {
            original.recycle();
        }
        return result;
    }

    public Bitmap rotateImage(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }



    // copy file to device
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

    // check file on the device
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

    // 오류시 show list 지우기
    protected void showList(String Clearing){
        try {

            SQLiteDatabase ReadDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            // SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
            Cursor c = ReadDB.rawQuery("SELECT * FROM " + tableName, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {

                        String Name = c.getString(c.getColumnIndex("name"));
                        String Phone = c.getString(c.getColumnIndex("phone"));
                        HashMap<String,String> persons = new HashMap<String,String>();
                        int pic_id=0;
                        if (Clearing.contains(Name))
                        {

                            /*persons.put(TAG_NAME,Name);
                            persons.put(TAG_PHONE,Phone);
                            personList.add(persons);*/
                            if(Phone.contains("계란"))
                            {
                                pic_id=R.drawable.eggs;
                            }
                            else if(Phone.contains("유제품"))
                            {
                                pic_id=R.drawable.milk;
                            }
                            else if(Phone.contains("콩"))
                            {
                                pic_id=R.drawable.soya;
                            }
                            else if(Phone.contains("견과류"))
                            {
                                pic_id=R.drawable.peanut_red_109453;
                            }
                            else if(Phone.contains("어패류"))
                            {
                                pic_id=R.drawable.fishpic;
                            }
                            else if(Phone.contains("갑각류"))
                            {
                                pic_id=R.drawable.jogaepic;
                            }
                            else
                            {
                                pic_id=R.drawable.sesame;
                            }

                            adapter.addItem(ContextCompat.getDrawable(this,pic_id),Name,Phone);


                        }


                        // HashMap에 넣습니다.

                    } while (c.moveToNext());
                }
            }
            ReadDB.close();

            // 새로운 apapter를 생성하여 데이터를 넣은 후..
           /* adapter = new SimpleAdapter(
                    this, personList, R.layout.list_item,
                    new String[]{TAG_NAME,TAG_PHONE},
                    new int[]{ R.id.name, R.id.phone}
            );*/

            // 화면에 보여주기 위해 Listview에 연결합니다.
            list.setAdapter(adapter);

        } catch (SQLiteException se) {
            Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }

    }
    public class ListViewAdapter extends BaseAdapter {
        private ArrayList<ListViewItem>listViewItemList=new ArrayList<ListViewItem>();

        public ListViewAdapter()
        {

        }

        public int getCount(){
            return listViewItemList.size();
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            final int pos=position;
            final Context context=parent.getContext();

            if(convertView==null)
            {
                LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView=inflater.inflate(R.layout.list_item,parent,false);
            }


            TextView nameTextView=(TextView)convertView.findViewById(R.id.name);

            TextView descTextView=(TextView)convertView.findViewById(R.id.phone);

            ImageView iconImageView=(ImageView)convertView.findViewById(R.id.image_pic);

            ListViewItem listViewItem=listViewItemList.get(position);


            nameTextView.setText(listViewItem.getTitle());
            descTextView.setText(listViewItem.getDesc());
            iconImageView.setImageDrawable(listViewItem.getIcon());

            return convertView;

        }

        public long getItemId(int position)
        {
            return position;
        }
        public Object getItem(int position)
        {
            return listViewItemList.get(position);
        }
        public void addItem(Drawable icon, String title, String desc)
        {
            ListViewItem item=new ListViewItem();

            item.setIcon(icon);
            item.setTitle(title);
            item.setDesc(desc);

            listViewItemList.add(item);
        }
    }
    public class ListViewItem{
        private Drawable iconDrawable;
        private String titleStr;
        private String descStr;

        public void setIcon(Drawable icon)
        {
            iconDrawable=icon;
        }
        public void setTitle(String title)
        {
            titleStr=title;
        }
        public void setDesc(String desc)
        {
            descStr=desc;
        }

        public Drawable getIcon()
        {
            return this.iconDrawable;
        }
        public String getTitle()
        {
            return this.titleStr;
        }
        public String getDesc()
        {
            return this.descStr;
        }
    }
}