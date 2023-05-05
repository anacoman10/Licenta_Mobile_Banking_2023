package com.example.licentamobilebanking;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.licentamobilebanking.classes.DepositAdapter;
import com.example.licentamobilebanking.classes.PinView;
import com.example.licentamobilebanking.classes.Provider;
import com.example.licentamobilebanking.classes.cards.Card;
import com.example.licentamobilebanking.classes.cards.CardType;
import com.example.licentamobilebanking.classes.cards.Deposit;
import com.example.licentamobilebanking.classes.cards.Transaction;
import com.example.licentamobilebanking.classes.cards.Withdrawal;
import com.example.licentamobilebanking.classes.cards.WithdrawalAdapter;
import com.example.licentamobilebanking.customDialogs.AddDepositDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    public static String cardId;  //id card curent
    public static Card card; //card curent
    public static User user;  //user curent
    public static CardType cardType;  //tipul cardului curent
    public static List<Withdrawal> withdrawalList = new ArrayList<>();  //toate retragerile
    public static List<Deposit> depositList = new ArrayList<>();  //toate depozitele
    public static List<Transaction> transactionList = new ArrayList<>();  //toate tranzatiile
    public static HashMap<String, Provider> providersList = new HashMap<>(); //toti providerii
    public static HashMap<String, User> userList = new HashMap<>(); //toti userii
    public static HashMap<String,Card> cardList= new HashMap<String,Card>();

    public static TextView myBalanceHome;
    public static TextView depozite;

    Dialog dialog1; //dialog ptr informatii card
    Dialog dialog2;  //dialog ptr aifsare retrageri
    Dialog dialog3;  //dialog ptr aifsare retrageri

    Switch swBlocat;

    ListView listView;


    ImageButton showDetailsButton;
    ImageView imgPin;

    boolean areDetailsShown = false;
    boolean areDepositsShown = false;

    public static DepositAdapter depositAdapter;

    public static FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        UtilitatiSingleton.initializare();
        listView = view.findViewById(R.id.depositListView);

        depositAdapter = new DepositAdapter(view.getContext(),
                R.layout.depozit_adapter, UtilitatiSingleton.depositList, getLayoutInflater());

        listView.setAdapter(depositAdapter);

        swBlocat = view.findViewById(R.id.switchfreeze);
        imgPin = view.findViewById(R.id.seePin);
        myBalanceHome = view.findViewById(R.id.textView14);
        depozite = view.findViewById(R.id.textView8);
        //animatii
        //initialAnimations();

        getCurrentUser();
        //luam userul curent


        //eye button cu informatiile de pe card
        showDetailsButton = view.findViewById(R.id.btn_show_details);
        showDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails(view);
                if (!areDetailsShown) {
                    showDetailsButton.setImageResource(R.drawable.iconeye);
                } else if (areDetailsShown) {
                    showDetailsButton.setImageResource(R.drawable.iconeyeclosed);
                }
            }
        });

        //Custom dialog cu withdrawals la atm la short click pe card
        showWithdrawals(view);

        //Custom dialog cu card informations la long click pe card
        showInformationsDialog(view);


        //buton adauga depozit care deschide un custom dialog cu formularul de completat
        ImageButton addDeposit = view.findViewById(R.id.addDeposit);
        addDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDepositDialog depositDialog = new AddDepositDialog();
                depositDialog.show(getFragmentManager(), "AddDepositDialog");
            }
        });
        depozite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog3 = new Dialog(getContext());
                dialog3.setContentView(R.layout.infodepozitedialog);
                dialog3.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
                dialog3.setCancelable(true);
                dialog3.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

                Button closeBtn = dialog3.findViewById(R.id.btnCloseCateg);

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog3.dismiss();
                    }
                });

                dialog3.show();

            }
        });

        swBlocat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean isBlocat=false;
                if (!isBlocat == b) {
                    Card card1=UtilitatiSingleton.cardList.get(0);
                    card1.setBlocat(b);
                    //database.collection("Cards").document(UtilitatiSingleton.user.getIDCard()).update("Blocat", b);
                }
                if (b) {
                    if (areDetailsShown) {
                        showDetailsButton.performClick();
                    }
                    showDetailsButton.setImageResource(R.drawable.blockedcard);
                    showDetailsButton.setEnabled(false);
                } else {
                    showDetailsButton.setImageResource(R.drawable.iconeye);
                    showDetailsButton.setEnabled(true);
                }
            }
        });

        //afiseaza si ascunde depozitele la click pe sagetica
        ImageButton arrowBtn = view.findViewById(R.id.arrow);
        arrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDeposits(view, listView);
            }
        });

        imgPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PinView.class);
                startActivity(intent);
            }
        });
    }


    private Dialog createWithdrawalDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.withdrawals_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

        Button closeBtn = dialog.findViewById(R.id.btn_close);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }
    private void setupListView(Dialog dialog, List<Withdrawal> withdrawalList) {
        WithdrawalAdapter adapter = new WithdrawalAdapter(dialog.getContext(),
                R.layout.withdrawldesign, withdrawalList, getLayoutInflater());

        ListView listView = dialog.findViewById(R.id.lv_withdrawal);
        listView.setAdapter(adapter);
    }
    private void showWithdrawals(@NonNull View view) {
        Dialog dialog = createWithdrawalDialog(getContext());

        TextView tvCardNumber = view.findViewById(R.id.tv_card_number);
         Withdrawal withdrawal = UtilitatiSingleton.withdrawalList.get(0);
        //Withdrawal withdrawal1 = UtilitatiSingleton.withdrawalList.get(1);

        tvCardNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupListView(dialog, Collections.singletonList(withdrawal));
                //setupListView(dialog, Collections.singletonList(withdrawal1));
                dialog.show();
            }
        });
    }

    private void showInformationsDialog(@NonNull View view) {

        dialog1 = new Dialog(getContext());
        dialog1.setContentView(R.layout.cardinfo_dialog);
        dialog1.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog1.setCancelable(true);
        //  dialog1.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;

        Button copyBtn = dialog1.findViewById(R.id.btn_copy);
        Button closeBtn = dialog1.findViewById(R.id.btn_close);

        TextView tvName = dialog1.findViewById(R.id.tv_name);
        TextView tvIban = dialog1.findViewById(R.id.tv_iban);
        TextView tvCurrency = dialog1.findViewById(R.id.tv_currency);
        TextView tvBank = dialog1.findViewById(R.id.tv_bank);

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                String informations = " Name:" + tvName.getText() + "\n IBAN:" + tvIban.getText() + "\n Currency: " + tvCurrency.getText() + "\n Bank:" + tvBank.getText();
                ClipData clip = ClipData.newPlainText("card info", informations);
                clipboard.setPrimaryClip(clip);


                Toast.makeText(getContext(), informations, Toast.LENGTH_SHORT).show();

            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog1.dismiss();
            }
        });

        View cardview = view.findViewById(R.id.cardview);
        User user1=UtilitatiSingleton.userList.get(0);
        Card card1=UtilitatiSingleton.cardList.get(0);
        cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //tvName.setText(" " + user1.getLastName() + " " + user1.getFirstName());
                tvIban.setText(" " + card1.getIBAN());
                tvCurrency.setText(" RON");
                tvBank.setText(" NeoBank");

                dialog1.show();
                return true;
            }
        });
    }

    private void showDeposits(@NonNull View view, ListView creator) {

        listView = view.findViewById(R.id.depositListView);

        depositAdapter = new DepositAdapter(view.getContext(),
                R.layout.depozit_adapter, UtilitatiSingleton.depositList, getLayoutInflater());

        listView.setAdapter(depositAdapter);

        ImageButton arrowBtn = view.findViewById(R.id.arrow);

        if (!areDepositsShown) {
            areDepositsShown = true;

            listView.setVisibility(View.VISIBLE);
            arrowBtn.setImageResource(R.drawable.arrow_up);
        } else if (areDepositsShown) {
            areDepositsShown = false;

            listView.setVisibility(View.GONE);
            arrowBtn.setImageResource(R.drawable.arrow);
        }
    }



    private void showDetails(@NonNull View view) {
        if (!areDetailsShown) {
            areDetailsShown = true;

            int cardIndex=0;
            Card card = UtilitatiSingleton.cardList.get(cardIndex);
            TextView cardNumber = view.findViewById(R.id.tv_card_number);
            cardNumber.setText(card.getNumber());

            TextView cvv = view.findViewById(R.id.tv_cvv);
            String cvv_string = String.valueOf(card.getCVV());
            cvv.setText(cvv_string);

        } else if (areDetailsShown) {
            areDetailsShown = false;

            TextView cardNumber = view.findViewById(R.id.tv_card_number);
            String text = card.getNumber().substring(card.getNumber().length() - 4);
            String message = "****  ****  ****  " + text;
            cardNumber.setText(message);

            TextView cvv = view.findViewById(R.id.tv_cvv);
            cvv.setText("***");

        }
    }

    private void getCurrentUser() {

        database.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UtilitatiSingleton.user = document.toObject(User.class);
                                findCardID();
                            }
                        } else {
                            user = UtilitatiSingleton.user;
                        }
                    }
                });
    }


    private void getTransactions() {
        //UtilitatiSingleton.fire();

        database.collection("Cards/" + "1234" + "/Transactions")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                       // UtilitatiSingleton.transactionList2.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Transaction transaction = document.toObject(Transaction.class);
                            UtilitatiSingleton.transactionList2 = new ArrayList<>();
                            UtilitatiSingleton.transactionList2.add(transaction);
                        }
                        Collections.sort(UtilitatiSingleton.transactionList2, Transaction.esteCronologic);
                    }
                });

    }

    private void findCardID() {

        findCardDetails(getView());

    }

    private void findCardDetails(@NonNull View view) {


        database.document("Cards/" + UtilitatiSingleton.user.getIDCard())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            //   for (QueryDocumentSnapshot document : task.getResult()) {

                            UtilitatiSingleton.card = task.getResult().toObject(Card.class);
                            fillCardWithDetails(view,0);
//                            if (UtilitatiSingleton.card.isBlocat()) {
//                                swBlocat.performClick();
//                            }

                            // }
                        } else {
                            user = UtilitatiSingleton.user;
                        }

                    }
                });


        getWithdrawals();
        getDeposits();
        getTransactions();
        getProviders();
}

    private void getProviders() {

        database.collection("Providers")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        UtilitatiSingleton.providersList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Provider provider = document.toObject(Provider.class);
                            UtilitatiSingleton.providersList.put(document.getId(), provider);
                        }
                    }
                });
    }


    private void getWithdrawals() {


        database.collection("Cards/" + UtilitatiSingleton.user.getIDCard() + "/Withdrawals")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        UtilitatiSingleton.withdrawalList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Withdrawal withdrawal = document.toObject(Withdrawal.class);
                            UtilitatiSingleton.withdrawalList.add(withdrawal);
                            Toast.makeText(getContext(), withdrawal.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getDeposits() {

        database.collection("Cards/" + UtilitatiSingleton.user.getIDCard() + "/Deposits")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        UtilitatiSingleton.depositList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Deposit deposit = document.toObject(Deposit.class);
                            UtilitatiSingleton.depositList.add(deposit);
                        }
                    }
                });

    }

    private void fillCardWithDetails(@NonNull View view, int cardIndex) {
        Card card = UtilitatiSingleton.cardList.get(cardIndex);

        TextView endDate = view.findViewById(R.id.tv_end_date);
        endDate.setText(card.getEndDate());

        TextView cardNumber = view.findViewById(R.id.tv_card_number);
        cardNumber.setText(card.getNumber());

        TextView owner = view.findViewById(R.id.tv_owner);
        owner.setText(card.getOwner());

        TextView balance = view.findViewById(R.id.textView14);
        String balanceText = card.getBalance() + " " + card.getCurrency();
        balance.setText(balanceText);
    }

}
