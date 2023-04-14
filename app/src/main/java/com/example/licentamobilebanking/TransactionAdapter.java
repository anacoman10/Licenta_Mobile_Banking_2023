package com.example.licentamobilebanking;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.licentamobilebanking.classes.cards.Transaction;

import java.util.List;

public class TransactionAdapter extends ArrayAdapter<Transaction> {
    private Context context;
    private List<Transaction> transactionList2;
    private LayoutInflater inflater;
    private int resource;
    private int pozitive;
Drawable dr;
    public TransactionAdapter(@NonNull Context context, int resource,
                              @NonNull List<Transaction> objects, LayoutInflater inflater, int pozitive, Drawable dr) {
        super(context, resource, objects);
        this.context = context;
        this.transactionList2 = objects;
        this.inflater = inflater;
        this.resource = resource;
        this.pozitive=pozitive;
        this.dr=dr;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(resource,parent,false);
        }
        Transaction transaction=transactionList2.get(position);

        if(transaction!=null){
            TextView trans_description=view.findViewById(R.id.textTitle);
            TextView trans_amount=view.findViewById(R.id.textDobanda);
            TextView trans_date=view.findViewById(R.id.textTimpRamas);

            trans_description.setText(transaction.getCardTo());
            trans_date.setText(transaction.getDate());
            trans_amount.setText(String.format("%.2f",pozitive*transaction.getAmount())+" RON");
            if(transaction.getAmount()>0) {
                trans_amount.setTextColor(Color.rgb(0, 124, 0));
            }
            else {
                trans_amount.setTextColor(Color.rgb(42, 92, 153));
            }
            if(dr!=null){
                AppCompatImageView img=view.findViewById(R.id.imgraportcateg);
                img.setImageDrawable(dr);
            }

        }

        return view;
    }
}
