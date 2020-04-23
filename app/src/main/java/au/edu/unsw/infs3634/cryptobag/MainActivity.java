package au.edu.unsw.infs3634.cryptobag;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.edu.unsw.infs3634.cryptobag.entities.Coin;
import au.edu.unsw.infs3634.cryptobag.entities.CoinLoreResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private boolean mTwoPane;
    ProgressDialog progressDialog;
    private String TAG = "MainActivity";
    private CoinAdapter mAdapter;
    private CoinDatabase mDb;

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
         mAdapter = new CoinAdapter(this, new ArrayList<Coin>(), mTwoPane);
        mRecyclerView.setAdapter(mAdapter);

        //Database build
        mDb = Room.databaseBuilder(getApplicationContext(), CoinDatabase.class, "database").build();
        //Retrieve from database
        new MyTaskGetDB().execute();
        new MyTask().execute();

    }
        private class MyTask extends AsyncTask<Void, Void, List<Coin>> {
            @Override
            protected List<Coin> doInBackground(Void... voids) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.coinlore.net/api/tickers/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                Log.d(TAG, "onBuild: SUCCESS");
                try {

                CoinService service = retrofit.create(CoinService.class);
                Call<CoinLoreResponse> coinsCall = service.getCoins();

                Log.d(TAG, "onResponse: SUCCESS");

                Response<CoinLoreResponse> coinsResponse = coinsCall.execute();
                List<Coin> coins = coinsResponse.body().getData();
                mDb.coinDao().deleteAll(mDb.coinDao().getCoins().toArray(new Coin[mDb.coinDao().getCoins().size()]));
                mDb.coinDao().insertAll(coins.toArray(new Coin[coins.size()]));
                return coins;

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Coin> coins) {
                super.onPostExecute(coins);
                mAdapter.setCoins(coins);
                Log.d(TAG, "onPostExecute: Coins output is  " + coins.toString());
            }

        }

    private class MyTaskGetDB extends AsyncTask<Void, Void, List<Coin>>{

        @Override
        protected List<Coin> doInBackground(Void... voids) {
            return mDb.coinDao().getCoins();
        }

        @Override
        protected void onPostExecute(List<Coin> coins) {
            mAdapter.setCoins(coins);
        }
    }
}
/**

            @Override
            protected void onPostExecute(List coins) {
                super.onPostExecute(coins);

            }

            }}

            //Gson gson = new Gson();
            //CoinLoreResponse response = gson.fromJson(CoinLoreResponse.json, CoinLoreResponse.class);


            //try {

            //Response<CoinLoreResponse> coinsResponse = coinsCall.execute();

            //} catch (IOException e) {
            //e.printStackTrace();
            //}
            //List<Coin> coins = coinsResponse.body().getData();

            //List<Coin> coins = response.getData();
            //RecyclerView.Adapter mAdapter = new CoinAdapter(this, coins, mTwoPane);
/**
            coinsCall.enqueue(new Callback<CoinLoreResponse>() {
                @Override
                public void onResponse(Call<CoinLoreResponse> call, Response<CoinLoreResponse> response) {
                    if(response.isSuccessful()) {
                        Log.d(TAG, "onResponse: SUCCESS");

                        List<Coin> coins = response.body().getData();
                        ((CoinAdapter) mAdapter).setCoins(coins);
                        Log.d(TAG, "onListFill: SUCCESS" + coins);
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

**/

