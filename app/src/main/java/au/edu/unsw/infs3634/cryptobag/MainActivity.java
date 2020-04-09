package au.edu.unsw.infs3634.cryptobag;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import au.edu.unsw.infs3634.cryptobag.entities.Coin;
import au.edu.unsw.infs3634.cryptobag.entities.CoinLoreResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private boolean mTwoPane;
    ProgressDialog progressDialog;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Widescreen determinant
        if (findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
        }
        //Layout code
        RecyclerView mRecyclerView = findViewById(R.id.rvList);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter mAdapter = new CoinAdapter(this, new ArrayList<Coin>(), mTwoPane);
        mRecyclerView.setAdapter(mAdapter);

        //Gson gson = new Gson();
        //CoinLoreResponse response = gson.fromJson(CoinLoreResponse.json, CoinLoreResponse.class);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.coinlore.net/api/tickers/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CoinService service = retrofit.create(CoinService.class);
        //try {
            Call<CoinLoreResponse> coinsCall = service.getCoins();
        //Response<CoinLoreResponse> coinsResponse = coinsCall.execute();

        //} catch (IOException e) {
            //e.printStackTrace();
        //}
        //List<Coin> coins = coinsResponse.body().getData();

        //List<Coin> coins = response.getData();
        //RecyclerView.Adapter mAdapter = new CoinAdapter(this, coins, mTwoPane);

        coinsCall.enqueue(new Callback<CoinLoreResponse>() {
            @Override
            public void onResponse(Call<CoinLoreResponse> call, Response<CoinLoreResponse> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "onResponse: SUCCESS");
                    //progressDialog.dismiss();
                    List<Coin> coins = response.body().getData();
                    ((CoinAdapter) mAdapter).setCoins(coins);
                }else{
                    Log.d(TAG, "onResponse: ERROR IS" + response.body());
                }

            }

            @Override
            public void onFailure(Call<CoinLoreResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: ON FAILURE IS:" + t.getLocalizedMessage());
                //progressDialog.dismiss();
            }


        });
    }
}


