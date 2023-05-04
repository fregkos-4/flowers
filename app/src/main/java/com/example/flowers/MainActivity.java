package com.example.flowers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_WORD_ACTIVITY_REQUEST_CODE = 2;
    public static final String EXTRA_DATA = "extra_data";

    private FlowerViewModel flowerViewModel;
    private Button addFlower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final FlowerAdapter adapter = new FlowerAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addFlower = findViewById(R.id.addFlowerButton);
        addFlower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            }
        });

        flowerViewModel =
                new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(FlowerViewModel.class);
        flowerViewModel.getAllItems().observe(this, new Observer<List<Flower>>() {
            @Override
            public void onChanged(List<Flower> flowers) {
                adapter.setFlowers(flowers);
            }
        });

        adapter.setOnItemListener(new FlowerAdapter.Listener() {

            @Override
            public void itemClicked(View v, int position) {
                Flower flower = adapter.getFlowerAtPosition(position);
                launchUpdate(flower);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Flower flower= (Flower) data.getSerializableExtra(SecondActivity.EXTRA_REPLY);
            flowerViewModel.insert(flower);

        } else if (requestCode == UPDATE_WORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Flower flower= (Flower) data.getSerializableExtra(SecondActivity.EXTRA_REPLY);
            flowerViewModel.update(flower);
        } else if (requestCode==RESULT_CANCELED) {
            Toast.makeText(this, R.string.no_update, Toast.LENGTH_LONG).show();
            }
        }


    public void launchUpdate(Flower flower) {

        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(EXTRA_DATA,flower);
        startActivityForResult(intent, UPDATE_WORD_ACTIVITY_REQUEST_CODE);
    }

    public String longToString(long currentDate) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = dateFormat.format(currentDate);
        return strDate;
    }

    public long stringToLong(String currentDate) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long milliseconds = -1;
        try {
            Date d = dateFormat.parse(currentDate);
            milliseconds = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }
}