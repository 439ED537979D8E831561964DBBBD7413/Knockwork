package com.pasistence.knockwork.Client.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.pasistence.knockwork.Adapter.LancerListAdapter;
import com.pasistence.knockwork.Common.Common;
import com.pasistence.knockwork.Freelancer.Activities.JobPoastingActivity;
import com.pasistence.knockwork.Interface.ILoadMore;
import com.pasistence.knockwork.Model.ApiResponse.ApiResponseLancer;
import com.pasistence.knockwork.Model.LancerListModel;
import com.pasistence.knockwork.Model.ResponseSuggestionList;
import com.pasistence.knockwork.R;
import com.pasistence.knockwork.Remote.MyApi;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LancersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "search";
    Context mContext;
    MaterialSearchBar searchBar;
    RecyclerView recyclerLancer;
    LinearLayoutManager layoutManager;
    //Variable for pagination
    int PageNo = 1;
    private boolean isScrolling = false;
    private int visibleItemCount,totalItemCount,pastVisiblesItems = 0;
    int previous_total,view_threshold = 0;
    TextView txtMore;

    ArrayList<LancerListModel> lancerList = new ArrayList<LancerListModel>();
    ArrayList<ApiResponseLancer.Result> resultList = new ArrayList<ApiResponseLancer.Result>();
    LancerListAdapter lancerListAdapter;
    LancerListAdapter searchAdapter;
    List<String> suggestList = new ArrayList<String>();
    String CatId,subCatId;
    ListView listSuggestions;
    MyApi mService;
    List<ResponseSuggestionList> suggestionLists;
    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*----------------------------------------------------------------------------*/
        mInit();

        loadSuggestList();
        getAllLancers(PageNo);
        //Setup search bar
        searchBar.setHint("Search");
        //searchBar.setLastSuggestions(suggestList);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                List<String>suggest = new ArrayList<String>();
                for(String search : suggestList)
                {
                    if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }

                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled)
                {
                    recyclerLancer.setAdapter(lancerListAdapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
              startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        /*----------------------------------------------------------------------------*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        recyclerLancer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount=layoutManager.getChildCount();
                totalItemCount= layoutManager.getItemCount();
                pastVisiblesItems=layoutManager.findFirstVisibleItemPosition();

                Log.e(TAG, "visible item"+visibleItemCount);
                Log.e(TAG, "total item"+totalItemCount);
                Log.e(TAG, "pastvis item"+pastVisiblesItems);

                if(dy>0){

                  /*  if(isLoading){

                        if(totalItemCount>previous_total){
                            isLoading = false;
                            previous_total = totalItemCount;
                        }
                    }*/

                    if(isLoading&&(visibleItemCount+pastVisiblesItems) >= totalItemCount){
                        PageNo++;
                       // performPagination(PageNo);
                        isLoading=false;
                    }
                }
            }
        });
    }

    private void getAllLancers(int PageNo) {

        mService.getLancers(PageNo).enqueue(new Callback<ApiResponseLancer>() {
            @Override
            public void onResponse(Call<ApiResponseLancer> call, Response<ApiResponseLancer> response) {

                response.message();
                ApiResponseLancer result = response.body();
                Log.e(TAG+"+", result.toString());

                resultList = result.getResult();
                Log.e(TAG+"++", resultList.toString());
                //totalItemCount = Integer.parseInt(response.body().getTotalCount());

                lancerListAdapter = new LancerListAdapter(mContext, resultList);
                recyclerLancer.setAdapter(lancerListAdapter);
                lancerListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ApiResponseLancer> call, Throwable t) {
                t.printStackTrace();
            }
        });

         /* */
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lancers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
   //         Snackbar.make(findViewById(R.id.swipe_refresh_layout), "Home", Snackbar.LENGTH_LONG)
     //               .setAction("Action", null).show();
            startActivity(new Intent(mContext,DashboardActivity.class));

        } else if (id == R.id.nav_inbox) {
//            Snackbar.make(findViewById(R.id.swipe_refresh_layout), "Inbox", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
            startActivity(new Intent(mContext,InboxActivity.class));


        } else if (id == R.id.nav_notification) {
//            Snackbar.make(findViewById(R.id.swipe_refresh_layout), "Notification", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();

        } else if (id == R.id.nav_manage) {
//                       Snackbar.make(findViewById(R.id.swipe_refresh_layout), "Manage", Snackbar.LENGTH_LONG)
//                             .setAction("Action", null).show();
//            //startActivity(new Intent(mContext,ManageJobPostActivity.class));

        } else if (id == R.id.nav_posting) {
//            Snackbar.make(findViewById(R.id.swipe_refresh_layout), "Posting", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
            startActivity(new Intent(mContext,JobPoastingActivity.class));

        } else if (id == R.id.nav_contest) {
//            Snackbar.make(findViewById(R.id.swipe_refresh_layout), "Contest", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();

        }else if (id == R.id.nav_settings) {

            startActivity(new Intent(mContext,SettingActivity.class));
            //             Snackbar.make(findViewById(R.id.swipe_refresh_layout), "Settings", Snackbar.LENGTH_LONG)
            //                .setAction("Action", null).show();

        }else if (id == R.id.nav_support) {
//            Snackbar.make(findViewById(R.id.swipe_refresh_layout), "Support", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void mInit() {
        mContext = LancersActivity.this;

        searchBar = (MaterialSearchBar)findViewById(R.id.search_bar_lancer);
        recyclerLancer = (RecyclerView)findViewById(R.id.recycler_lancer_list);
        recyclerLancer.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerLancer.setLayoutManager(layoutManager);
        txtMore = (TextView)findViewById(R.id.txt_more);

        try{

            if(getIntent()!=null){

                CatId = getIntent().getStringExtra("catId");
                subCatId = getIntent().getStringExtra("subcatId") ;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        //Init Api
        mService = Common.getApi();

    }

    private void startSearch(CharSequence text) {
        mService.LancerSearch(1,text).enqueue(new Callback<ApiResponseLancer>() {
            @Override
            public void onResponse(Call<ApiResponseLancer> call, Response<ApiResponseLancer> response) {
                response.message();
                ApiResponseLancer result = response.body();
              // Log.e(TAG, result.toString());

                resultList = result.getResult();

                searchAdapter = new LancerListAdapter(mContext,resultList);
                recyclerLancer.setAdapter(searchAdapter);
                searchAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<ApiResponseLancer> call, Throwable t) {
                t.printStackTrace();
            }

        });

    }

    private void loadSuggestList() {
        //get Data into the list
        try{
                mService.getSuggestionList()
                        .enqueue(new Callback<List<ResponseSuggestionList>>() {
                            @Override
                            public void onResponse(Call<List<ResponseSuggestionList>> call, Response<List<ResponseSuggestionList>> response) {
                                Log.e(TAG, response.body().toString());
                                suggestionLists = response.body();

                                //final List<String> labels = new ArrayList<String>();
                                for(int i = 0; i<suggestionLists.size(); i++)
                                {
                                    suggestList.add(suggestionLists.get(i).getTitle());
                                }
                                searchBar.setLastSuggestions(suggestList);
                            }

                            @Override
                            public void onFailure(Call<List<ResponseSuggestionList>> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });

        }catch (Exception e){
            e.printStackTrace();
        }
        //searchBar.setLastSuggestions(suggestList);
    }

    private void performPagination(int PageNo){
        //perform call statment
        mService.getLancers(PageNo).enqueue(new Callback<ApiResponseLancer>() {
            @Override
            public void onResponse(Call<ApiResponseLancer> call, Response<ApiResponseLancer> response) {
                if(response.body().getStatus().equals("OK")){
                    if(response.body().getResult()!= null || !response.body().getResult().equals("")){

                        int oldsize = resultList.size();
                        int newsize = 0;
                        ArrayList<ApiResponseLancer.Result> results = response.body().getResult();
                        lancerListAdapter.addLancers(results);

                       /* for(int i=0;i<results.size();i++){
                            resultList.add(results.get(i));
                            newsize = resultList.size();
                        }
                        lancerListAdapter = new LancerListAdapter(mContext, resultList);
                        recyclerLancer.setAdapter(lancerListAdapter);
                        lancerListAdapter.notifyItemInserted(newsize-oldsize+1);*/
                       // lancerListAdapter.notifyDataSetChanged();
                    }

                }else {
                    Snackbar.make(findViewById(R.id.lancer_activity), "End of List", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

                response.message();
                ApiResponseLancer result = response.body();
                Log.e(TAG+"+", result.toString());

                resultList = result.getResult();
                Log.e(TAG+"++", resultList.toString());

                lancerListAdapter = new LancerListAdapter(mContext, resultList);
                recyclerLancer.setAdapter(lancerListAdapter);
                lancerListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ApiResponseLancer> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }

}
