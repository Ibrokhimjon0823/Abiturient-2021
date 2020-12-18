package com.jaloliddinabdullaev.abiturient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toolbar;

import com.jaloliddinabdullaev.abiturient.Adapter.CategoryAdapter;
import com.jaloliddinabdullaev.abiturient.Common.SpaceDecoration;
import com.jaloliddinabdullaev.abiturient.DBHelper.DBHelper;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("EDMT Quiz 2019");
        setSupportActionBar(toolbar);

        recyclerView=findViewById(R.id.recycler_category);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //Get Screen Height
        CategoryAdapter categoryAdapter=new CategoryAdapter(MainActivity.this, DBHelper.getInstance(this).getAllCategories());
        int spaceInPixels=4;
        recyclerView.addItemDecoration(new SpaceDecoration(spaceInPixels));
        recyclerView.setAdapter(categoryAdapter);

    }

    private void setSupportActionBar(Toolbar toolbar) {


    }
}