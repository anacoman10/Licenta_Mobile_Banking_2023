package com.example.licentamobilebanking;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ecran_principal extends AppCompatActivity {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth;
//    public static View btmTrans;
    public static String phone = "+40733920067";
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecran_principal);

         bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navListener);

        firebaseAuth = FirebaseAuth.getInstance();
        //checkUserStatus();
       // btmTrans = findViewById(R.id.bottom_transactions);


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();


    }

    private void checkUserStatus() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            phone = firebaseUser.getPhoneNumber();
            Toast.makeText(this, phone, Toast.LENGTH_SHORT).show();

        } else {
            finish();
        }
    }

    private NavigationBarView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                       item.setChecked(true);
                    switch (item.getItemId()) {
                        case R.id.bottom_home:
                            selectedFragment = new HomeFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                            break;
                        case R.id.bottom_maps:
                           selectedFragment = new MapsFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                            break;
                        case R.id.bottom_transactions:
                            selectedFragment = new TransactionsFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                            break;
//                        case R.id.bottom_pay:
//                            if (!UtilitatiSingleton.card.isBlocat()) {
//                                selectedFragment = new PaymentsFragment();
//                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
//                            } else{
////                                updateNavigationBarState(R.id.bottom_home);
//                                bottomNavigationView.setSelectedItemId(R.id.bottom_home);
//                              //  bottomNavigationView.getMenu().getItem(0).setChecked(true);
//
//                            }
//                            break;
//                        case R.id.bottom_reports:
//                            selectedFragment = new ReportsFragment();
//                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
//                            break;
//                    }
//

                    }
                        return true;
                    }
                };
    private void updateNavigationBarState(int actionId){
        Menu menu = bottomNavigationView.getMenu();

        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(item.getItemId() == actionId);
        }
    }

}