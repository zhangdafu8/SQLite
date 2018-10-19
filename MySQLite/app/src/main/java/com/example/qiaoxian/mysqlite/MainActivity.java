package com.example.qiaoxian.mysqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView SDCardPath;
    private RadioGroup radioGroup;
    private EditText name, age, id;
    private String genderString = "man";
    private String stringName, stringAge, idString;
    private SQLiteDatabase db;
    private ListView listViewInfo;
    private RadioButton manRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SDCardPath = (TextView)findViewById(R.id.textPath);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        name = (EditText) findViewById(R.id.editName);
        age = (EditText) findViewById(R.id.editAge);
        id = (EditText) findViewById(R.id.editId);
        listViewInfo=(ListView)findViewById(R.id.listViewQuery);
        manRadio = (RadioButton)findViewById(R.id.radioMan);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.radioMan){
                    genderString = "man";
                }else{
                    genderString = "woman";
                }
            }
        });


        String path = Environment.getExternalStorageDirectory()+"/test.db";
        SQLiteOpenHelper sqLiteOpenHelper = new SQLiteOpenHelper(this,path,null,4) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {
                Toast.makeText(MainActivity.this,"Database Completed！",
                        Toast.LENGTH_SHORT)
                        .show();
                String sql = "create table info_tb (_id integer primary key autoincrement,"+"name varchar (20),"
                        +"age integer,"+"gender varchar(4))";
                sqLiteDatabase.execSQL(sql);
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
                Toast.makeText(MainActivity.this,"Database Update！",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        };
        db = sqLiteOpenHelper.getReadableDatabase();

        SDCardPath.setText("SD card path:"+Environment.getExternalStorageDirectory().getAbsolutePath());

    }

    public void onMyButton(View view){
        stringName = name.getText().toString();
        stringAge = age.getText().toString();
        idString = id.getText().toString();

        switch (view.getId()){
            case R.id.buttonAdd:

                String sqlAdd = "insert into info_tb (name,age,gender) values (?,?,?)";
                db.execSQL(sqlAdd,new String[]{stringName,stringAge,genderString});
                Toast.makeText(MainActivity.this,"add successfully!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonQuery:
                String sqlQuery = "select * from info_tb";

                if(!idString.equals("")){
                    sqlQuery+=" where _id = "+idString;
                }
                Cursor c = db.rawQuery(sqlQuery,null);
                SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(MainActivity.this,
                        R.layout.listview_item,c,new String[]{"_id","name","age","gender"},
                        new int[]{R.id.itemID,R.id.itemName,R.id.itemAge,R.id.itemGender}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                listViewInfo.setAdapter(simpleCursorAdapter);
                Toast.makeText(MainActivity.this,"query successfully!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonDelete:
                String sqlDelete = "delete from info_tb where _id =?";
                db.execSQL(sqlDelete,new String[]{idString});
                Toast.makeText(MainActivity.this,"delete successfully!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonModify:
                String sqlUpdate = "update info_tb set name = ?, age = ?, gender = ? where _id = ?";
                db.execSQL(sqlUpdate,new String[]{stringName,stringAge,genderString,idString});
                Toast.makeText(MainActivity.this,"update successfully!",Toast.LENGTH_SHORT).show();
                break;
        }
        name.setText("");
        age.setText("");
        id.setText("");
        manRadio.setChecked(true);
    }
}
