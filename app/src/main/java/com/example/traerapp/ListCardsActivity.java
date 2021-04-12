package com.example.traerapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paymentez.android.model.Card;
import com.paymentez.android.rest.model.ErrorResponse;
import com.example.traerapp.rest.RetrofitFactory;
import com.example.traerapp.rest.BackendService;
import com.example.traerapp.rest.model.DeleteCardResponse;
import com.example.traerapp.rest.model.GetCardsResponse;
import com.example.traerapp.utils.Alert;
import com.example.traerapp.utils.Constants;
import com.example.traerapp.utils.MyCardAdapter;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.traerapp.datospref.datpref.getDatosUsuario;

public class ListCardsActivity extends AppCompatActivity {

    ArrayList<Card> listCard;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button buttonAddCard;

    Context mContext;
    BackendService backendService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cards);
        mContext = this;
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        buttonAddCard = (Button) findViewById(R.id.buttonAddCard);
        buttonAddCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddCardActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        backendService = RetrofitFactory.getClient().create(BackendService.class);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        getCards();
    }

    public void getCards(){

        final ProgressDialog pd = new ProgressDialog(ListCardsActivity.this);
        pd.setMessage("Agregando Tarjeta");
        pd.show();

        String[] datosusu = getDatosUsuario(ListCardsActivity.this);
        backendService.getCards(datosusu[5]).enqueue(new Callback<GetCardsResponse>() {
            @Override
            public void onResponse(Call<GetCardsResponse> call, Response<GetCardsResponse> response) {
                pd.dismiss();
                GetCardsResponse getCardsResponse = response.body();
                if(response.isSuccessful()) {
                    listCard = (ArrayList<Card>) getCardsResponse.getCards();
                    mAdapter = new MyCardAdapter(listCard, new MyCardAdapter.OnCardSelectedListener() {
                        @Override public void onItemClick(Card card) {

                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("CARD_TOKEN",card.getToken());
                            returnIntent.putExtra("CARD_TYPE",card.getType());
                            returnIntent.putExtra("CARD_LAST4",card.getLast4());
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();

                        }
                    }, new MyCardAdapter.OnCardDeletedClickListener() {
                        @Override public void onItemClick(Card card) {
                            deleteCard(card);

                        }
                    });
                    mRecyclerView.setAdapter(mAdapter);
                }else {
                    Gson gson = new GsonBuilder().create();
                    try {
                        ErrorResponse errorResponse = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        Alert.show(mContext,
                                "Error",
                                errorResponse.getError().getType());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetCardsResponse> call, Throwable e) {
                pd.dismiss();
                Alert.show(mContext,
                        "Error",
                        e.getLocalizedMessage());
            }
        });
    }


    public void deleteCard(Card card){

        final ProgressDialog pd = new ProgressDialog(ListCardsActivity.this);
        pd.setMessage("Borrando Tarjeta");
        pd.show();

        String[] datosusu = getDatosUsuario(ListCardsActivity.this);
        backendService.deleteCard(datosusu[5], card.getToken()).enqueue(new Callback<DeleteCardResponse>() {
            @Override
            public void onResponse(Call<DeleteCardResponse> call, Response<DeleteCardResponse> response) {
                pd.dismiss();
                DeleteCardResponse deleteCardResponse = response.body();
                if(response.isSuccessful()) {
                    getCards();
                    Alert.show(mContext,
                            "Successfully Deleted Card",
                            deleteCardResponse.getMessage());
                }else {
                    Gson gson = new GsonBuilder().create();
                    try {
                        ErrorResponse errorResponse = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        Alert.show(mContext,
                                "Error",
                                errorResponse.getError().getType());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<DeleteCardResponse> call, Throwable e) {
                pd.dismiss();
                Alert.show(mContext,
                        "Error",
                        e.getLocalizedMessage());
            }
        });
    }


}