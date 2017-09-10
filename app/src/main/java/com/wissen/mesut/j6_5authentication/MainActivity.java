package com.wissen.mesut.j6_5authentication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wissen.mesut.j6_5authentication.base.BaseActivity;
import com.wissen.mesut.j6_5authentication.model.Kisi;
import com.wissen.mesut.j6_5authentication.model.Yapilacak;
import com.wissen.mesut.j6_5authentication.tool.AppTool;
import com.wissen.mesut.j6_5authentication.tool.CustomItemClickListener;
import com.wissen.mesut.j6_5authentication.tool.CustomItemLongClickListener;
import com.wissen.mesut.j6_5authentication.tool.MyRecyclerAdapter;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static int sayac = 0;
    CircleImageView nav_userimg;
    TextView nav_txtAdSoyad, nav_txtEmail;

    Kisi kullanici;
    ListView listView;
    ArrayList<Yapilacak> yapilacakList;
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //listView = (ListView) findViewById(R.id.main_listView);
        recyclerView = (RecyclerView) findViewById(R.id.main_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.main_swiperrefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listeyiDoldur();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Aynı Toast Gibi çalışmakta", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this, AddTodo.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) finish();

        nav_userimg = header.findViewById(R.id.nav_userimg);
        nav_txtAdSoyad = header.findViewById(R.id.nav_txtAdSoyad);
        nav_txtEmail = header.findViewById(R.id.nav_txtEmail);

        nav_txtEmail.setText(user.getEmail());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("uyeler");
        Query query = myRef.child(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                kullanici = dataSnapshot.getValue(Kisi.class);
                nav_txtAdSoyad.setText(String.format("%s %s", kullanici.getAd() == null ? "Ad" : kullanici.getAd(), kullanici.getSoyad() == null ? "Soyad" : kullanici.getSoyad()));
                if (kullanici.getFotograf() != null)
                    nav_userimg.setImageBitmap(AppTool.stringToBitmap(kullanici.getFotograf()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void listeyiDoldur() {
        showProgressDialog("Lütfen bekleyin", "Veritabanına bağlantı kuruluyor");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        final Query yapilacaklarQuery = myRef.child("yapilacaklar").orderByChild("eklenmeZamani");
        yapilacaklarQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yapilacakList = new ArrayList<>();
                for (DataSnapshot gelen : dataSnapshot.getChildren()) {
                    Yapilacak yeni = gelen.getValue(Yapilacak.class);
                    yapilacakList.add(yeni);
                }
                //MyAdapter adapter = new MyAdapter(MainActivity.this, yapilacakList);
                //listView.setAdapter(adapter);
                MyRecyclerAdapter adapter = new MyRecyclerAdapter(yapilacakList, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                    }

                }, new CustomItemLongClickListener() {
                    @Override
                    public void onItemLongClick(View v, int position) {
                        final int pos = position;
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        Yapilacak silinecek = yapilacakList.get(pos);
                                        database = FirebaseDatabase.getInstance();
                                        myRef = database.getReference().child("yapilacaklar");
                                        myRef.child(silinecek.getId()).removeValue();
                                        Toast.makeText(MainActivity.this, "Silindi", Toast.LENGTH_SHORT).show();
                                        listeyiDoldur();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        Toast.makeText(MainActivity.this, "İptal edildi", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Emin misiniz?").setPositiveButton("Evet", dialogClickListener)
                                .setNegativeButton("Bilemiyorum", dialogClickListener).show();
                    }
                });
                //recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                hideProgressDialog();
                yapilacaklarQuery.removeEventListener(this);
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Lütfen giriş yapın", Toast.LENGTH_SHORT).show();
            finish();
        }
        listeyiDoldur();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            sayac++;
            if (sayac == 2) {
                mAuth.signOut();
                finish();
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        if (id == R.id.nav_profil) {
            //profil sayfası açılacak
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        } else if (id == R.id.nav_cikis) {
            mAuth = FirebaseAuth.getInstance();
            Toast.makeText(this, "Güle güle sahip", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            finish();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
